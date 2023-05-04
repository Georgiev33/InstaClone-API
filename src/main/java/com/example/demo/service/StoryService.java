package com.example.demo.service;

import com.example.demo.model.dto.CreateStoryDTO;
import com.example.demo.model.dto.StoryResponseDTO;
import com.example.demo.model.entity.*;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.repository.*;
import com.example.demo.service.contracts.UserValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.util.Constants.*;

@Service
@RequiredArgsConstructor
public class StoryService {
    private final FileService fileService;
    private final UserValidationService userValidationService;
    private final StoryRepository storyRepository;
    private final NotificationService notificationService;
    private final HashTagService hashTagService;
    private final JwtService jwtService;
    private final UserStoryReactionRepository userStoryReactionRepository;

    @Transactional
    public StoryResponseDTO createStory(CreateStoryDTO dto, String authToken) {
        long userId = jwtService.extractUserId(authToken);
        User user = userValidationService.findUserById(userId);
        Story story = Story.builder()
                .dateCreated(LocalDateTime.now())
                .expirationDate(LocalDateTime.now().plusHours(24))
                .user(user)
                .hashtags(new HashSet<>())
                .userTags(addPersonTags(dto.personTags(), user))
                .contentUrl(fileService.createContent(dto.content(), userId))
                .build();
        hashTagService.addHashTags(dto.hashTags(), story);
        Story saved = storyRepository.save(story);

        return mapStoryToStoryResponseDTO(saved);
    }

    public File getContent(String fileName) {
        return fileService.getFile(fileName);
    }

    private Story findStoryById(long storyId) {
        return storyRepository.findById(storyId).orElseThrow(() -> new NotFoundException(STORY_NOT_FOUND));
    }

    @Transactional
    public void react(String authToken, long storyId, boolean status) {
        long userId = jwtService.extractUserId(authToken);
        User user = userValidationService.findUserById(userId);
        Story story = findStoryById(storyId);

        if (deleteReactionIfStatusMatches(userId, storyId, status)) {
            return;
        }
        UserStoryReaction userStoryReaction = UserStoryReaction.builder()
                .id(new UserStoryReaction.UserStoryReactionKey(userId, storyId))
                .user(user)
                .story(story)
                .status(status)
                .build();
        notificationService.addNotification(story.getUser(), user.getUsername() + " liked your story.");
        userStoryReactionRepository.save(userStoryReaction);
    }
    @Scheduled(fixedRate = 1000 )
    @Transactional
    public void deleteExpiredStoriesEveryHour(){
        storyRepository.deleteAllByExpirationDateBefore(LocalDateTime.now());
    }

    private StoryResponseDTO mapStoryToStoryResponseDTO(Story saved) {
        return new StoryResponseDTO(saved.getId(),
                saved.getContentUrl(),
                saved.getDateCreated(),
                saved.getExpirationDate(),
                saved.getHashtags().stream().map(Hashtag::getTagName).toList(),
                saved.getUserTags().stream().map(User::getUsername).toList());
    }

    private boolean deleteReactionIfStatusMatches(long userId, long storyId, boolean status) {
        Optional<UserStoryReaction> reaction =
                userStoryReactionRepository.findById(new UserStoryReaction.UserStoryReactionKey(userId, storyId));
        if (reaction.isPresent() && reaction.get().isStatus() == status) {
            userStoryReactionRepository.delete(reaction.get());
            return true;
        }
        return false;
    }

    private Set<User> addPersonTags(Optional<List<String>> users, User creator) {
        if (users.isEmpty()) return Collections.emptySet();
        Set<User> userList = users.get()
                .stream()
                .map(userValidationService::getUserOrThrowException)
                .collect(Collectors.toSet());
        notificationService.addNotification(userList, creator.getUsername() + TAGGED_YOU_IN_HIS_STORY);
        return userList;
    }
}
