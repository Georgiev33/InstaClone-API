package com.example.demo.service.contracts;


import com.example.demo.model.dto.ReportedUsers.ReportHistoryResponseDTO;
import com.example.demo.model.dto.ReportedUsers.ReportedUsersResponseDTO;
import com.example.demo.model.dto.banUser.HandleReportDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.AccessDeniedException;
import com.example.demo.model.exception.ReportNotFoundException;
import org.springframework.data.domain.Page;

public interface AdminService {
   void hasPermission(User targetUser) throws AccessDeniedException;
    Page<ReportHistoryResponseDTO> getReportHistory(long reportedId, int page, int size) throws ReportNotFoundException;

    void handleReport(String authToken, HandleReportDTO handleReportDTO) throws ReportNotFoundException;

    Page<ReportedUsersResponseDTO> getActiveReports(long reportedId, int page, int size);
}
