package com.example.demo.model.entity.Ban;

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
    public BannedUsers(long bannedId, long adminId, String reason, LocalDateTime banStartDate, LocalDateTime banEndDate) {
        this.bannedId = bannedId;
        this.adminId = adminId;
        this.reason = reason;
        this.banStartDate = banStartDate;
        this.banEndDate = banEndDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long bannedId;
    private long adminId;
    private String reason;
    private LocalDateTime banStartDate;
    private LocalDateTime banEndDate;
}
