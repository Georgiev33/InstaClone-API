package com.example.demo.service.contracts;

import com.example.demo.model.dto.User.UserWithUsernameAndIdDTO;
import com.example.demo.model.dto.post.PostSearchResponseDTO;
import com.example.demo.model.dto.search.SearchQueryDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SearchService {
    Page<PostSearchResponseDTO> getPostsByHashTag(String hashtag, String authToken, int limit, int offset);
    Page<UserWithUsernameAndIdDTO> getPageOfUsers(String username, String authToken,int limit, int offset);
    List<SearchQueryDTO> getSearchHistory(String authToken);
    void clearSearchHistory(String authToken);
    void clearSearchQuery(long queryId, String authToken);
}
