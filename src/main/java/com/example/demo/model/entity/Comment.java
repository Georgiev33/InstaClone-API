package com.example.demo.model.entity;

import com.example.demo.model.Postable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "comments")
@Getter
@Setter
public class Comment implements Postable {
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
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "comment_user_tag",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> taggedUsers = new HashSet<>();
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "hashtags_comments",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    private Set<Hashtag> hashtags = new HashSet<>();

    private LocalDateTime createdAt;
}
