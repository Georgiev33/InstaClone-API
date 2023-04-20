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
import java.util.List;


@RestController
@RequestMapping("/story")
@RequiredArgsConstructor
public class StoryController {
    private final StoryService storyService;


    @PostMapping
    public ResponseEntity<StoryResponseDTO> createStory(@ModelAttribute CreateStoryDTO dto,
                                                      @RequestHeader("Authorization") String authToken) {
        StoryResponseDTO responseDTO = storyService.createStory(dto,authToken);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/media/{storyId}")
    public List<String> getStoryMedia(@PathVariable long storyId) {
        return storyService.getAllStoryUrls(storyId);
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
