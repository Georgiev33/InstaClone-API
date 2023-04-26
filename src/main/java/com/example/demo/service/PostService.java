package com.example.demo.service;

import com.example.demo.model.dto.CreatePostDTO;
import com.example.demo.model.dto.PostResponseDTO;
import com.example.demo.model.entity.*;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.repository.PostContentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserPostReactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.util.Constants.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final FileService fileService;
    private final UserService userService;
    private final PostRepository postRepository;
    private final PostContentRepository contentRepository;
    private final HashTagService hashTagService;
    private final JwtService jwtService;
    private final UserPostReactionRepository userPostReactionRepository;
    private final NotificationService notificationService;

    @Transactional
    public PostResponseDTO createPost(CreatePostDTO dto, String authToken) {
        long userId = jwtService.extractUserId(authToken);
        User user = userService.findUserById(userId);
        Post post = Post.builder()
                .dateCreated(LocalDateTime.now())
                .caption(dto.caption())
                .user(user)
                .userTags(addTaggedUsers(dto.taggedUsers(), user))
                .hashtags(new HashSet<>())
                .contentUrls(new ArrayList<>())
                .build();
        hashTagService.addHashTags(dto.hashtags(), post);
        Post saved = postRepository.save(post);
        fileService.createContent(dto.content(), userId, saved);

        return mapPostToPostResponseDTO(saved);
    }
    public Page<PostResponseDTO> getFeed(String authToken, int limit, int offset) {
        long userId = jwtService.extractUserId(authToken);
        System.out.println(userId);
        List<Post> posts = postRepository.findPostsByUserIdWithPostTotalCount(userId, limit,offset);
        return new PageImpl<>(posts.stream()
                .map(this::mapPostToPostResponseDTO)
                .toList());
    }
    public List<String> getAllPostUrls(long postId) {
        List<PostContent> postContents = contentRepository.findAllByPostId(postId)
                .orElseThrow(() -> new BadRequestException(INVALID_POST_ID));

        return postContents.stream()
                .map(PostContent::getContentUrl)
                .collect(Collectors.toList());
    }

    public File getContent(String fileName) {
        return fileService.getFile(fileName);
    }

    public Post findPostById(long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new NotFoundException(POST_NOT_FOUND));
    }

    @Transactional
    public void react(String authToken, long postId, boolean status) {
        long userId = jwtService.extractUserId(authToken);
        User user = userService.findUserById(userId);
        Post post = findPostById(postId);

        if (deleteReactionIfStatusMatches(userId, postId, status)) {
            return;
        }
        UserPostReaction userPostReaction = UserPostReaction.builder()
                .id(new UserPostReaction.UserPostReactionKey(userId, postId))
                .user(user)
                .post(post)
                .status(status)
                .build();
        userPostReactionRepository.save(userPostReaction);
    }

    public Page<PostResponseDTO> getAllUserPosts(long userId, int page, int size) {
        Page<Post> posts = postRepository.findAllByUserId(userId, PageRequest.of(page, size));
        List<PostResponseDTO> pageList = posts.stream()
                .map(this::mapPostToPostResponseDTO)
                .toList();
        return new PageImpl<>(pageList);
    }

    private boolean deleteReactionIfStatusMatches(long userId, long postId, boolean status) {
        Optional<UserPostReaction> reaction =
                userPostReactionRepository.findById(new UserPostReaction.UserPostReactionKey(userId, postId));
        if (reaction.isPresent() && reaction.get().isStatus() == status) {
            userPostReactionRepository.delete(reaction.get());
            return true;
        }
        return false;
    }

    private PostResponseDTO mapPostToPostResponseDTO(Post saved) {
        return new PostResponseDTO(saved.getId(),
                saved.getContentUrls().stream().map(PostContent::getContentUrl).toList(),
                saved.getCaption(),
                saved.getDateCreated(),
                saved.getHashtags().stream().map(Hashtag::getTagName).toList(),
                saved.getUserTags().stream().map(User::getUsername).toList());
    }

    private Set<User> addTaggedUsers(Optional<List<String>> users, User creator) {
        if (users.isEmpty()) return Collections.emptySet();
        Set<User> userList = users.get()
                .stream()
                .map(userService::findUserByUsername)
                .collect(Collectors.toSet());
        notificationService.addNotification(userList, creator.getUsername() + TAGGED_YOU_IN_HIS_POST);
        return userList;
    }

}
