package com.example.demo.model.dto;

import java.time.LocalDateTime;

public record BanHistoryDTO(long bannedId, long adminId,
                            String reason,
                            LocalDateTime banStartDate,
                            LocalDateTime banEndDate,
                            boolean isBanned) {
}
