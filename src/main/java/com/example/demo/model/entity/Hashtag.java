package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "hashtags")
@Getter
@Setter
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "tag_name")
    private String tagName;
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "hashtags")
    private Set<Post> posts = new HashSet<>();
}
