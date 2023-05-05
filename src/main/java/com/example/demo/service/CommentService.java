package com.example.demo.service;

import com.example.demo.model.dto.CommentResponseDTO;
import com.example.demo.model.dto.CreateCommentDTO;
import com.example.demo.model.entity.*;
import com.example.demo.model.entity.post.Post;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserCommentReactionRepository;
import com.example.demo.service.contracts.AdminService;
import com.example.demo.service.contracts.UserValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.demo.util.Constants.TAGGED_YOU_IN_HIS_COMMENT;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserValidationService userValidationService;
    private final AdminService adminService;
    private final JwtService jwtService;
    private final NotificationRepository notificationRepository;
    private final UserCommentReactionRepository userCommentReactionRepository;
    private final NotificationService notificationService;

    @Transactional
    public CommentResponseDTO createComment(CreateCommentDTO createCommentDTO, String authToken) {
        long userId = jwtService.extractUserId(authToken);
        User author = userValidationService.findUserById(userId);
        Post ownerPost = postService.findPostById(createCommentDTO.postId());
        adminService.hasPermission(ownerPost.getUser());

        Comment comment = new Comment();
        comment.setPost(ownerPost);
        comment.setUser(author);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setContent(createCommentDTO.content());
        ownerPost.getComments().add(comment);
        if(createCommentDTO.taggedUsers() != null && !createCommentDTO.taggedUsers().isEmpty()){
            addTaggedUsers(createCommentDTO.taggedUsers(), comment);
        }

       if(createCommentDTO.repliedCommentId() != null){
           Comment parentComment = findById(createCommentDTO.repliedCommentId(),
                   "Can't reply to a nonexistent comment!");
           if(!parentComment.getPost().getId().equals(createCommentDTO.postId())){
               throw new BadRequestException("The comment you're replying to belongs to another post!");
           }
           comment.setRepliedComment(parentComment);
           commentRepository.save(parentComment);
       }
       commentRepository.save(comment);
       notificationService.addNotification(ownerPost.getUser(), author.getUsername() + " commented your your post.");
        return mapCommentToResponseDTO(comment);
    }

    public void react(String authToken, long commentId, boolean status) {
        long userId = jwtService.extractUserId(authToken);
        User user = userValidationService.findUserById(userId);
        Comment comment = findById(commentId,"Comment doesn't exist.");

        if (deleteReactionIfStatusMatches(userId, commentId, status)) {
            return;
        }
        UserCommentReaction userCommentReaction = UserCommentReaction.builder()
                .id(new UserCommentReaction.UserCommentReactionKey(userId, commentId))
                .user(user)
                .comment(comment)
                .status(status)
                .build();
        userCommentReactionRepository.save(userCommentReaction);
    }

    private Comment findById(Long commentId, String exceptionMessage) {
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(exceptionMessage));
    }

    private boolean deleteReactionIfStatusMatches(long userId, long commentId, boolean status) {
        Optional<UserCommentReaction> reaction =
                userCommentReactionRepository.findById(new UserCommentReaction.UserCommentReactionKey(userId, commentId));
        if (reaction.isPresent() && reaction.get().isStatus() == status) {
            userCommentReactionRepository.delete(reaction.get());
            return true;
        }
        return false;
    }

    private void addTaggedUsers(List<String> taggedUsers, Comment comment) {
        for (String taggedUser : taggedUsers) {
            User user = userValidationService.getUserOrThrowException(taggedUser);
            adminService.hasPermission(user);
            comment.getTaggedUsers().add(user);
            Notification notification = Notification.builder()
                    .user(user)
                    .dateCreated(LocalDateTime.now())
                    .notification(comment.getUser().getUsername()+ TAGGED_YOU_IN_HIS_COMMENT)
                    .build();
            notificationRepository.save(notification);
        }
    }

    private CommentResponseDTO mapCommentToResponseDTO(Comment comment) {
        Long repliedCommentId = comment.getRepliedComment() == null ? null : comment.getRepliedComment().getId();
        return new CommentResponseDTO(comment.getId(),
                comment.getUser().getId(),
                comment.getPost().getId(),
                repliedCommentId,
                comment.getContent(),
                comment.getCreatedAt());
    }
}
