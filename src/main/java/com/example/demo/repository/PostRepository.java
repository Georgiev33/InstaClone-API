package com.example.demo.repository;

import com.example.demo.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    Page<Post> findAllByUserId(long userId, PageRequest request);
}
