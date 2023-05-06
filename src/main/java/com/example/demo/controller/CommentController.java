package com.example.demo.controller;

import com.example.demo.model.dto.CommentResponseDTO;
import com.example.demo.model.dto.CreateCommentDTO;
import com.example.demo.service.CommentServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentServiceImpl commentService;
    public CommentController(@Autowired CommentServiceImpl commentService){
        this.commentService = commentService;
    }
    @GetMapping("/{commentId}")
    public CommentResponseDTO getCommentById(@PathVariable long commentId){
        return commentService.getCommentById(commentId);
    }
    @GetMapping("/replies")
    public Page<CommentResponseDTO> getPageOfCommentReplies(@RequestParam long commentId,
                                                            @RequestParam(required = false, defaultValue = "0") int page,
                                                            @RequestParam(required = false, defaultValue = "10") int size){
        return commentService.getPageOfCommentReplies(commentId, page, size);
    }
    @GetMapping("/post")
    public Page<CommentResponseDTO> getPageOfCommentsForPost(@RequestParam long postId,
                                                             @RequestParam(required = false, defaultValue = "0") int page,
                                                             @RequestParam(required = false, defaultValue = "10") int size){
        return commentService.getPageOfCommentsForPost(postId, page, size);
    }
    @PostMapping()
    public CommentResponseDTO createComment(@RequestBody @Valid CreateCommentDTO commentDTO,
                                            @RequestHeader("Authorization") String authToken){
        return commentService.createComment(commentDTO, authToken);
    }
    @PostMapping("/{commentId}/reactions")
    public void react(@PathVariable long commentId,
                      @RequestHeader("Authorization") String authToken,
                      @RequestParam boolean status) {
        commentService.react(authToken, commentId, status);
    }
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable long commentId,
                              @RequestHeader("Authorization") String authToken){
        commentService.deleteComment(commentId, authToken);
    }
}
