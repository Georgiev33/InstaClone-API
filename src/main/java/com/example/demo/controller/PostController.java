package com.example.demo.controller;

import com.example.demo.model.dto.post.CreatePostDTO;
import com.example.demo.model.dto.post.PostResponseDTO;
import com.example.demo.service.contracts.PostService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;


@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(@ModelAttribute @Valid CreatePostDTO dto,
                                                      @RequestHeader("Authorization") String authToken) {
        PostResponseDTO responseDTO = postService.createPost(dto, authToken);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/media/{postId}")
    public List<String> getPostMedia(@PathVariable long postId) {
        return postService.getAllPostUrls(postId);
    }

    @GetMapping("/content/{fileName}")
    @SneakyThrows
    public void getPostMedia(@PathVariable String fileName, HttpServletResponse response) {
        File file = postService.getContent(fileName);
        response.setContentType(Files.probeContentType(file.toPath()));
        Files.copy(file.toPath(), response.getOutputStream());
    }

    @PostMapping("/{postId}/reactions")
    public void react(@PathVariable long postId,
                         @RequestHeader("Authorization") String authToken,
                         @RequestParam boolean status) {
        postService.react(authToken, postId, status);
    }
    @DeleteMapping("/{postId}")
    public void deletePostById(@PathVariable long postId, @RequestHeader("Authorization") String authToken){
        postService.deletePostById(postId, authToken);
    }
}
