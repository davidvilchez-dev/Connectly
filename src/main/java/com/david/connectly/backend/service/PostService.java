package com.david.connectly.backend.service;

import com.david.connectly.backend.dto.request.PostRequest;
import com.david.connectly.backend.dto.response.PostResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    List<PostResponse> getFeed();

    List<PostResponse> explorePosts();

    PostResponse getPostById(Long id);

    PostResponse createPost(PostRequest request);

    PostResponse createPost(String content, MultipartFile image);

    void deletePost(Long id);
}
