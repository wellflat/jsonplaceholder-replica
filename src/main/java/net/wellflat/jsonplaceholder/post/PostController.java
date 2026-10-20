package net.wellflat.jsonplaceholder.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Posts API", description = "投稿リソースの取得・作成・更新・削除API")
@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * 全件取得・フィルタリング・ページネーション・ソート (GET /posts)
     */
    @Operation(summary = "投稿一覧の取得", description = "クエリパラメータでユーザーフィルタ・ページネーション・ソートの指定が可能です。")
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts(
            @Parameter(description = "ユーザーIDによるフィルタ")
            @RequestParam(required = false) Long userId,

            @Parameter(description = "ページ番号 (1〜)")
            @RequestParam(name = "_page", required = false) Integer page,

            @Parameter(description = "1ページあたりの取得件数")
            @RequestParam(name = "_limit", required = false) Integer limit,

            @Parameter(description = "ソート項目 (id, title, userId)")
            @RequestParam(name = "_sort", required = false, defaultValue = "id") String sort,

            @Parameter(description = "順序 (asc, desc)")
            @RequestParam(name = "_order", required = false, defaultValue = "asc") String order) {

        log.info("GET /posts called with userId={}, page={}, limit={}, sort={}, order={}",
                userId, page, limit, sort, order);

        long totalCount = postService.count(userId);
        List<Post> posts = postService.findPaged(userId, page, limit, sort, order);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(totalCount))
                .header("Access-Control-Expose-Headers", "X-Total-Count")
                .body(posts);
    }

    /**
     * IDによる1件取得 (GET /posts/{id})
     */
    @Operation(summary = "投稿の1件取得", description = "指定したIDの投稿詳細を取得します。")
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(
            @Parameter(description = "投稿ID") @PathVariable Long id) {
        log.info("GET /posts/{} called", id);
        Post post = postService.findById(id);
        return ResponseEntity.ok(post);
    }

    /**
     * 新規作成 (POST /posts)
     */
    @Operation(summary = "投稿の新規作成", description = "新しい投稿を登録します。")
    @PostMapping
    public ResponseEntity<Post> createPost(@Valid @RequestBody PostRequest request) {
        log.info("POST /posts called with userId={}, title={}", request.userId(), request.title());
        Post createdPost = postService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    /**
     * 更新 (PUT /posts/{id})
     */
    @Operation(summary = "投稿の全体更新 (PUT)", description = "指定したIDの投稿情報を全件置換で更新します。")
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @Parameter(description = "投稿ID") @PathVariable Long id,
            @Valid @RequestBody PostRequest request) {
        log.info("PUT /posts/{} called with userId={}, title={}", id, request.userId(), request.title());
        Post updatedPost = postService.update(id, request);
        return ResponseEntity.ok(updatedPost);
    }

    /**
     * 部分更新 (PATCH /posts/{id})
     */
    @Operation(summary = "投稿の一部更新 (PATCH)", description = "リクエストに含まれる項目のみ動的に更新します。")
    @PatchMapping("/{id}")
    public ResponseEntity<Post> patchPost(
            @Parameter(description = "投稿ID") @PathVariable Long id,
            @Valid @RequestBody PostPatchRequest request) {
        log.info("PATCH /posts/{} called with userId={}, title={}", id, request.userId(), request.title());
        Post patchedPost = postService.patch(id, request);
        return ResponseEntity.ok(patchedPost);
    }

    /**
     * 削除 (DELETE /posts/{id})
     */
    @Operation(summary = "投稿の削除", description = "指定したIDの投稿を削除します。")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "投稿ID") @PathVariable Long id) {
        log.info("DELETE /posts/{} called", id);
        postService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
