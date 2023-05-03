package com.example.demo.repository.ban;

import com.example.demo.model.entity.ban.BannedUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BannedUsersRepository extends JpaRepository<BannedUsers, Long> {
    Optional<BannedUsers> findByBannedId(long bannedId);
}
