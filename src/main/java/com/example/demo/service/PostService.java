package com.example.demo.service;

import com.example.demo.model.dto.CreatePostDTO;
import com.example.demo.model.dto.PostResponseDTO;
import com.example.demo.model.entity.*;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.PostContentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserPostReactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.demo.util.Constants.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final String serverPort;
    private final FileService fileService;
    private final UserService userService;
    private final PostRepository postRepository;
    private final PostContentRepository contentRepository;
    private final HashTagService hashTagService;
    private final JwtService jwtService;
    private final UserPostReactionRepository userPostReactionRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public PostResponseDTO createPost(CreatePostDTO dto, String authToken) {
        long userId = jwtService.extractUserId(authToken);
        User user = userService.findUserById(userId);
        if (dto.getContent() == null) {
            throw new BadRequestException(POST_CONTENT_IS_REQUIRED1);
        }

        Post post = new Post();
        post.setDateCreated(LocalDateTime.now());
        post.setCaption(dto.getCaption());
        post.setUser(user);
        if(dto.getTaggedUsers() != null || !dto.getTaggedUsers().isEmpty()) {
            addTaggedUsers(post, dto);
        }
        hashTagService.addHashTags(dto.getHashtags(), post);

        Post saved = postRepository.save(post);

        for (MultipartFile file : dto.getContent()) {
            String fileName = fileService.saveFile(file, userId);
            PostContent content = new PostContent();
            content.setPost(post);
            content.setContentUrl(HTTP_LOCALHOST + serverPort + POST_CONTENT + fileName);
            contentRepository.save(content);
        }

        return mapPostToPostResponseDto(saved);
    }

    private void addTaggedUsers(Post post, CreatePostDTO dto) {
        for (String taggedUser : dto.getTaggedUsers()) {
            User user = userService.findUserByUsername(taggedUser);
            post.getUserTags().add(user);
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setDateCreated(LocalDateTime.now());
            notification.setNotification(post.getUser().getUsername() + " tagged you in his post.");
            notificationRepository.save(notification);
        }
    }

    public List<String> getAllPostUrls(long postId) {
        List<PostContent> postContents = contentRepository.findAllByPostId(postId)
                .orElseThrow(() -> new BadRequestException(INVALID_POST_ID));

        return postContents.stream().map(PostContent::getContentUrl).collect(Collectors.toList());
    }

    public File getContent(String fileName) {
        return fileService.getFile(fileName);
    }

    public Post findPostById(long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new NotFoundException(POST_NOT_FOUND));
    }

    private void setResponseUrl(PostResponseDTO responseDTO, Long postId) {
        String contentUrl = HTTP_LOCALHOST + serverPort + MEDIA_URI + postId;
        responseDTO.setContentUrl(contentUrl);
    }

    private void setHashTags(PostResponseDTO responseDTO, Set<Hashtag> hashtags) {
        for (Hashtag hashtag : hashtags) {
            responseDTO.getHashtags().add(hashtag.getTagName());
        }
    }

    private PostResponseDTO mapPostToPostResponseDto(Post post) {
        PostResponseDTO responseDTO = new PostResponseDTO();
        responseDTO.setId(post.getId());
        responseDTO.setCaption(post.getCaption());
        responseDTO.setDateCreated(post.getDateCreated());
        setResponseUrl(responseDTO, post.getId());
        setHashTags(responseDTO, post.getHashtags());
        return responseDTO;
    }

    @Transactional
    public void likePost(String authToken, long postId, boolean status) {
        long userId = jwtService.extractUserId(authToken);
        User user = userService.findUserById(userId);
        Post post = findPostById(postId);

        if(deleteReactionIfStatusMatches(userId, postId, status)){
            return;
        }
        UserPostReaction userPostReaction = UserPostReaction.builder()
                .id(new UserPostReactionKey(userId,postId))
                .user(user)
                .post(post)
                .status(status)
                .build();
        userPostReactionRepository.save(userPostReaction);
    }

    private boolean deleteReactionIfStatusMatches(long userId, long postId, boolean status){
        Optional<UserPostReaction> reaction = userPostReactionRepository.findById(new UserPostReactionKey(userId, postId));
        if (reaction.isPresent() && reaction.get().isStatus() == status) {
            userPostReactionRepository.delete(reaction.get());
            return true;
        }
        return false;
    }
}
