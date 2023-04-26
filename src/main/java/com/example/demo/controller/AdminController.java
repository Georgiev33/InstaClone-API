package com.example.demo.controller;

import com.example.demo.model.dto.ReportedUsers.ReportedUsersResponseDTO;
import com.example.demo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    @GetMapping
            ("/report")
    public List<ReportedUsersResponseDTO> reportUser() {
        return adminService.getReports();
    }
}
