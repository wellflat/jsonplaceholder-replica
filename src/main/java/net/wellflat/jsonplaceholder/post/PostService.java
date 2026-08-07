package net.wellflat.jsonplaceholder.post;

import net.wellflat.jsonplaceholder.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostMapper postMapper;

    public PostService(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    public long count(Long userId) {
        return postMapper.count(userId);
    }

    public List<Post> findPaged(Long userId, Integer page, Integer limit, String sort, String order) {
        Integer offset = null;
        if (page != null && limit != null) {
            offset = (page - 1) * limit;
        }
        return postMapper.findAllPaged(userId, offset, limit, sort, order);
    }

    public Post findById(Long id) {
        return postMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID: " + id + " の投稿が見つかりません"));
    }

    @Transactional
    public Post create(PostRequest request) {
        Post post = new Post(null, request.userId(), request.title(), request.body());
        postMapper.insert(post);
        return post;
    }

    @Transactional
    public Post update(Long id, PostRequest request) {
        // 存在確認 (存在しなければ ResourceNotFoundException)
        findById(id);

        Post updatedPost = new Post(id, request.userId(), request.title(), request.body());
        postMapper.update(updatedPost);
        return updatedPost;
    }

    @Transactional
    public Post patch(Long id, PostPatchRequest request) {
        // 存在確認 (存在しなければ ResourceNotFoundException)
        findById(id);

        postMapper.patch(id, request.userId(), request.title(), request.body());
        return findById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        int deletedRows = postMapper.deleteById(id);
        if (deletedRows == 0) {
            throw new ResourceNotFoundException("ID: " + id + " の投稿が見つかりません");
        }
    }
}
