package com.example.demo.service;

import com.example.demo.model.dto.CommentResponseDTO;
import com.example.demo.model.dto.CreateCommentDTO;
import com.example.demo.model.entity.*;
import com.example.demo.model.entity.post.Post;
import com.example.demo.model.exception.*;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserCommentReactionRepository;
import com.example.demo.service.contracts.*;
import com.example.demo.util.constants.MessageConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.demo.util.constants.MessageConstants.COMMENT_NOT_FOUND;
import static com.example.demo.util.constants.MessageConstants.TAGGED_YOU_IN_HIS_COMMENT;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserValidationService userValidationService;
    private final AdminService adminService;
    private final JwtService jwtService;
    private final NotificationRepository notificationRepository;
    private final UserCommentReactionRepository userCommentReactionRepository;
    private final NotificationService notificationService;


    @Override
    public CommentResponseDTO getCommentById(long commentId) throws CommentNotFoundException{
        Comment comment = findById(commentId);
        Long repliedCommentId = comment.getRepliedComment() == null ? null : comment.getRepliedComment().getId();
        return new CommentResponseDTO(comment.getId(),
                comment.getUser().getId(),
                comment.getPost().getId(),
                repliedCommentId,
                comment.getContent(),
                comment.getCreatedAt());
    }

    public Page<CommentResponseDTO> getPageOfCommentsForPost(long postId, int page, int size) {
        Page<Comment> postComments = commentRepository.findAllByPostIdAndRepliedCommentIsNull(postId, PageRequest.of(page, size));
        return new PageImpl<>(postComments.stream().map(comment -> new CommentResponseDTO(comment.getId(),
                comment.getUser().getId(),
                comment.getPost().getId(),
                null,
                comment.getContent(),
                comment.getCreatedAt())).toList());
    }

    @Override
    public Page<CommentResponseDTO> getPageOfCommentReplies(long commentId, int page, int size) {
        Page<Comment> commentReplies = commentRepository.findAllByRepliedCommentId(commentId, PageRequest.of(page, size));
        return new PageImpl<>(commentReplies.stream().map(comment -> new CommentResponseDTO(comment.getId(),
                comment.getUser().getId(),
                comment.getPost().getId(),
                comment.getRepliedComment().getId(),
                comment.getContent(),
                comment.getCreatedAt())).toList());
    }

    @Transactional
    public CommentResponseDTO createComment(CreateCommentDTO createCommentDTO, String authToken)
            throws UserNotFoundException, PostNotFoundException, AccessDeniedException, CommentNotFoundException{
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
           Comment parentComment = findById(createCommentDTO.repliedCommentId());
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

    public void react(String authToken, long commentId, boolean status) throws UserNotFoundException, CommentNotFoundException{
        long userId = jwtService.extractUserId(authToken);
        User user = userValidationService.findUserById(userId);
        Comment comment = findById(commentId);

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

    @Override
    public void deleteComment(long commentId, String authToken) throws InvalidOwnerException, CommentNotFoundException{
        long userId = jwtService.extractUserId(authToken);
        Comment comment = findById(commentId);
        if(adminService.isLoggedUserAdmin()){
            commentRepository.delete(comment);
            return;
        }
        comment.verifyOwnerIdOrThrow(userId);
        commentRepository.delete(comment);
    }

    private Comment findById(Long commentId) throws CommentNotFoundException {
        return commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(COMMENT_NOT_FOUND));
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

    private void addTaggedUsers(List<String> taggedUsers, Comment comment) throws UserNotFoundException {
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
