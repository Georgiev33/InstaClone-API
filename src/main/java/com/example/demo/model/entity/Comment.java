package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    private String content;
    @ManyToOne
    private Post post;
    @ManyToOne
    private Comment repliedComment;
    @OneToMany(mappedBy = "repliedComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Comment> replies;
    private LocalDateTime createdAt;
}
