package com.example.demo.repository;

import com.example.demo.model.entity.UserCommentReaction;
import com.example.demo.model.entity.UserPostReaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCommentReactionRepository extends JpaRepository<UserCommentReaction, UserCommentReaction.UserCommentReactionKey> {
    long countAllByCommentIdAndStatusTrue(long commentId);
    long countAllByCommentIdAndStatusFalse(long commentId);
    Page<UserCommentReaction> findAllByCommentId(long commentId, PageRequest request);
}
