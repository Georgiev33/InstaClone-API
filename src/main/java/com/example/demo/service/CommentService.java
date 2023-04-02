package com.example.demo.service;

import com.example.demo.model.dto.CommentResponseDTO;
import com.example.demo.model.dto.CreateCommentDTO;
import com.example.demo.model.entity.Comment;
import com.example.demo.model.entity.Post;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    public CommentService(@Autowired CommentRepository commentRepository,
                          @Autowired PostRepository postRepository,
                          @Autowired UserRepository userRepository,
                          @Autowired ModelMapper modelMapper){
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public CommentResponseDTO createComment(CreateCommentDTO createCommentDTO, Long userId) {
        validateCommentData(createCommentDTO);
        User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User doesn't exist!"));
        Post ownerPost = postRepository.findById(createCommentDTO.getPostId())
                .orElseThrow(() -> new NotFoundException("Nonexistent post."));

        Comment comment = new Comment();
        comment.setPost(ownerPost);
        comment.setUser(author);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setContent(createCommentDTO.getContent());
        comment.setPost(ownerPost);
        ownerPost.getComments().add(comment);


       if(createCommentDTO.getRepliedCommentId() != null){
           Comment parentComment = commentRepository.findById(createCommentDTO.getRepliedCommentId())
                   .orElseThrow(() -> new NotFoundException("Can't reply to a nonexistent comment."));
           comment.setRepliedComment(parentComment);
           commentRepository.save(parentComment);
       }
       commentRepository.save(comment);
        return modelMapper.map(comment, CommentResponseDTO.class);
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
