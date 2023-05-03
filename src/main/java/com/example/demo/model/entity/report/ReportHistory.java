package com.example.demo.model.entity.report;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity(name = "report_history")
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ReportHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long reporterId;
    private long reportedId;
    private String reason;
    private boolean status;
    private long adminId;
}
