package com.example.demo.model.dto.ReportedUsers;

public record ReportHistoryResponseDTO(long reporter, long reported, String reason, long adminId, boolean status) {
}
