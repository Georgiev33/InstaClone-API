package com.example.demo.repository.report;

import com.example.demo.model.entity.report.ReportHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportHistoryRepository extends JpaRepository<ReportHistory, Long> {
    Page<ReportHistory> findAllByReportedId(long reportedId, PageRequest request);
}
