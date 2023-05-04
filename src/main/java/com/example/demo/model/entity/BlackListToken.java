package com.example.demo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity(name = "blacklisted_tokens")
@Getter
@NoArgsConstructor
public class BlackListToken {
    public BlackListToken(String token){
        this.token = token;
    }
    @Id
    @Column(name = "token")
    private String token;
}
