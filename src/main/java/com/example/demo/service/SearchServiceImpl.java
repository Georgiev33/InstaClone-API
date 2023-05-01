package com.example.demo.service;

import com.example.demo.model.dao.PostDAO;
import com.example.demo.model.dto.User.UserWithUsernameAndIdDTO;
import com.example.demo.model.dto.post.PostSearchResponseDTO;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.contracts.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final UserRepository userRepository;
    private final PostDAO postDAO;

    @Override
    public Page<PostSearchResponseDTO> getPostsByHashTag(String hashtag, int page, int size) {
        int offset = page * size;
        int limit = size;
       return postDAO.getPostSearchResponseDTOPage(hashtag, limit, offset);
    }

    @Override
    public Page<UserWithUsernameAndIdDTO> getPageOfUsers(String username, int page, int size) {
        return new PageImpl<>(userRepository.findAllByUsernameContaining(username, PageRequest.of(page, size))
                .stream()
                .map(user -> new UserWithUsernameAndIdDTO(user.getUsername(), user.getId()))
                .toList());
    }

}
