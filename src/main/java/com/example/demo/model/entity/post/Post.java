package com.example.demo.model.entity.post;
import com.example.demo.model.Ownable;
import com.example.demo.model.Postable;
import com.example.demo.model.entity.Comment;
import com.example.demo.model.entity.Hashtag;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.UserPostReaction;
import com.example.demo.model.exception.InvalidOwnerException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.example.demo.util.constants.MessageConstants.INVALID_OWNER_MESSAGE;

@Entity(name = "posts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post implements Postable, Ownable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    private List<PostContent> contentUrls;
    @Column(name = "caption")
    private String caption;
    @Column(name = "is_deleted")
    private boolean isDeleted;
    @Column(name = "date_created")
    private LocalDateTime dateCreated;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "post")
    Set<UserPostReaction> ratings;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "hashtags_posts",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Hashtag> hashtags;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_user_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> userTags;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Comment> comments;

    @Override
    public void verifyOwnerIdOrThrow(long userId) throws InvalidOwnerException {
        if(!this.user.getId().equals(userId)){
            throw new InvalidOwnerException(INVALID_OWNER_MESSAGE);
        }
    }
}
