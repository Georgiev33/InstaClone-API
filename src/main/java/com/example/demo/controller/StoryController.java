package com.example.demo.controller;

import com.example.demo.model.dto.ReactionResponseDTO;
import com.example.demo.model.dto.story.StoryResponseDTO;
import com.example.demo.model.dto.story.CreateStoryDTO;
import com.example.demo.service.contracts.StoryService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
@RestController
@RequestMapping("/story")
@RequiredArgsConstructor
public class StoryController {
    private final StoryService storyService;
    @GetMapping("/{storyId}")
    public StoryResponseDTO getStoryById(@PathVariable long storyId){
        return storyService.getStoryById(storyId);
    }
    @GetMapping("/user/{userId}")
    public Page<StoryResponseDTO> getPageOfStoriesForUser(@PathVariable long userId,
                                                          @RequestParam(required = false, defaultValue = "0") int page,
                                                          @RequestParam(required = false, defaultValue = "10") int size
                                                          ){
        return storyService.getPageOfStoriesForUser(userId, page, size);
    }
    @PostMapping
    public ResponseEntity<StoryResponseDTO> createStory(@ModelAttribute @Valid CreateStoryDTO dto,
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
    @GetMapping("/{storyId}/reactions")
    public Page<ReactionResponseDTO> getPageOfStoryReactions(
            @PathVariable long storyId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size){
        return storyService.getPageOfStoryReactions(storyId, page, size);
    }
    @PostMapping("/{storyId}/reactions")
    public void react(@PathVariable long storyId,
                          @RequestHeader("Authorization") String authToken,
                          @RequestParam boolean status) {
        storyService.react(authToken, storyId, status);
    }
    @DeleteMapping("/{storyId}")
    public void deleteStory(@PathVariable long storyId, @RequestHeader("Authorization") String authToken){
       storyService.deleteStory(storyId, authToken);
    }

}
