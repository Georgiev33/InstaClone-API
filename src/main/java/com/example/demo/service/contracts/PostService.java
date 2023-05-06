package com.example.demo.service.contracts;

import com.example.demo.model.dto.post.CreatePostDTO;
import com.example.demo.model.dto.post.PostResponseDTO;
import com.example.demo.model.entity.post.Post;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.io.File;
import java.util.List;

public interface PostService {
    @Transactional
    PostResponseDTO createPost(@Valid CreatePostDTO dto, String authToken);

    Page<PostResponseDTO> getFeed(String authToken, int page, int size);

    List<String> getAllPostUrls(long postId);

    File getContent(String fileName);

    Post findPostById(long postId);

    @Transactional
    void react(String authToken, long postId, boolean status);

    Page<PostResponseDTO> getAllUserPosts(long userId, int page, int size);
}
