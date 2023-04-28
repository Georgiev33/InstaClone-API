package com.example.demo.controller;

import com.example.demo.model.dto.ReportedUsers.ReportedUsersResponseDTO;
import com.example.demo.service.AdminServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminServiceImpl adminService;
    @GetMapping
            ("/report")
    public List<ReportedUsersResponseDTO> reportUser() {
        return adminService.getReports();
    }
//    @PutMapping("/{userId}/ban")
//    public void banUser(@PathVariable int userId, @RequestHeader("Authorization") String authToken){
//        adminService.banUser(userId,authToken);
//    }
}
