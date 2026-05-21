package com.david.connectly.backend.service.impl;

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
import com.david.connectly.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    @Override
    public List<PostResponse> getFeed() {
        // Placeholder: por ahora feed = explore (ordenado por fecha)
        return explorePosts();
    }

    @Override
    public List<PostResponse> explorePosts() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        return postMapper.toResponse(post);
    }

    @Override
    public PostResponse createPost(PostRequest request) {
        String currentEmail = SecurityUtils.getCurrentUserEmail();
        if (currentEmail == null) {
            throw new IllegalArgumentException("Unauthorized");
        }

        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for authenticated principal"));

        Post post = postMapper.toEntity(request);
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(null);

        Post saved = postRepository.save(post);
        return postMapper.toResponse(saved);
    }

    @Override
    public void deletePost(Long id) {
        String currentEmail = SecurityUtils.getCurrentUserEmail();
        if (currentEmail == null) {
            throw new IllegalArgumentException("Unauthorized");
        }

        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for authenticated principal"));

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        if (post.getUser() == null || post.getUser().getId() == null || !post.getUser().getId().equals(user.getId())) {
            throw new ConflictException("You can only delete your own posts");
        }

        postRepository.delete(post);
    }
}