package net.wellflat.jsonplaceholder.post;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PostMapper {

    long count(@Param("userId") Long userId);

    List<Post> findAllPaged(@Param("userId") Long userId,
                            @Param("offset") Integer offset,
                            @Param("limit") Integer limit,
                            @Param("sort") String sort,
                            @Param("order") String order);

    Optional<Post> findById(Long id);

    void insert(Post post);

    int update(Post post);

    int patch(@Param("id") Long id,
              @Param("userId") Long userId,
              @Param("title") String title,
              @Param("body") String body);

    int deleteById(Long id);
}
