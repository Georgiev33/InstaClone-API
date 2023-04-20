package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private boolean status;
}
