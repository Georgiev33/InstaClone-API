package com.example.demo.service.contracts;

import com.example.demo.model.dto.NotificationResponseDTO;
import com.example.demo.model.entity.User;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.Set;

public interface NotificationService {
    void addNotification(User user, String message);

    void addNotification(Set<User> users, String message);

    DeferredResult<List<NotificationResponseDTO>> getNotifications(String authToken);
}
