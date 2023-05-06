package com.example.demo.service;

import com.example.demo.model.dto.ReportedUsers.ReportHistoryResponseDTO;
import com.example.demo.model.dto.ReportedUsers.ReportedUsersResponseDTO;
import com.example.demo.model.dto.banUser.BanUserDTO;
import com.example.demo.model.dto.banUser.HandleReportDTO;
import com.example.demo.model.entity.report.ReportHistory;
import com.example.demo.model.entity.report.ReportedUser;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.AccessDeniedException;
import com.example.demo.model.exception.ReportNotFoundException;
import com.example.demo.repository.report.ReportHistoryRepository;
import com.example.demo.repository.report.ReportedUserRepository;
import com.example.demo.service.contracts.AdminService;
import com.example.demo.service.contracts.BanUserService;
import com.example.demo.service.contracts.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.util.Constants.ACCESS_DENIED;
import static com.example.demo.util.Constants.ADMIN;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final ReportedUserRepository reportedUsersRepository;
    private final ReportHistoryRepository reportHistoryRepository;
    private final BanUserService banUserService;
    private final JwtService jwtService;

    public Page<ReportHistoryResponseDTO> getReportHistory(long reportedId,int page, int size) throws ReportNotFoundException {
        if(reportedId == -1) {
            Page<ReportHistory> reportHistories = reportHistoryRepository.findAll(PageRequest.of(page, size));
            return new PageImpl<>(reportHistories
                    .stream()
                    .map(reportHistory -> new ReportHistoryResponseDTO(reportHistory.getReporterId(),
                            reportHistory.getReportedId(),
                            reportHistory.getReason(),
                            reportHistory.getAdminId(),
                            reportHistory.isStatus()))
                    .toList());
        }
        Page<ReportHistory> reportHistories = reportHistoryRepository.findAllByReportedId(reportedId, PageRequest.of(page, size));
        return new PageImpl<>(reportHistories
                .stream()
                .map(reportHistory -> new ReportHistoryResponseDTO(reportHistory.getReporterId(),
                        reportHistory.getReportedId(),
                        reportHistory.getReason(),
                        reportHistory.getAdminId(),
                        reportHistory.isStatus()))
                .toList());
    }

    @Override
    @Transactional
    public void handleReport(String authToken, HandleReportDTO handleReportDTO) throws ReportNotFoundException {
        ReportedUser report = findReportByIdElseThrowNotFound(handleReportDTO.reportId());
        List<ReportedUser> userReports = reportedUsersRepository.findAllByReportedId(report.getReportedId());
        String reason = handleReportDTO.reason() == null ? report.getReason() : handleReportDTO.reason();
        if (handleReportDTO.status()) {
            banUserService.banUser(new BanUserDTO(report.getReportedId(), reason, handleReportDTO.hoursToBan()), authToken);
            reportHistoryRepository.saveAll(userReports
                    .stream()
                    .map(reportedUser -> ReportHistory
                            .builder().reporterId(reportedUser.getReporterId())
                            .reportedId(reportedUser.getReportedId())
                            .reason(reportedUser.getReason())
                            .adminId(jwtService.extractUserId(authToken))
                            .status(true)
                            .build())
                    .toList());
            reportedUsersRepository.deleteAll(userReports);
            return;
        }
        reportedUsersRepository.delete(report);
        reportHistoryRepository.save(
                ReportHistory
                        .builder()
                        .reportedId(report.getReportedId())
                        .reporterId(report.getReporterId())
                        .adminId(jwtService.extractUserId(authToken))
                        .reason(report.getReason())
                        .status(false)
                        .build());
    }

    @Override
    public Page<ReportedUsersResponseDTO> getActiveReports(long reportedId, int page, int size) {
        if(reportedId == -1){
            return new PageImpl<>(reportedUsersRepository.findAll(PageRequest.of(page, size))
                    .stream()
                    .map(reportedUser -> new ReportedUsersResponseDTO(reportedUser.getReporterId(),
                            reportedUser.getReportedId(),
                            reportedUser.getReason())).toList());
        }
        return new PageImpl<>(reportedUsersRepository.findAllByReportedId(reportedId, PageRequest.of(page, size))
                .stream()
                .map(reportedUser -> new ReportedUsersResponseDTO(reportedUser.getReporterId(),
                        reportedUser.getReportedId(),
                        reportedUser.getReason())).toList());
    }
    @Override
    public void hasPermission(User targetUser) throws AccessDeniedException {
        if (targetUser.isPrivate() && !isLoggedUserAdmin() && isLoggedUserFollow(targetUser)) {
            throw new AccessDeniedException(ACCESS_DENIED);
        }
    }
    public boolean isLoggedUserAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.contains(ADMIN));
    }

    private ReportedUser findReportByIdElseThrowNotFound(long reportId) {
        return reportedUsersRepository.findById(reportId).orElseThrow(() -> new ReportNotFoundException("Report doesn't exist."));
    }
    private boolean isLoggedUserFollow(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return user.getFollowers().stream()
                .map(User::getUsername)
                .anyMatch(u -> u.contains(authentication.getName()));
    }


}
