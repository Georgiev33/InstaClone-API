package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

@Entity(name = "notifications")
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String notification;
    @Column
    private LocalDateTime dateCreated;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
