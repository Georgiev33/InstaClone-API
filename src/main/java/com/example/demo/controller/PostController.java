package com.example.demo.controller;

import com.example.demo.model.dto.CreatePostDTO;
import com.example.demo.model.dto.PostResponseDTO;
import com.example.demo.service.PostService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static com.example.demo.util.Constants.USER_ID;

@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    public PostController(@Autowired PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(@ModelAttribute CreatePostDTO dto, @RequestHeader("Authorization") String authToken) {
        PostResponseDTO responseDTO = postService.createPost(dto,authToken);
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
    @GetMapping("/kur")
    @SneakyThrows
    public String getPostMedia() {
        return "kurec";
    }
}
