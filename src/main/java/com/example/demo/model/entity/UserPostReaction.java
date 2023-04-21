package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "users_posts_reactions")
public class UserPostReaction {
    @EmbeddedId
    private UserPostReactionKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
    private boolean status;
}
