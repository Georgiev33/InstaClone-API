package com.example.demo.service;

import com.example.demo.model.dto.NotificationResponseDTO;
import com.example.demo.model.entity.Notification;
import com.example.demo.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JwtService jwtService;
    private final ExecutorService executorService;
    private final NotificationRepository notificationRepository;

    public DeferredResult<List<NotificationResponseDTO>> getNotifications(String authToken) {
        DeferredResult<List<NotificationResponseDTO>> deferredResult = new DeferredResult<>();
        SecurityContext originalSecurityContext = SecurityContextHolder.getContext();

        Runnable setResult = () -> {
            try {
                SecurityContextHolder.setContext(originalSecurityContext);
                List<NotificationResponseDTO> responseDTOS = pollForNotifications(authToken, 10);
                deferredResult.setResult(responseDTOS);

            } catch (InterruptedException e) {
                deferredResult.setErrorResult(e);
            } finally {
                SecurityContextHolder.clearContext();
            }
        };
        executorService.execute(setResult);

        return deferredResult;
    }

    private List<NotificationResponseDTO>
    pollForNotifications(String authToken, long timeout) throws InterruptedException {
        long userId = jwtService.extractUserId(authToken);
        long startTime = System.currentTimeMillis();
        long endTime = startTime + TimeUnit.SECONDS.toMillis(timeout);

        while (System.currentTimeMillis() < endTime) {

            List<Notification> notifications = notificationRepository.findAllByUserId(userId);
            if (notifications != null && !notifications.isEmpty()) {
                System.out.println(notifications.get(0).getNotification());
                notificationRepository.deleteAll(notifications);

                return notifications
                        .stream()
                        .map(n -> new NotificationResponseDTO(n.getNotification(), n.getDateCreated()))
                        .toList();
            }
            Thread.sleep(1000);
        }
        return Collections.emptyList();
    }
}
