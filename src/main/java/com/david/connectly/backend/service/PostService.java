package com.david.connectly.backend.service;

import com.david.connectly.backend.dto.request.PostRequest;
import com.david.connectly.backend.dto.response.PostResponse;

import java.util.List;

public interface PostService {
    List<PostResponse> getFeed();

    List<PostResponse> explorePosts();

    PostResponse getPostById(Long id);

    PostResponse createPost(PostRequest request);

    void deletePost(Long id);
}
