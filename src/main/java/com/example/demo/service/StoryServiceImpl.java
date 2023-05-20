package com.example.demo.service;

import com.example.demo.model.dto.ReactionResponseDTO;
import com.example.demo.model.dto.story.CreateStoryDTO;
import com.example.demo.model.dto.story.StoryResponseDTO;
import com.example.demo.model.entity.*;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.exception.StoryNotFoundException;
import com.example.demo.repository.*;
import com.example.demo.service.contracts.*;
import com.example.demo.util.constants.MessageConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.util.constants.Constants.*;
import static com.example.demo.util.constants.MessageConstants.STORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {

    private final FileService fileService;
    private final UserValidationService userValidationService;
    private final StoryRepository storyRepository;
    private final NotificationService notificationService;
    private final HashTagService hashTagService;
    private final JwtService jwtService;
    private final UserStoryReactionRepository userStoryReactionRepository;

    @Override
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

    @Override
    public File getContent(String fileName) {
        return fileService.getFile(fileName);
    }

    private Story findStoryById(long storyId) throws StoryNotFoundException{
        return storyRepository.findById(storyId).orElseThrow(() -> new StoryNotFoundException(STORY_NOT_FOUND));
    }

    @Override
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
    @Override
    @Scheduled(fixedRate = HOUR_IN_MILLISECONDS)
    @Transactional
    public void deleteExpiredStoriesEveryHour(){
        storyRepository.deleteAllByExpirationDateBefore(LocalDateTime.now());
    }

    @Override
    public Page<ReactionResponseDTO> getPageOfStoryReactions(long storyId, int page, int size) throws StoryNotFoundException{
        findStoryById(storyId);
        Page<UserStoryReaction> userStoryReactions = userStoryReactionRepository.findAllByStoryId(storyId, PageRequest.of(page, size));
        return new PageImpl<>(
                userStoryReactions
                .stream()
                .map(this::mapReactionToReactionResponseDTO).toList());
    }
    private ReactionResponseDTO mapReactionToReactionResponseDTO(UserStoryReaction userStoryReaction) {
        return new ReactionResponseDTO(
                userStoryReaction.getUser().getId(),
                userStoryReaction.getUser().getUsername(),
                userStoryReaction.isStatus());
    }
    private StoryResponseDTO mapStoryToStoryResponseDTO(Story story) {
        return new StoryResponseDTO(story.getId(),
                story.getContentUrl(),
                story.getDateCreated(),
                story.getExpirationDate(),
                story.getHashtags().stream().map(Hashtag::getTagName).toList(),
                story.getUserTags().stream().map(User::getUsername).toList(),
                userStoryReactionRepository.countAllByStoryIdAndStatusTrue(story.getId()),
                userStoryReactionRepository.countAllByStoryIdAndStatusFalse(story.getId()));
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
        notificationService.addNotification(userList, creator.getUsername() + MessageConstants.TAGGED_YOU_IN_HIS_STORY);
        return userList;
    }
}
