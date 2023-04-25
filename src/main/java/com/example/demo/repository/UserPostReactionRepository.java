package com.example.demo.repository;

import com.example.demo.model.entity.UserPostReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPostReactionRepository extends JpaRepository<UserPostReaction, UserPostReaction.UserPostReactionKey> {
}
