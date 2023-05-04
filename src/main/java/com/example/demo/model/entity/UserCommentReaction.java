package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Entity(name = "users_comments_reactions")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCommentReaction {
    @EmbeddedId
    private UserCommentReactionKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("commentId")
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "status")
    private boolean status;


    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserCommentReactionKey implements Serializable {
        @Serial
        private static final long serialVersionUID = -51241250693584292L;
        @Column(name = "user_id")
        private Long userId;
        @Column(name = "comment_id")
        private Long commentId;
    }
}
