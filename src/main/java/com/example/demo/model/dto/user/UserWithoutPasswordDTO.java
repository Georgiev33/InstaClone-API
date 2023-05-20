package com.example.demo.model.dto.user;

public record UserWithoutPasswordDTO(long id,
                                     String username,
                                     String bio,
                                     int postCount,
                                     long followers,
                                     long following) {
}
