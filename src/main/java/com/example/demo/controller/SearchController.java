package com.example.demo.controller;

import com.example.demo.model.dto.User.UserWithUsernameAndIdDTO;
import com.example.demo.model.dto.post.PostSearchResponseDTO;
import com.example.demo.service.contracts.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/hashtag")
    public Page<PostSearchResponseDTO> getPageOfPostsByHashtag(@RequestParam String hashtag,
                                                               @RequestParam(required = false, defaultValue = "0") int page,
                                                               @RequestParam(required = false, defaultValue = "10") int size){
        return searchService.getPostsByHashTag(hashtag, page, size);
    }
    @GetMapping("/users")
    public Page<UserWithUsernameAndIdDTO> getPageOfUsers(@RequestParam String username,
                                                         @RequestParam(required = false, defaultValue = "0") int page,
                                                         @RequestParam(required = false, defaultValue = "10") int size){
        return searchService. getPageOfUsers(username, page, size);
    }
}
