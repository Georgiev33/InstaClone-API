package com.example.demo.model.entity.ban;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "ban_history")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BanHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long bannedUserId;
    private long adminId;
    private String reason;
    private LocalDateTime banStartDate;
    private LocalDateTime banEndDate;
    private boolean isBanned;
}
