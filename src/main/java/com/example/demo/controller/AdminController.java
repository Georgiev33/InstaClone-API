package com.example.demo.controller;

import com.example.demo.model.dto.BanHistoryDTO;
import com.example.demo.model.dto.ReportedUsers.ReportHistoryResponseDTO;
import com.example.demo.model.dto.banUser.BanUserDTO;
import com.example.demo.model.dto.ReportedUsers.ReportedUsersResponseDTO;
import com.example.demo.model.dto.banUser.HandleReportDTO;
import com.example.demo.model.dto.banUser.UnbanUserDTO;
import com.example.demo.service.contracts.AdminService;
import com.example.demo.service.contracts.BanUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final BanUserService banUserService;

    @PutMapping("/ban")
    public void banUser(@RequestBody BanUserDTO banUserDTO, @RequestHeader("Authorization") String authToken) {
        banUserService.banUser(banUserDTO, authToken);
    }

    @DeleteMapping("/ban")
    public void unbanUser(@RequestBody UnbanUserDTO unbanUserDTO, @RequestHeader("Authorization") String authToken) {
        banUserService.unbanUser(unbanUserDTO, authToken);
    }

    @GetMapping
            ("/ban")
    public Page<BanHistoryDTO> getBanHistory(@RequestParam(required = false, defaultValue = "-1") long bannedId,
                                          @RequestParam(required = false, defaultValue = "0") int page,
                                          @RequestParam(required = false, defaultValue = "10") int size) {
        return banUserService.banHistory(bannedId, page,size);
    }

    @PostMapping("/report")
    public void handleReport(@RequestHeader("Authorization") String authToken,
                             @RequestBody HandleReportDTO handleReportDTO) {
        adminService.handleReport(authToken,handleReportDTO);
    }

    @GetMapping("/report/history")
    public Page<ReportHistoryResponseDTO> getReportHistory(@RequestParam(required = false, defaultValue = "-1") long reportedId,
                                                           @RequestParam(required = false, defaultValue = "0") int page,
                                                           @RequestParam(required = false, defaultValue = "10") int size) {
        return adminService.getReportHistory(reportedId, page, size);
    }

    @GetMapping("/report")
    public Page<ReportedUsersResponseDTO> getActiveReports(@RequestParam(required = false, defaultValue = "-1") long reportedId,
                                                           @RequestParam(required = false, defaultValue = "0") int page,
                                                           @RequestParam(required = false, defaultValue = "10") int size){
        return adminService.getActiveReports(reportedId, page, size);
    }
}
