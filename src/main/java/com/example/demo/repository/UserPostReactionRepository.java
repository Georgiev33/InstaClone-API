package com.example.demo.repository;

import com.example.demo.model.entity.UserPostReaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPostReactionRepository extends JpaRepository<UserPostReaction, UserPostReaction.UserPostReactionKey> {
    long countAllByPostIdAndStatusTrue(long postId);
    long countAllByPostIdAndStatusFalse(long postId);
    Page<UserPostReaction> findAllByPostId(long postId, PageRequest request);
}
