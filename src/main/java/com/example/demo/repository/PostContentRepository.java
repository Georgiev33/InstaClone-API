package com.example.demo.repository;

import com.example.demo.model.entity.post.PostContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostContentRepository extends JpaRepository<PostContent, Long> {
    Optional<List<PostContent>> findAllByPostId(long postId);
}
