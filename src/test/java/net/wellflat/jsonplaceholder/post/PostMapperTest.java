package net.wellflat.jsonplaceholder.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostMapperTest {

    @Autowired
    private PostMapper postMapper;

    @Test
    @DisplayName("count: 総件数を取得できる")
    void testCount() {
        long total = postMapper.count(null);
        assertThat(total).isEqualTo(3);

        long user1Total = postMapper.count(1L);
        assertThat(user1Total).isEqualTo(2);
    }

    @Test
    @DisplayName("findAllPaged: ページネーションとソートを指定して取得できる")
    void testFindAllPaged() {
        List<Post> posts = postMapper.findAllPaged(null, 0, 2, "id", "asc");
        assertThat(posts).hasSize(2);
        assertThat(posts.get(0).getId()).isEqualTo(1L);
        assertThat(posts.get(1).getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("findById: ID指定で1件取得できる")
    void testFindById() {
        Optional<Post> post = postMapper.findById(1L);
        assertThat(post).isPresent();
        assertThat(post.get().getTitle()).isEqualTo("Welcome to JSONPlaceholder");
    }

    @Test
    @DisplayName("insert: 新規挿入され自動生成IDが設定される")
    void testInsert() {
        Post newPost = new Post(null, 1L, "New Title", "New Body");
        postMapper.insert(newPost);

        assertThat(newPost.getId()).isNotNull();
        assertThat(postMapper.count(null)).isEqualTo(4);
    }

    @Test
    @DisplayName("update: 投稿を更新できる")
    void testUpdate() {
        Post post = new Post(1L, 1L, "Updated Title", "Updated Body");
        int updatedRows = postMapper.update(post);

        assertThat(updatedRows).isEqualTo(1);
        Optional<Post> updated = postMapper.findById(1L);
        assertThat(updated).isPresent();
        assertThat(updated.get().getTitle()).isEqualTo("Updated Title");
    }

    @Test
    @DisplayName("deleteById: 投稿を削除できる")
    void testDeleteById() {
        int deletedRows = postMapper.deleteById(1L);
        assertThat(deletedRows).isEqualTo(1);
        assertThat(postMapper.findById(1L)).isEmpty();
    }
}
