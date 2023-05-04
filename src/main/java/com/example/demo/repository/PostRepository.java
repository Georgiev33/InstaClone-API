package com.example.demo.repository;

import com.example.demo.model.entity.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    Page<Post> findAllByUserId(long userId, PageRequest request);
    @Query(value = "SELECT * FROM(" +
            "SELECT p.id," +
            "p.user_id," +
            "p.caption," +
            "p.is_deleted," +
            "p.date_created," +
            "COUNT(DISTINCT upr.user_id) + COUNT(DISTINCT c.id) * 0.5 AS post_total_count " +
            "FROM posts AS p " +
            "JOIN users AS u ON (p.user_id = u.id) " +
            "JOIN following AS f ON(f.following_id = p.user_id) " +
            "LEFT JOIN users_posts_reactions AS upr ON p.id = upr.post_id " +
            "LEFT JOIN comments AS c ON p.id = c.post_id " +
            "WHERE f.user_id = ?1 " +
            "GROUP BY p.id ORDER BY date_created DESC LIMIT ?2 OFFSET ?3) AS posts " +
            "ORDER BY posts.post_total_count DESC",
          nativeQuery = true)
    List<Post> findPostsByUserIdWithPostTotalCount(long userId, int limit, int offset);

}
