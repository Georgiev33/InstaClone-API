package com.example.demo.service;
import static com.example.demo.util.constants.MessageConstants.INVALID_OWNER_MESSAGE;
import static com.example.demo.util.constants.MessageConstants.POST_REACTION_MESSAGE;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.example.demo.model.dto.post.CreatePostDTO;
import com.example.demo.model.dto.post.PostResponseDTO;
import com.example.demo.model.entity.Hashtag;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.UserPostReaction;
import com.example.demo.model.entity.post.Post;
import com.example.demo.model.entity.post.PostContent;
import com.example.demo.model.exception.InvalidOwnerException;
import com.example.demo.model.exception.PostNotFoundException;
import com.example.demo.repository.PostContentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserPostReactionRepository;
import com.example.demo.service.contracts.*;
import com.example.demo.util.constants.MessageConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;


@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {


    private PostServiceImpl postService;
    @Mock
    private FileService fileService;
    @Mock
    private UserValidationService userValidationService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostContentRepository contentRepository;
    @Mock
    private HashTagService hashTagService;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserPostReactionRepository userPostReactionRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private AdminService adminService;

    private static final String AUTH_TOKEN = "dummyAuthToken";
    private static final long USER_ID = 1L;
    private static final long POST_ID = 10L;

    @BeforeEach
    public void setUp() {
        lenient().when(jwtService.extractUserId(AUTH_TOKEN)).thenReturn(USER_ID);
        postService = new PostServiceImpl(fileService, userValidationService,postRepository,contentRepository,hashTagService,jwtService,userPostReactionRepository,notificationService,adminService);
    }

    @Test
    public void createPostWithValidDataShouldCreatePost(){
        // Arrange
        CreatePostDTO dto = new CreatePostDTO("test",
                List.of(new MockMultipartFile("testFile", "123".getBytes())),
                Optional.of(List.of("testHashtag")),Optional.of(List.of("testUserTag")));
        User user = User
                .builder()
                .build();
        when(userValidationService.findUserById(anyLong())).thenReturn(user);
        Post post = Post
                .builder()
                .id(POST_ID)
                .contentUrls(List.of(new PostContent(null, null, null)))
                .hashtags(Set.of(new Hashtag()))
                .userTags(Set.of(new User()))
                .build();
        when(postRepository.save(any(Post.class))).thenReturn(post);

        // Act
        PostResponseDTO postResponseDTO = postService.createPost(dto, AUTH_TOKEN);

        // Assert
        assertThat(postResponseDTO).isNotNull();
        verify(hashTagService, times(1)).addHashTags(eq(dto.hashtags()), any(Post.class));
        verify(fileService, times(1)).createContent(eq(dto.content()), eq(USER_ID), any(Post.class));
    }

    @Test
    public void getAllPostUrlsWithInvalidPostIdShouldThrowException(){
        //Arrange
        when(contentRepository.findAllByPostId(anyLong())).thenReturn(Optional.empty());
        //Act

        //Assert
        assertThatExceptionOfType(PostNotFoundException.class).isThrownBy(() -> postService.getAllPostUrls(POST_ID)).withMessage(MessageConstants.POST_NOT_FOUND);
    }
    @Test
    public void getAllPostUrlsWithValidPostIdShouldntThrowException(){
        //Arrange

        when(contentRepository.findAllByPostId(anyLong())).thenReturn(Optional.of(List.of(PostContent.builder().build())));
        //Act

        //Assert
        assertThatNoException().isThrownBy(() -> postService.getAllPostUrls(POST_ID));

    }

    @Test
    public void findPostByIdWithInvalidPostIdShouldThrowException(){
        //Arrange
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        //Act

        //Assert
        assertThatExceptionOfType(PostNotFoundException.class).isThrownBy(() -> postService.findPostById(POST_ID)).withMessage(MessageConstants.POST_NOT_FOUND);
    }
    @Test
    public void findPostByIdWithValidIdShouldntThrowException(){
        //Arrange
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(Post.builder().build()));
        //Act

        //Assert
        assertThatNoException().isThrownBy(() -> postService.findPostById(POST_ID));

    }
    @Test
    public void reactionThatDoesntExistShouldBeCreated(){
        // Arrange
        final long reactingUserId = 1L;
        final String reactingUsername = "testUsername";
        User reactingUser = User.builder().id(reactingUserId).username(reactingUsername).build();
        User postOwner = User.builder().build();
        when(userValidationService.findUserById(anyLong())).thenReturn(reactingUser);
        Post testPost = Post.builder().id(POST_ID).user(postOwner).build();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(testPost));
        when(userPostReactionRepository.findById(any(UserPostReaction.UserPostReactionKey.class))).thenReturn(Optional.empty());

        // Act
        postService.react(AUTH_TOKEN, POST_ID, true);

        // Assert
        verify(notificationService, times(1)).addNotification(eq(testPost.getUser()), eq(reactingUsername + POST_REACTION_MESSAGE));
        verify(userPostReactionRepository, times(1)).save(any(UserPostReaction.class));
        verify(userPostReactionRepository, never()).delete(any(UserPostReaction.class));
    }

    @Test
    public void reactionThatExistsWithSameStatusShouldBeDeleted(){
        // Arrange
        final long reactingUserId = 1L;
        final String reactingUsername = "testUsername";
        User reactingUser = User.builder().id(reactingUserId).username(reactingUsername).build();
        User postOwner = User.builder().build();
        when(userValidationService.findUserById(anyLong())).thenReturn(reactingUser);
        Post testPost = Post.builder().id(POST_ID).user(postOwner).build();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(testPost));
        when(userPostReactionRepository.findById(any(UserPostReaction.UserPostReactionKey.class)))
                .thenReturn(Optional.of(UserPostReaction
                        .builder()
                        .post(testPost)
                        .user(reactingUser)
                        .status(true)
                        .build()));

        // Act
        postService.react(AUTH_TOKEN, POST_ID, true);

        // Assert
        verify(notificationService, never()).addNotification(any(User.class), anyString());
        verify(userPostReactionRepository, never()).save(any(UserPostReaction.class));
        verify(userPostReactionRepository, times(1)).delete(any(UserPostReaction.class));
    }

    @Test
    public void deletePostWithInvalidPostIdShouldThrowException(){
        //Arrange
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        //Act

        //Assert
        assertThatExceptionOfType(PostNotFoundException.class).isThrownBy(() -> postService.deletePostById(POST_ID, AUTH_TOKEN));
    }

    @Test
    public void deletePostWithValidPostIdShouldntThrowException(){
        //Arrange
        User owner = User.builder().id(USER_ID).build();
        Post testPost = Post.builder().user(owner).build();
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(testPost));

        //Act

        //Assert
        assertThatNoException().isThrownBy(() -> postService.deletePostById(POST_ID, AUTH_TOKEN));
    }

    @Test
    public void deletePostWithValidOwnerShouldDeletePost(){
        //Arrange
        User owner = User.builder().id(USER_ID).build();
        Post testPost = Post.builder().user(owner).build();
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(testPost));

        //Act
        postService.deletePostById(POST_ID, AUTH_TOKEN);
        //Assert
        verify(postRepository, times(1)).delete(eq(testPost));
        assertThatNoException().isThrownBy(() -> postService.deletePostById(POST_ID,AUTH_TOKEN));
    }

    @Test
    public void deletePostWithInvalidOwnerShouldThrowException(){
        //Arrange
        final long fakeOwnerId = 2L;
        when(jwtService.extractUserId(AUTH_TOKEN)).thenReturn(fakeOwnerId);
        User owner = User.builder().id(USER_ID).build();
        Post testPost = Post.builder().user(owner).build();
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(testPost));

        //Act

        //Assert
        assertThatExceptionOfType(InvalidOwnerException.class)
                .isThrownBy(() -> postService.deletePostById(POST_ID, AUTH_TOKEN)).withMessage(INVALID_OWNER_MESSAGE);
    }

    @Test
    public void deletePostWithAdminIdShouldDeletePost(){
        //Arrange
        final long adminId = 2L;
        when(jwtService.extractUserId(AUTH_TOKEN)).thenReturn(adminId);
        when(adminService.isLoggedUserAdmin()).thenReturn(true);
        User owner = User.builder().id(USER_ID).build();
        Post testPost = spy(Post.builder().user(owner).build());
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(testPost));

        //Act
        postService.deletePostById(POST_ID, AUTH_TOKEN);
        //Assert
        verify(postRepository,times(1)).delete(eq(testPost));
        verify(testPost, never()).verifyOwnerIdOrThrow(anyLong());

    }

}