package com.example.demo.model.entity.post;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "posts_content")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "content_url")
    private String contentUrl;
    @ManyToOne()
    @JoinColumn(name = "post_id")
    private Post post;
}
