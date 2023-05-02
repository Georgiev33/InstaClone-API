package com.example.demo.service;

import com.example.demo.model.dao.PostDAO;
import com.example.demo.model.dto.User.UserWithUsernameAndIdDTO;
import com.example.demo.model.dto.post.PostSearchResponseDTO;
import com.example.demo.model.dto.search.SearchQueryDTO;
import com.example.demo.model.entity.SearchQuery;
import com.example.demo.repository.SearchQueryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.contracts.SearchService;
import com.example.demo.service.contracts.UserValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final UserRepository userRepository;
    private final PostDAO postDAO;
    private final SearchQueryRepository searchQueryRepository;
    private final UserValidationService userValidationService;
    private final JwtService jwtService;

    @Override
    public Page<PostSearchResponseDTO> getPostsByHashTag(String hashtag, String authToken, int page, int size) {
        int offset = page * size;
        int limit = size;
        if(authToken != null) {
            saveSearchQuery(hashtag, authToken);
        }
       return postDAO.getPostSearchResponseDTOPage(hashtag, limit, offset);
    }

    @Override
    public Page<UserWithUsernameAndIdDTO> getPageOfUsers(String username, String authToken, int page, int size) {
        if(authToken != null) {
            saveSearchQuery(username, authToken);
        }
        return new PageImpl<>(userRepository.findAllByUsernameContaining(username, PageRequest.of(page, size))
                .stream()
                .map(user -> new UserWithUsernameAndIdDTO(user.getUsername(), user.getId()))
                .toList());
    }

    @Override
    public List<SearchQueryDTO> getSearchHistory(String authToken) {
        return searchQueryRepository
                .findAllByUserOrderByDateCreatedDesc(userValidationService.findUserById(jwtService.extractUserId(authToken)))
                .stream()
                .map(searchQuery -> new SearchQueryDTO(searchQuery.getId(), searchQuery.getSearchQuery()))
                .toList();
    }
    @Transactional
    @Override
    public void clearSearchHistory(String authToken) {
        searchQueryRepository.deleteAllByUser(userValidationService.findUserById(jwtService.extractUserId(authToken)));
    }

    @Override
    public void clearSearchQuery(long queryId, String authToken) {
        searchQueryRepository.deleteById(queryId);
    }

    private void saveSearchQuery(String query, String authToken){
        SearchQuery searchQuery = SearchQuery
                .builder()
                .user(userValidationService.findUserById(jwtService.extractUserId(authToken)))
                .searchQuery(query)
                .dateCreated(LocalDateTime.now())
                .build();
        searchQueryRepository.save(searchQuery);
    }
}
