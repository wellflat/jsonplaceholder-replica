package net.wellflat.jsonplaceholder.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /posts で全件取得と X-Total-Count ヘッダーが取得できる")
    void testGetAllPosts() throws Exception {
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "3"))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("GET /posts?_page=1&_limit=2 でページネーションが動作する")
    void testGetPostsPaged() throws Exception {
        mockMvc.perform(get("/posts")
                        .param("_page", "1")
                        .param("_limit", "2"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "3"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @DisplayName("GET /posts?_page=2&_limit=2 で2ページ目が取得できる")
    void testGetPostsPagedSecondPage() throws Exception {
        mockMvc.perform(get("/posts")
                        .param("_page", "2")
                        .param("_limit", "2"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "3"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(3));
    }

    @Test
    @DisplayName("GET /posts?_sort=title&_order=desc でソートが動作する")
    void testGetPostsSorted() throws Exception {
        mockMvc.perform(get("/posts")
                        .param("_sort", "title")
                        .param("_order", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("sunt aut facere repeat..."))
                .andExpect(jsonPath("$[1].title").value("qui est esse"))
                .andExpect(jsonPath("$[2].title").value("ea molestias quasi"));
    }

    @Test
    @DisplayName("GET /posts?userId=1 で指定ユーザーの投稿のみ取得と X-Total-Count が一致する")
    void testGetPostsByUserId() throws Exception {
        mockMvc.perform(get("/posts").param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /posts/{id} で1件取得できる")
    void testGetPostById() throws Exception {
        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("sunt aut facere repeat..."));
    }

    @Test
    @DisplayName("GET /posts/{id} 存在しないIDの場合は404 ProblemDetailが返る")
    void testGetPostByIdNotFound() throws Exception {
        mockMvc.perform(get("/posts/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("ID: 999 の投稿が見つかりません"));
    }

    @Test
    @DisplayName("POST /posts で正常なパラメータなら新規作成できる")
    void testCreatePostSuccess() throws Exception {
        String json = """
                {
                    "userId": 1,
                    "title": "New Post Title",
                    "body": "New Post Body"
                }
                """;

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.title").value("New Post Title"));
    }

    @Test
    @DisplayName("POST /posts で不正なパラメータ(titleが空)の場合は400エラー(ProblemDetail)")
    void testCreatePostValidationError() throws Exception {
        String json = """
                {
                    "userId": 1,
                    "title": "",
                    "body": "New Post Body"
                }
                """;

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.invalidFields.title").value("titleは必須です"));
    }

    @Test
    @DisplayName("PUT /posts/{id} で全体更新できる")
    void testUpdatePost() throws Exception {
        String json = """
                {
                    "userId": 1,
                    "title": "Updated Title",
                    "body": "Updated Body"
                }
                """;

        mockMvc.perform(put("/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    @DisplayName("PATCH /posts/{id} で指定されたフィールド(titleのみ)を部分更新できる")
    void testPatchPostSuccess() throws Exception {
        String json = """
                {
                    "title": "Patched Title Only"
                }
                """;

        mockMvc.perform(patch("/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1)) // 元のまま保持
                .andExpect(jsonPath("$.title").value("Patched Title Only"))
                .andExpect(jsonPath("$.body").value("quia et suscipit...")); // 元のまま保持
    }

    @Test
    @DisplayName("DELETE /posts/{id} で削除できる")
    void testDeletePost() throws Exception {
        mockMvc.perform(delete("/posts/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /posts/{id} 存在しないIDの場合は404 ProblemDetailが返る")
    void testDeletePostNotFound() throws Exception {
        mockMvc.perform(delete("/posts/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("ID: 999 の投稿が見つかりません"));
    }
}
