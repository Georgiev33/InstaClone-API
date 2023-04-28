package com.example.demo.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "banned_users")
@Getter
@NoArgsConstructor
public class BannedUsers {
    public BannedUsers(int bannedId, int adminId, String reason, LocalDateTime expirationDate) {
        this.bannedId = bannedId;
        this.adminId = adminId;
        this.reason = reason;
        this.expirationDate = expirationDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int bannedId;
    private int adminId;
    private String reason;
    private LocalDateTime expirationDate;
}
