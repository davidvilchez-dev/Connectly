package com.david.connectly.backend.service;

import com.david.connectly.backend.dto.request.PostRequest;
import com.david.connectly.backend.dto.response.PostResponse;
import com.david.connectly.backend.entity.Post;
import com.david.connectly.backend.entity.User;
import com.david.connectly.backend.exception.ConflictException;
import com.david.connectly.backend.exception.ResourceNotFoundException;
import com.david.connectly.backend.mapper.PostMapper;
import com.david.connectly.backend.repository.PostRepository;
import com.david.connectly.backend.repository.UserRepository;
import com.david.connectly.backend.security.SecurityUtils;
import com.david.connectly.backend.service.impl.PostServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostServiceImpl - Pruebas unitarias")
class PostServiceImplTest {

    @Mock private PostRepository postRepository;
    @Mock private UserRepository userRepository;
    @Mock private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    private User user;
    private Post post;
    private PostResponse postResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("david@test.com");

        post = new Post();
        post.setId(10L);
        post.setUser(user);

        postResponse = new PostResponse();

        // Simula usuario autenticado en el SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("david@test.com", null, List.of())
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ─── GET POST BY ID ───────────────────────────────────────────────────────

    @Test
    @DisplayName("getPostById: retorna PostResponse cuando el post existe")
    void getPostById_shouldReturnPost_whenExists() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        PostResponse result = postService.getPostById(10L);

        assertThat(result).isEqualTo(postResponse);
        verify(postRepository).findById(10L);
    }

    @Test
    @DisplayName("getPostById: lanza ResourceNotFoundException cuando no existe")
    void getPostById_shouldThrow_whenNotFound() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPostById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ─── EXPLORE / FEED ───────────────────────────────────────────────────────

    @Test
    @DisplayName("explorePosts: retorna lista de posts ordenados por fecha")
    void explorePosts_shouldReturnAllPostsOrderedByDate() {
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(post));
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        List<PostResponse> result = postService.explorePosts();

        assertThat(result).hasSize(1).contains(postResponse);
    }

    @Test
    @DisplayName("getFeed: delega a explorePosts y retorna su resultado")
    void getFeed_shouldDelegateToExplorePosts() {
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(post));
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        List<PostResponse> result = postService.getFeed();

        assertThat(result).hasSize(1);
    }

    // ─── CREATE POST ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("createPost: crea post exitosamente para el usuario autenticado")
    void createPost_shouldCreateAndReturnPost_whenAuthenticated() {
        PostRequest request = new PostRequest();

        when(userRepository.findByEmail("david@test.com")).thenReturn(Optional.of(user));
        when(postMapper.toEntity(request)).thenReturn(post);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        PostResponse result = postService.createPost(request);

        assertThat(result).isEqualTo(postResponse);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("createPost: lanza excepción si no hay usuario autenticado")
    void createPost_shouldThrow_whenNotAuthenticated() {
        SecurityContextHolder.clearContext();

        try (MockedStatic<SecurityUtils> utils = mockStatic(SecurityUtils.class)) {
            utils.when(SecurityUtils::getCurrentUserEmail).thenReturn(null);

            PostRequest request = new PostRequest();

            assertThatThrownBy(() -> postService.createPost(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Unauthorized");
        }
    }

    // ─── DELETE POST ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("deletePost: elimina el post cuando pertenece al usuario autenticado")
    void deletePost_shouldDelete_whenPostBelongsToUser() {
        when(userRepository.findByEmail("david@test.com")).thenReturn(Optional.of(user));
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        assertThatNoException().isThrownBy(() -> postService.deletePost(10L));
        verify(postRepository).delete(post);
    }

    @Test
    @DisplayName("deletePost: lanza ConflictException si el post es de otro usuario")
    void deletePost_shouldThrow_whenPostBelongsToOtherUser() {
        User otherUser = new User();
        otherUser.setId(99L);
        post.setUser(otherUser);

        when(userRepository.findByEmail("david@test.com")).thenReturn(Optional.of(user));
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.deletePost(10L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("your own posts");

        verify(postRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deletePost: lanza ResourceNotFoundException si el post no existe")
    void deletePost_shouldThrow_whenPostNotFound() {
        when(userRepository.findByEmail("david@test.com")).thenReturn(Optional.of(user));
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.deletePost(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
