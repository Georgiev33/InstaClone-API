package com.example.demo.service.contracts;

import com.example.demo.model.dto.User.UserWithUsernameAndIdDTO;
import com.example.demo.model.dto.post.PostSearchResponseDTO;
import org.springframework.data.domain.Page;

public interface SearchService {
    Page<PostSearchResponseDTO> getPostsByHashTag(String hashtag, int limit, int offset);
    Page<UserWithUsernameAndIdDTO> getPageOfUsers(String username, int limit, int offset);
}
