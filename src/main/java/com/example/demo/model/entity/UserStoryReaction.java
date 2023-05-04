package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "users_stories_reactions")
public class UserStoryReaction {
    @EmbeddedId
    private UserStoryReactionKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("storyId")
    @JoinColumn(name = "story_id")
    private Story story;

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
    public static class UserStoryReactionKey implements Serializable {
        @Serial
        private static final long serialVersionUID = -5124123850693554092L;
        @Column(name = "user_id")
        private Long userId;
        @Column(name = "story_id")
        private Long storyId;
    }
}
