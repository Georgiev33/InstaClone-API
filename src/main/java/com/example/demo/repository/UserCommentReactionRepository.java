package com.example.demo.repository;

import com.example.demo.model.entity.UserCommentReaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCommentReactionRepository extends JpaRepository<UserCommentReaction, UserCommentReaction.UserCommentReactionKey> {
}
