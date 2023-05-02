package com.example.demo.repository;

import com.example.demo.model.entity.BannedUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BannedUsersRepository extends JpaRepository<BannedUsers, Long> {
    Optional<BannedUsers> findByBannedId(long bannedId);
}
