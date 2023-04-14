package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Getter
@Setter
@Entity(name = "authorities")
@NoArgsConstructor
public class Role implements GrantedAuthority {
    @Id
    private Long id;
    private String authority;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_autorities",
            joinColumns = @JoinColumn(name = "autority_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    Set<User> users;

}
