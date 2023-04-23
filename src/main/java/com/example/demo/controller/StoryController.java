package com.example.demo.controller;

import com.example.demo.model.dto.StoryResponseDTO;
import com.example.demo.model.dto.CreateStoryDTO;
import com.example.demo.service.StoryService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
@RestController
@RequestMapping("/story")
@RequiredArgsConstructor
public class StoryController {
    private final StoryService storyService;

    @PostMapping
    public ResponseEntity<StoryResponseDTO> createStory(@ModelAttribute CreateStoryDTO dto,
                                                        @RequestHeader("Authorization") String authToken) {
        return ResponseEntity.ok(storyService.createStory(dto, authToken));
    }

    @GetMapping("/content/{fileName}")
    @SneakyThrows
    public void getStoryMedia(@PathVariable String fileName, HttpServletResponse response) {
        File file = storyService.getContent(fileName);
        response.setContentType(Files.probeContentType(file.toPath()));
        Files.copy(file.toPath(), response.getOutputStream());
    }

    @PostMapping("/react/{storyId}")
    public void likeStory(@PathVariable long storyId,
                          @RequestHeader("Authorization") String authToken,
                          @RequestParam boolean status) {
        storyService.likeStory(authToken, storyId, status);
    }
}