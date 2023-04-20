package com.example.demo.service;

import com.example.demo.model.dto.CreateStoryDTO;
import com.example.demo.model.dto.StoryResponseDTO;
import com.example.demo.model.entity.*;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.demo.util.Constants.*;

@Service
@RequiredArgsConstructor
public class StoryService {
    private final String serverPort;
    private final FileService fileService;
    private final UserService userService;
    private final StoryRepository storyRepository;
    private final StoryContentRepository contentRepository;
    private final HashTagService hashTagService;
    private final JwtService jwtService;
    private final UserStoryReactionRepository userStoryReactionRepository;

    @Transactional
    public StoryResponseDTO createStory(CreateStoryDTO dto, String authToken) {
        long userId = jwtService.extractUserId(authToken);
        User user = userService.findUserById(userId);
        if (dto.content() == null) {
            throw new BadRequestException(STORY_CONTENT_IS_REQUIRED1);
        }
        Story story = new Story();
        story.setDateCreated(LocalDateTime.now());
        story.setExpirationDate(LocalDateTime.now().plusHours(24));
        story.setUser(user);
        hashTagService.addHashTags(dto.hashtags(), story);

        Story saved = storyRepository.save(story);

        for (MultipartFile file : dto.content()) {
            String fileName = fileService.saveFile(file, userId);
            StoryContent content = new StoryContent();
            content.setStory(story);
            content.setContentUrl(HTTP_LOCALHOST + serverPort + STORY_CONTENT + fileName);
            contentRepository.save(content);
        }
        return mapStoryToStoryResponseDTO(saved);
    }


    public List<String> getAllStoryUrls(long storyId) {
        List<StoryContent> storyContents = contentRepository.findAllByStoryId(storyId)
                .orElseThrow(() -> new BadRequestException(INVALID_STORY_ID));

        return storyContents.stream()
                .map(StoryContent::getContentUrl)
                .toList();
    }

    public File getContent(String fileName) {
        return fileService.getFile(fileName);
    }

    public Story findStoryById(long storyId) {
        return storyRepository.findById(storyId).orElseThrow(() -> new NotFoundException(STORY_NOT_FOUND));
    }

    @Transactional
    public void likeStory(String authToken, long storyId, boolean status) {
        long userId = jwtService.extractUserId(authToken);
        User user = userService.findUserById(userId);
        Story story = findStoryById(storyId);

        if (deleteReactionIfStatusMatches(userId, storyId, status)) {
            return;
        }
        UserStoryReaction userStoryReaction = UserStoryReaction.builder()
                .id(new UserStoryReactionKey(userId, storyId))
                .user(user)
                .story(story)
                .status(status)
                .build();
        userStoryReactionRepository.save(userStoryReaction);
    }

    private StoryResponseDTO mapStoryToStoryResponseDTO(Story saved) {
        return new StoryResponseDTO(saved.getId(),
                saved.getContentUrls().get(0).toString(),
                saved.getDateCreated(),
                saved.getExpirationDate(),
                saved.getHashtags().stream().map(Hashtag::getTagName).toList(),
                saved.getUserTags().stream().map(User::getUsername).toList());
    }

    private boolean deleteReactionIfStatusMatches(long userId, long storyId, boolean status) {
        Optional<UserStoryReaction> reaction = userStoryReactionRepository.findById(new UserStoryReactionKey(userId, storyId));
        if (reaction.isPresent() && reaction.get().isStatus() == status) {
            userStoryReactionRepository.delete(reaction.get());
            return true;
        }
        return false;
    }
}
