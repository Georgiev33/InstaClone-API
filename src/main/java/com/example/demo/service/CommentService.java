package com.example.demo.service;

import com.example.demo.model.dto.CommentResponseDTO;
import com.example.demo.model.dto.CreateCommentDTO;
import com.example.demo.model.entity.Comment;
import com.example.demo.model.entity.Post;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.repository.CommentRepository;
import com.example.demo.util.UserServiceHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    private final PostService postService;

    private final UserServiceHelper userServiceHelper;

    private final ModelMapper modelMapper;
    private final JwtService jwtService;

    @Transactional
    public CommentResponseDTO createComment(CreateCommentDTO createCommentDTO, String authToken) {
        long userId = jwtService.extractUserId(authToken);
        validateCommentData(createCommentDTO);
        User author = userServiceHelper.findUserById(userId);
        Post ownerPost = postService.findPostById(createCommentDTO.getPostId());

        Comment comment = new Comment();
        comment.setPost(ownerPost);
        comment.setUser(author);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setContent(createCommentDTO.getContent());
        ownerPost.getComments().add(comment);


       if(createCommentDTO.getRepliedCommentId() != null){
           Comment parentComment = findById(createCommentDTO.getRepliedCommentId(),
                   "Can't reply to a nonexistent comment!");
           if(!parentComment.getPost().getId().equals(createCommentDTO.getPostId())){
               throw new BadRequestException("The comment you're replying to belongs to another post!");
           }
           comment.setRepliedComment(parentComment);
           commentRepository.save(parentComment);
       }
       commentRepository.save(comment);
        return modelMapper.map(comment, CommentResponseDTO.class);
    }

    private Comment findById(Long commentId, String exceptionMessage) {
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(exceptionMessage));
    }

    private void validateCommentData(CreateCommentDTO createCommentDTO) {
        if(createCommentDTO.getContent() == null){
            throw new BadRequestException("Can't create a comment with no content!");
        }
        if(createCommentDTO.getPostId() == null){
            throw new BadRequestException("Can't comment a nonexistent post");
        }

    }
}
