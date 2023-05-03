package com.example.demo.model.entity.report;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private Long id;
    private long reporterId;
    private long reportedId;
    private String reason;
}
