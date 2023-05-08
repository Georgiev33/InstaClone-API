package com.example.demo.service.contracts;

import com.example.demo.model.dto.CreateStoryDTO;
import com.example.demo.model.dto.StoryResponseDTO;
import jakarta.transaction.Transactional;
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
}
