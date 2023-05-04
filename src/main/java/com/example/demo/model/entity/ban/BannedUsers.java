package com.example.demo.model.entity.ban;

import jakarta.persistence.*;
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
    @Column(name = "id")
    private Long id;
    @Column(name = "banned_id")
    private long bannedId;
    @Column(name = "admin_id")
    private long adminId;
    @Column(name = "reason")
    private String reason;
    @Column(name = "ban_start_date")
    private LocalDateTime banStartDate;
    @Column(name = "ban_end_date")
    private LocalDateTime banEndDate;
}
