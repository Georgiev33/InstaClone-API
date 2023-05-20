package com.example.demo.service.contracts;

import com.example.demo.model.dto.CommentResponseDTO;
import com.example.demo.model.dto.CreateCommentDTO;
import com.example.demo.model.dto.ReactionResponseDTO;
import com.example.demo.model.exception.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;

public interface CommentService {
    @Transactional
    CommentResponseDTO createComment(CreateCommentDTO createCommentDTO, String authToken)
            throws UserNotFoundException, PostNotFoundException, AccessDeniedException, CommentNotFoundException;

    void react(String authToken, long commentId, boolean status) throws UserNotFoundException;

    void deleteComment(long commentId, String authToken)  throws InvalidOwnerException, CommentNotFoundException;

    CommentResponseDTO getCommentById(long commentId) throws CommentNotFoundException;

    Page<CommentResponseDTO> getPageOfCommentReplies(long commentId, int page, int size);

    Page<ReactionResponseDTO> getPageOfCommentReactions(long commentId, int page, int size) throws CommentNotFoundException;
}
