package com.example.demo.model.dto;

import java.time.LocalDateTime;

public record NotificationResponseDTO(String notification, LocalDateTime dateCreated) {
}
