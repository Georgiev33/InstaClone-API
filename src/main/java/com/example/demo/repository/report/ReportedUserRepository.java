package com.example.demo.repository.report;

import com.example.demo.model.entity.report.ReportedUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportedUserRepository extends JpaRepository<ReportedUser,Long> {
    Optional<ReportedUser> findByReporterIdAndReportedId(long reporterId, long reportedId);
    Page<ReportedUser> findAllByReportedId(long reportedId, PageRequest request);
    List<ReportedUser> findAllByReportedId(long reportedId);
}
