package com.example.demo.controller;

import com.example.demo.model.dto.User.UserWithUsernameAndIdDTO;
import com.example.demo.model.dto.post.PostSearchResponseDTO;
import com.example.demo.model.dto.search.SearchQueryDTO;
import com.example.demo.service.contracts.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/hashtag")
    public Page<PostSearchResponseDTO> getPageOfPostsByHashtag(@RequestParam String hashtag,
                                                               @RequestParam(required = false, defaultValue = "0") int page,
                                                               @RequestParam(required = false, defaultValue = "10") int size,
                                                               @RequestHeader(value = "Authorization", required = false) String authToken){
        return searchService.getPostsByHashTag(hashtag, authToken,page, size);
    }
    @GetMapping("/users")
    public Page<UserWithUsernameAndIdDTO> getPageOfUsers(@RequestParam String username,
                                                         @RequestParam(required = false, defaultValue = "0") int page,
                                                         @RequestParam(required = false, defaultValue = "10") int size,
                                                         @RequestHeader(value = "Authorization",required = false) String authToken){
        return searchService. getPageOfUsers(username,authToken, page, size);
    }
    @GetMapping
    public List<SearchQueryDTO> getSearchHistory(@RequestHeader("Authorization") String authToken){
        return searchService.getSearchHistory(authToken);
    }
    @DeleteMapping
    public void clearSearchHistory(@RequestHeader("Authorization") String authToken){
        searchService.clearSearchHistory(authToken);
    }
    @DeleteMapping("/{queryId}")
    public void deleteSearchQuery(@PathVariable long queryId,
                                  @RequestHeader("Authorization") String authToken){
        searchService.clearSearchQuery(queryId, authToken);
    }
}
