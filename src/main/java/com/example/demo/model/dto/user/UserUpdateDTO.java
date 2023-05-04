package com.example.demo.model.dto.user;

import jakarta.validation.constraints.NotNull;

public record UserUpdateDTO(@NotNull String bio) {
}
