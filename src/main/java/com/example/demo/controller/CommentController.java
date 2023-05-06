package com.example.demo.controller;

import com.example.demo.model.dto.CommentResponseDTO;
import com.example.demo.model.dto.CreateCommentDTO;
import com.example.demo.service.CommentServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentServiceImpl commentService;
    public CommentController(@Autowired CommentServiceImpl commentService){
        this.commentService = commentService;
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
}
