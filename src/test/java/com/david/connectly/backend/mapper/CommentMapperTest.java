package com.david.connectly.backend.mapper;

import com.david.connectly.backend.dto.request.CommentRequest;
import com.david.connectly.backend.dto.response.CommentResponse;
import com.david.connectly.backend.entity.Comment;
import com.david.connectly.backend.entity.Post;
import com.david.connectly.backend.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentMapper - Pruebas unitarias")
class CommentMapperTest {

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @InjectMocks
    private CommentMapperImpl commentMapper;

    @Test
    @DisplayName("toEntity: mapea CommentRequest a Comment correctamente")
    void toEntity_shouldMapCommentRequestToComment() {
        CommentRequest request = new CommentRequest();
        request.setContent("This is a comment");

        Comment result = commentMapper.toEntity(request);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("This is a comment");
    }

    @Test
    @DisplayName("toEntity: retorna null si la petición es null")
    void toEntity_shouldReturnNull_whenRequestIsNull() {
        assertThat(commentMapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("toResponse: mapea Comment a CommentResponse correctamente")
    void toResponse_shouldMapCommentToCommentResponse() {
        User user = new User();
        user.setId(2L);
        user.setUsername("juan");

        Post post = new Post();
        post.setId(10L);

        Comment comment = new Comment();
        comment.setId(100L);
        comment.setContent("Awesome post");
        comment.setUser(user);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        CommentResponse result = commentMapper.toResponse(comment);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getContent()).isEqualTo("Awesome post");
        assertThat(result.getPostId()).isEqualTo(10L);
        assertThat(result.getUser()).isNotNull();
        assertThat(result.getUser().getUsername()).isEqualTo("juan");
    }

    @Test
    @DisplayName("toResponse: retorna null si la entidad es null")
    void toResponse_shouldReturnNull_whenEntityIsNull() {
        assertThat(commentMapper.toResponse(null)).isNull();
    }
}
