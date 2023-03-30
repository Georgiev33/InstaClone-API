package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
    private String verificationCode;
    private boolean isVerified;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "following",
            joinColumns = { @JoinColumn(name = "following_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    Set<User> followers = new HashSet<>();
    @ManyToMany(mappedBy = "followers")
    private Set<User> following = new HashSet<>();
}
