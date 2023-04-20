package com.example.demo.repository;

import com.example.demo.model.entity.StoryContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoryContentRepository extends JpaRepository<StoryContent,Long> {
    Optional<List<StoryContent>> findAllByStoryId(long storyId);

}
