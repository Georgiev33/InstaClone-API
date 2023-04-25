package com.example.demo.repository;

import com.example.demo.model.entity.UserStoryReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStoryReactionRepository extends JpaRepository<UserStoryReaction, UserStoryReaction.UserStoryReactionKey> {
}
