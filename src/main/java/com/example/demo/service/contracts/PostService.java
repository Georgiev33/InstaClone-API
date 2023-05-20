package com.example.demo.service.contracts;

import com.example.demo.model.dto.post.CreatePostDTO;
import com.example.demo.model.dto.ReactionResponseDTO;
import com.example.demo.model.dto.post.PostResponseDTO;
import com.example.demo.model.entity.post.Post;
import com.example.demo.model.exception.FileNotFoundException;
import com.example.demo.model.exception.InvalidOwnerException;
import com.example.demo.model.exception.PostNotFoundException;
import com.example.demo.model.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.io.File;
import java.util.List;

public interface PostService {
    @Transactional
    PostResponseDTO createPost(@Valid CreatePostDTO dto, String authToken);

    Page<PostResponseDTO> getFeed(String authToken, int page, int size);

    List<String> getAllPostUrls(long postId) throws PostNotFoundException;

    File getContent(String fileName) throws FileNotFoundException;

    Post findPostById(long postId) throws PostNotFoundException;

    @Transactional
    void react(String authToken, long postId, boolean status);

    Page<PostResponseDTO> getAllUserPosts(long userId, int page, int size) throws UserNotFoundException;

    void deletePostById(long postId, String authToken) throws InvalidOwnerException, PostNotFoundException;

    PostResponseDTO getPostById(long postId);

    Page<ReactionResponseDTO> getPageOfPostReactions(long postId, int page, int size) throws PostNotFoundException;
}
