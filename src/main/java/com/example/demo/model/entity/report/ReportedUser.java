package com.example.demo.model.entity.report;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "reported_users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportedUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "reporter_id")
    private long reporterId;
    @Column(name = "reported_id")
    private long reportedId;
    @Column(name = "reason")
    private String reason;
}
