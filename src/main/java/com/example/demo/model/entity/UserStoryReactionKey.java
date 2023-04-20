package com.example.demo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStoryReactionKey implements Serializable {
    @Serial
    private static final long serialVersionUID = -5124123850693554092L;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "story_id")
    private Long storyId;
}
