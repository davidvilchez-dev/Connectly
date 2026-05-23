package com.david.connectly.backend.mapper;

import com.david.connectly.backend.dto.request.PostRequest;
import com.david.connectly.backend.dto.response.PostResponse;
import com.david.connectly.backend.entity.Comment;
import com.david.connectly.backend.entity.Like;
import com.david.connectly.backend.entity.Post;
import com.david.connectly.backend.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostMapper - Pruebas unitarias")
class PostMapperTest {

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @InjectMocks
    private PostMapperImpl postMapper;

    @Test
    @DisplayName("toEntity: mapea PostRequest a Post correctamente")
    void toEntity_shouldMapPostRequestToPost() {
        PostRequest request = new PostRequest();
        request.setContent("This is a post");
        request.setImageUrl("http://image.url/1");

        Post result = postMapper.toEntity(request);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("This is a post");
        assertThat(result.getImageUrl()).isEqualTo("http://image.url/1");
    }

    @Test
    @DisplayName("toEntity: retorna null si la petición es null")
    void toEntity_shouldReturnNull_whenRequestIsNull() {
        assertThat(postMapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("toResponse: mapea Post a PostResponse correctamente")
    void toResponse_shouldMapPostToPostResponse() {
        User user = new User();
        user.setId(1L);
        user.setUsername("david");

        Post post = new Post();
        post.setId(10L);
        post.setContent("Hello connectly");
        post.setImageUrl("http://images/1");
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        // Comments y Likes nulos
        post.setComments(null);
        post.setLikes(null);

        PostResponse result1 = postMapper.toResponse(post);

        assertThat(result1).isNotNull();
        assertThat(result1.getId()).isEqualTo(10L);
        assertThat(result1.getContent()).isEqualTo("Hello connectly");
        assertThat(result1.getImageUrl()).isEqualTo("http://images/1");
        assertThat(result1.getCommentsCount()).isZero();
        assertThat(result1.getLikesCount()).isZero();
        assertThat(result1.getUser()).isNotNull();
        assertThat(result1.getUser().getUsername()).isEqualTo("david");

        // Comments y Likes poblados
        post.setComments(List.of(new Comment(), new Comment()));
        post.setLikes(List.of(new Like()));

        PostResponse result2 = postMapper.toResponse(post);

        assertThat(result2.getCommentsCount()).isEqualTo(2);
        assertThat(result2.getLikesCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("toResponse: retorna null si la entidad es null")
    void toResponse_shouldReturnNull_whenEntityIsNull() {
        assertThat(postMapper.toResponse(null)).isNull();
    }
}
