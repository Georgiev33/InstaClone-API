package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

@Entity(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
    private String verificationCode;
    private boolean isVerified;
    private boolean isPrivate;
    private String bio;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Comment> comments;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "following",
            joinColumns = {@JoinColumn(name = "following_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private Set<User> followers = new HashSet<>();
    @ManyToMany(mappedBy = "taggedUsers", fetch = FetchType.LAZY)
    private Set<Comment> taggedComments;
    @ManyToMany(mappedBy = "userTags", fetch = FetchType.LAZY)
    private Set<Post> taggedPosts = new HashSet<>();
    @ManyToMany(mappedBy = "userTags", fetch = FetchType.LAZY)
    private Set<Post> taggedStories = new HashSet<>();
    @ManyToMany(mappedBy = "followers", fetch = FetchType.LAZY)
    private Set<User> following = new HashSet<>();
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserPostReaction> ratings;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Notification> notifications;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_autorities",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "autority_id")
    )
    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @Override
    public boolean isEnabled() {
        return isVerified;
    }
}
