package com.example.demo.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity(name = "blacklisted_tokens")
@Setter
@Getter
@NoArgsConstructor
public class BlackListToken {
    public BlackListToken(String token){
        this.token = token;
    }
    @Id
    private String token;
}
