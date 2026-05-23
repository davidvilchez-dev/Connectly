package com.david.connectly.backend.mapper;

import com.david.connectly.backend.dto.response.LikeResponse;
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

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("LikeMapper - Pruebas unitarias")
class LikeMapperTest {

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @InjectMocks
    private LikeMapperImpl likeMapper;

    @Test
    @DisplayName("toResponse: mapea Like a LikeResponse correctamente")
    void toResponse_shouldMapLikeToLikeResponse() {
        User user = new User();
        user.setId(3L);
        user.setUsername("maria");

        Post post = new Post();
        post.setId(10L);

        Like like = new Like();
        like.setId(500L);
        like.setUser(user);
        like.setPost(post);
        like.setCreatedAt(LocalDateTime.now());

        LikeResponse result = likeMapper.toResponse(like);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(500L);
        assertThat(result.getPostId()).isEqualTo(10L);
        assertThat(result.getUser()).isNotNull();
        assertThat(result.getUser().getUsername()).isEqualTo("maria");
    }

    @Test
    @DisplayName("toResponse: retorna null si la entidad es null")
    void toResponse_shouldReturnNull_whenEntityIsNull() {
        assertThat(likeMapper.toResponse(null)).isNull();
    }
}
