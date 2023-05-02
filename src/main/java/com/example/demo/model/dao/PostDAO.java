package com.example.demo.model.dao;

import com.example.demo.model.dto.post.PostSearchResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
@RequiredArgsConstructor
@Component
public class PostDAO {

    private final JdbcTemplate jdbcTemplate;



    public Page<PostSearchResponseDTO> getPostSearchResponseDTOPage(String hashtag, int limit, int offset){
        String query = "SELECT p.id, MIN(pc.content_url) AS content_url, COALESCE(like_count, 0) AS like_count, COALESCE(comment_count, 0) AS comment_count FROM posts AS p \n" +
                "JOIN posts_content AS pc ON (p.id = pc.post_id) \n" +
                "JOIN users AS u ON (p.user_id = u.id AND u.is_private = false)\n" +
                "JOIN hashtags_posts AS hp ON (p.id = hp.post_id)\n" +
                "JOIN hashtags AS h ON(h.id = hp.tag_id AND h.tag_name LIKE ?)\n" +
                "LEFT JOIN(\n" +
                " SELECT post_id, COUNT(*) AS like_count \n" +
                " FROM users_posts_reactions \n" +
                " GROUP BY post_id\n" +
                " ) AS upr\n" +
                " ON (p.id = upr.post_id)\n" +
                "LEFT JOIN (\n" +
                "SELECT post_id, COUNT(*) AS comment_count\n" +
                "FROM comments\n" +
                "GROUP BY post_id\n" +
                ") AS c ON (p.id = c.post_id)\n" +
                "GROUP BY p.id LIMIT ? OFFSET ?";
        return new PageImpl<>(jdbcTemplate.query(query,ps -> {
            ps.setString(1, hashtag);
            ps.setInt(2,  limit);
            ps.setInt(3, offset);
        }, new PostMapper()));
    }

    private static class PostMapper implements RowMapper<PostSearchResponseDTO> {
        @Override
        public PostSearchResponseDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new PostSearchResponseDTO(rs.getLong("id"),
                    rs.getString("content_url"),
                    rs.getInt("like_count"),
                    rs.getInt("comment_count"));
        }
    }
}
