package com.example.demo.repository;

import com.example.demo.model.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
@Repository
public interface StoryRepository extends JpaRepository<Story,Long> {
    void deleteAllByExpirationDateBefore(LocalDateTime localDateTime);
}
