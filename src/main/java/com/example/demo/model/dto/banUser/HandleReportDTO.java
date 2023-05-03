package com.example.demo.model.dto.banUser;

public record HandleReportDTO(long reportId, String reason, boolean status, int hoursToBan) {
}
