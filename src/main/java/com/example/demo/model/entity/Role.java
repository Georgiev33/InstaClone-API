package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Getter
@Setter
@Entity(name = "authorities")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role implements GrantedAuthority {
    @Id
    private Long id;
    @Column(name = "authority")
    private String authority;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_authorities",
            joinColumns = @JoinColumn(name = "autority_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    Set<User> users;
}
