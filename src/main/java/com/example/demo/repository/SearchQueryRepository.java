package com.example.demo.repository;

import com.example.demo.model.entity.SearchQuery;
import com.example.demo.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchQueryRepository extends JpaRepository<SearchQuery, Long> {
    List<SearchQuery> findAllByUserOrderByDateCreatedDesc(User user);
    void deleteAllByUser(User user);
}
