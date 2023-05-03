package com.example.demo.controller;

import com.example.demo.model.dto.BanHistoryDTO;
import com.example.demo.model.dto.banUser.BanUserDTO;
import com.example.demo.model.dto.ReportedUsers.ReportedUsersResponseDTO;
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

    @GetMapping
            ("/report")
    public List<ReportedUsersResponseDTO> reportUser() {
        return adminService.getReports();
    }

    @PutMapping("/ban")
    public void banUser(@RequestBody BanUserDTO banUserDTO, @RequestHeader("Authorization") String authToken) {
        banUserService.banUser(banUserDTO, authToken);
    }

    @DeleteMapping("/ban")
    public void unbanUser(@RequestBody UnbanUserDTO unbanUserDTO, @RequestHeader("Authorization") String authToken) {
        banUserService.unbanUser(unbanUserDTO, authToken);
    }

    @GetMapping
            ("/bana")
    public Page<BanHistoryDTO> reportUser(@RequestParam(required = false, defaultValue = "-1") long bannedId,
                                          @RequestParam(required = false, defaultValue = "0") int page,
                                          @RequestParam(required = false, defaultValue = "10") int size) {
        return banUserService.banHistory(bannedId, page,size);
    }
}
