package com.example.demo.repository;

import com.example.demo.model.entity.UserCommentReaction;
import com.example.demo.model.entity.UserStoryReaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStoryReactionRepository extends JpaRepository<UserStoryReaction, UserStoryReaction.UserStoryReactionKey> {
    long countAllByStoryIdAndStatusTrue(long storyId);
    long countAllByStoryIdAndStatusFalse(long storyId);
    Page<UserStoryReaction> findAllByStoryId(long storyId, PageRequest request);
}
