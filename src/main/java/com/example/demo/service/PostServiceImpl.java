package com.example.demo.service;

import com.example.demo.model.dto.post.CreatePostDTO;
import com.example.demo.model.dto.ReactionResponseDTO;
import com.example.demo.model.dto.post.PostResponseDTO;
import com.example.demo.model.entity.*;
import com.example.demo.model.entity.post.Post;
import com.example.demo.model.entity.post.PostContent;
import com.example.demo.model.exception.*;
import com.example.demo.repository.PostContentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserPostReactionRepository;
import com.example.demo.service.contracts.*;
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

import static com.example.demo.util.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final FileService fileService;
    private final UserValidationService userValidationService;
    private final PostRepository postRepository;
    private final PostContentRepository contentRepository;
    private final HashTagService hashTagService;
    private final JwtService jwtService;
    private final UserPostReactionRepository userPostReactionRepository;
    private final NotificationService notificationService;
    private final AdminService adminService;

    @Override
    @Transactional
    public PostResponseDTO createPost(CreatePostDTO dto, String authToken) throws UserNotFoundException {
        long userId = jwtService.extractUserId(authToken);
        User user = userValidationService.findUserById(userId);
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
    @Override
    public Page<PostResponseDTO> getFeed(String authToken, int page, int size) {
        long userId = jwtService.extractUserId(authToken);
        int offset = page * size;
        int limit = size;
        List<Post> posts = postRepository.findPostsByUserIdWithPostTotalCount(userId, offset,limit);
        return new PageImpl<>(posts.stream()
                .map(this::mapPostToPostResponseDTO)
                .toList());
    }
    @Override
    public List<String> getAllPostUrls(long postId) throws PostNotFoundException {
        List<PostContent> postContents = contentRepository.findAllByPostId(postId)
                .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND));

        return postContents.stream()
                .map(PostContent::getContentUrl)
                .collect(Collectors.toList());
    }

    @Override
    public File getContent(String fileName) throws FileNotFoundException{
        return fileService.getFile(fileName);
    }

    @Override
    public Post findPostById(long postId) throws PostNotFoundException {
        return postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND));
    }

    @Override
    @Transactional
    public void react(String authToken, long postId, boolean status) {
        long userId = jwtService.extractUserId(authToken);
        User user = userValidationService.findUserById(userId);
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
        notificationService.addNotification(post.getUser(), user.getUsername() + POST_REACTION_MESSAGE);
        userPostReactionRepository.save(userPostReaction);
    }

    @Override
    public Page<PostResponseDTO> getAllUserPosts(long userId, int page, int size) throws UserNotFoundException{
        userValidationService.throwExceptionIfUserNotFound(userId);
        Page<Post> posts = postRepository.findAllByUserId(userId, PageRequest.of(page, size));
        List<PostResponseDTO> pageList = posts.stream()
                .map(this::mapPostToPostResponseDTO)
                .toList();
        return new PageImpl<>(pageList);
    }

    @Override
    public void deletePostById(long postId, String authToken) throws InvalidOwnerException, PostNotFoundException {
        Post post = findPostById(postId);
        long userId = jwtService.extractUserId(authToken);
        if(adminService.isLoggedUserAdmin()){
            postRepository.delete(post);
            return;
        }
        post.verifyOwnerIdOrThrow(userId);
        postRepository.delete(post);
    }

    @Override
    public PostResponseDTO getPostById(long postId) throws PostNotFoundException{
        Post post = findPostById(postId);
        return mapPostToPostResponseDTO(post);
    }

    @Override
    public Page<ReactionResponseDTO> getPageOfPostReactions(long postId, int page, int size) throws PostNotFoundException {
        findPostById(postId);
        Page<UserPostReaction> reactions = userPostReactionRepository.findAllByPostId(postId, PageRequest.of(page, size));
        return new PageImpl<>(reactions
                .stream()
                .map(this::mapReactionToReactionResponseDTO
                ).toList());
    }

    private ReactionResponseDTO mapReactionToReactionResponseDTO(UserPostReaction userPostReaction) {
        return new ReactionResponseDTO(
                userPostReaction.getUser().getId(),
                userPostReaction.getUser().getUsername(),
                userPostReaction.isStatus());
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

    private PostResponseDTO mapPostToPostResponseDTO(Post post) {
        return new PostResponseDTO(post.getId(),
                post.getContentUrls().stream().map(PostContent::getContentUrl).toList(),
                post.getCaption(),
                post.getDateCreated(),
                post.getHashtags().stream().map(Hashtag::getTagName).toList(),
                post.getUserTags().stream().map(User::getUsername).toList(),
                userPostReactionRepository.countAllByPostIdAndStatusTrue(post.getId()),
                userPostReactionRepository.countAllByPostIdAndStatusFalse(post.getId())
                );
    }

    private Set<User> addTaggedUsers(Optional<List<String>> users, User creator) {
        if (users.isEmpty()) return Collections.emptySet();
        Set<User> userList = users.get()
                .stream()
                .map(userValidationService::getUserOrThrowException)
                .collect(Collectors.toSet());
        notificationService.addNotification(userList, creator.getUsername() + TAGGED_YOU_IN_HIS_POST);
        return userList;
    }

}
