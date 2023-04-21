package com.example.demo.model.entity;

import com.example.demo.model.Postable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity(name = "stories")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Story implements Postable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "is_deleted")
    private boolean isDeleted;
    @Column(name = "date_created")
    private LocalDateTime dateCreated;
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;
    @Column(name = "content_url")
    private String contentUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "story")
    Set<UserStoryReaction> reactions;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "stories_hashtag",
            joinColumns = @JoinColumn(name = "story_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    private Set<Hashtag> hashtags;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "stories_user_tag",
            joinColumns = @JoinColumn(name = "story_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> userTags;
}
