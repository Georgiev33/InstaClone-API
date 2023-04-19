package com.example.demo.controller;

import com.example.demo.model.dto.NotificationResponseDTO;
import com.example.demo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notify")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public DeferredResult<List<NotificationResponseDTO>>
    getNotifications(@RequestHeader("Authorization") String authToken) {
        return notificationService.getNotifications(authToken);
    }
}
