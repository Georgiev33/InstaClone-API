package com.example.demo.service.contracts;

import com.example.demo.model.dto.ReactionResponseDTO;
import com.example.demo.model.dto.story.CreateStoryDTO;
import com.example.demo.model.dto.story.StoryResponseDTO;
import com.example.demo.model.exception.InvalidOwnerException;
import com.example.demo.model.exception.StoryNotFoundException;
import com.example.demo.model.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;

import static com.example.demo.util.constants.Constants.HOUR_IN_MILLISECONDS;

public interface StoryService {
    @Transactional
    StoryResponseDTO createStory(CreateStoryDTO dto, String authToken);

    File getContent(String fileName);

    @Transactional
    void react(String authToken, long storyId, boolean status);

    @Scheduled(fixedRate = HOUR_IN_MILLISECONDS)
    @Transactional
    void deleteExpiredStoriesEveryHour();

    Page<ReactionResponseDTO> getPageOfStoryReactions(long storyId, int page, int size) throws StoryNotFoundException;

    StoryResponseDTO getStoryById(long storyId) throws StoryNotFoundException;

    Page<StoryResponseDTO> getPageOfStoriesForUser(long userId, int page, int size) throws UserNotFoundException;

    void deleteStory(long storyId, String authToken) throws StoryNotFoundException, InvalidOwnerException;
}
