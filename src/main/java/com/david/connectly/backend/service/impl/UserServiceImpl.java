package com.david.connectly.backend.service.impl;

import com.david.connectly.backend.dto.request.UpdateProfileRequest;
import com.david.connectly.backend.dto.response.PostResponse;
import com.david.connectly.backend.dto.response.UserResponse;
import com.david.connectly.backend.entity.Follow;
import com.david.connectly.backend.entity.User;
import com.david.connectly.backend.exception.ConflictException;
import com.david.connectly.backend.exception.ResourceNotFoundException;
import com.david.connectly.backend.mapper.PostMapper;
import com.david.connectly.backend.mapper.UserMapper;
import com.david.connectly.backend.repository.FollowRepository;
import com.david.connectly.backend.repository.PostRepository;
import com.david.connectly.backend.repository.UserRepository;
import com.david.connectly.backend.security.SecurityUtils;
import com.david.connectly.backend.service.CloudinaryService;
import com.david.connectly.backend.service.UserService;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    public UserResponse getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    public List<PostResponse> getUserPosts(Long id) {
        // 404 si el usuario no existe
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return postRepository.findAllByUserIdOrderByCreatedAtDesc(id)
                .stream()
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUserFollowers(Long id) {
        // 404 si el usuario no existe
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        List<Follow> followers = followRepository.findAllByFollowingId(id);
        return followers.stream()
                .map(Follow::getFollower)
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUserFollowing(Long id) {
        // 404 si el usuario no existe
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        List<Follow> following = followRepository.findAllByFollowerId(id);
        return following.stream()
                .map(Follow::getFollowing)
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateProfile(Long id, UpdateProfileRequest request) {
        // Solo el usuario autenticado puede actualizar SU perfil
        String currentEmail = SecurityUtils.getCurrentUserEmail();
        if (currentEmail == null) {
            throw new IllegalArgumentException("Unauthorized");
        }

        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for authenticated principal"));
        if (!currentUser.getId().equals(id)) {
            throw new ConflictException("You can only update your own profile");
        }

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            userRepository.findByUsername(request.getUsername())
                    .filter(u -> !u.getId().equals(currentUser.getId()))
                    .ifPresent(u -> {
                        throw new ConflictException("Username is already taken");
                    });
            currentUser.setUsername(request.getUsername());
        }
        if (request.getBio() != null) {
            currentUser.setBio(request.getBio());
        }
        if (request.getAvatarUrl() != null) {
            currentUser.setAvatarUrl(request.getAvatarUrl());
        }
        currentUser.setUpdatedAt(LocalDateTime.now());

        User saved = userRepository.save(currentUser);
        return userMapper.toResponse(saved);
    }

    @Override
    public UserResponse uploadAvatar(Long id, MultipartFile file) {
        String currentEmail = SecurityUtils.getCurrentUserEmail();
        if (currentEmail == null) {
            throw new IllegalArgumentException("Unauthorized");
        }

        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for authenticated principal"));
        if (!currentUser.getId().equals(id)) {
            throw new ConflictException("You can only update your own avatar");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        String avatarUrl = cloudinaryService.uploadImage(file, "avatars");
        currentUser.setAvatarUrl(avatarUrl);
        currentUser.setUpdatedAt(java.time.LocalDateTime.now());

        User saved = userRepository.save(currentUser);
        return userMapper.toResponse(saved);
    }
}