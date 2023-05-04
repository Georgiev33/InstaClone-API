package com.example.demo.model.entity.report;

import jakarta.persistence.*;
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
    @Column(name = "id")
    private long id;
    @Column(name = "reporter_id")
    private long reporterId;
    @Column(name = "reported_id")
    private long reportedId;
    @Column(name = "reason")
    private String reason;
    @Column(name = "status")
    private boolean status;
    @Column(name = "admin_id")
    private long adminId;
}
