package com.example.demo.repository;

import com.example.demo.model.entity.BlackListToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlackListedToken extends JpaRepository<BlackListToken, String> {
    Optional<BlackListToken> getBlackListTokensByToken(String token);
}
