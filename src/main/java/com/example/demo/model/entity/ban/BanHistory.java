package com.example.demo.model.entity.ban;

import jakarta.persistence.*;
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
    @Column(name = "id")
    private Long id;
    @Column(name = "banned_user_id")
    private long bannedUserId;
    @Column(name = "admin_id")
    private long adminId;
    @Column(name = "reason")
    private String reason;
    @Column(name = "ban_start_date")
    private LocalDateTime banStartDate;
    @Column(name = "ban_end_date")
    private LocalDateTime banEndDate;
    @Column(name = "is_banned")
    private boolean isBanned;
}
