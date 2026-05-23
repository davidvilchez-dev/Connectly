package com.david.connectly.backend.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA Entities - Pruebas de getters, setters y constructores")
class EntityTest {

    @Test
    @DisplayName("User: prueba de todos los campos de entidad")
    void testUserEntity() {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setEmail("email@test.com");
        user.setPassword("password");
        user.setBio("my bio");
        user.setAvatarUrl("http://avatar");
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        List<Post> posts = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();
        List<Like> likes = new ArrayList<>();

        user.setPosts(posts);
        user.setComments(comments);
        user.setLikes(likes);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("username");
        assertThat(user.getEmail()).isEqualTo("email@test.com");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.getBio()).isEqualTo("my bio");
        assertThat(user.getAvatarUrl()).isEqualTo("http://avatar");
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
        assertThat(user.getPosts()).isSameAs(posts);
        assertThat(user.getComments()).isSameAs(comments);
        assertThat(user.getLikes()).isSameAs(likes);
    }

    @Test
    @DisplayName("Post: prueba de todos los campos de entidad")
    void testPostEntity() {
        Post post = new Post();
        post.setId(2L);
        post.setContent("content");
        post.setImageUrl("http://image");
        LocalDateTime now = LocalDateTime.now();
        post.setCreatedAt(now);
        post.setUpdatedAt(now);

        User user = new User();
        post.setUser(user);

        List<Comment> comments = new ArrayList<>();
        List<Like> likes = new ArrayList<>();
        post.setComments(comments);
        post.setLikes(likes);

        assertThat(post.getId()).isEqualTo(2L);
        assertThat(post.getContent()).isEqualTo("content");
        assertThat(post.getImageUrl()).isEqualTo("http://image");
        assertThat(post.getCreatedAt()).isEqualTo(now);
        assertThat(post.getUpdatedAt()).isEqualTo(now);
        assertThat(post.getUser()).isSameAs(user);
        assertThat(post.getComments()).isSameAs(comments);
        assertThat(post.getLikes()).isSameAs(likes);
    }

    @Test
    @DisplayName("Comment: prueba de todos los campos de entidad")
    void testCommentEntity() {
        Comment comment = new Comment();
        comment.setId(3L);
        comment.setContent("my comment");
        LocalDateTime now = LocalDateTime.now();
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);

        User user = new User();
        Post post = new Post();
        comment.setUser(user);
        comment.setPost(post);

        assertThat(comment.getId()).isEqualTo(3L);
        assertThat(comment.getContent()).isEqualTo("my comment");
        assertThat(comment.getCreatedAt()).isEqualTo(now);
        assertThat(comment.getUpdatedAt()).isEqualTo(now);
        assertThat(comment.getUser()).isSameAs(user);
        assertThat(comment.getPost()).isSameAs(post);
    }

    @Test
    @DisplayName("Like: prueba de todos los campos de entidad")
    void testLikeEntity() {
        Like like = new Like();
        like.setId(4L);
        LocalDateTime now = LocalDateTime.now();
        like.setCreatedAt(now);

        User user = new User();
        Post post = new Post();
        like.setUser(user);
        like.setPost(post);

        assertThat(like.getId()).isEqualTo(4L);
        assertThat(like.getCreatedAt()).isEqualTo(now);
        assertThat(like.getUser()).isSameAs(user);
        assertThat(like.getPost()).isSameAs(post);
    }

    @Test
    @DisplayName("Follow: prueba de todos los campos de entidad")
    void testFollowEntity() {
        Follow follow = new Follow();
        follow.setId(5L);
        LocalDateTime now = LocalDateTime.now();
        follow.setCreatedAt(now);

        User follower = new User();
        User following = new User();
        follow.setFollower(follower);
        follow.setFollowing(following);

        assertThat(follow.getId()).isEqualTo(5L);
        assertThat(follow.getCreatedAt()).isEqualTo(now);
        assertThat(follow.getFollower()).isSameAs(follower);
        assertThat(follow.getFollowing()).isSameAs(following);
    }
}
