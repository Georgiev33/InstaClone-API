package com.example.demo.service;

import com.example.demo.model.dto.ReportedUsers.ReportUserDTO;
import com.example.demo.model.dto.user.UserRegistrationDTO;
import com.example.demo.model.dto.user.UserUpdateDTO;
import com.example.demo.model.dto.user.UserWithUsernameAndIdDTO;
import com.example.demo.model.entity.Role;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.report.ReportedUser;
import com.example.demo.model.exception.*;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.report.ReportedUserRepository;
import com.example.demo.service.contracts.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.example.demo.util.constants.Constants.USER;
import static com.example.demo.util.constants.MessageConstants.USER_CAN_T_FOLLOW_ITSELF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImpTest {

    public static final String TEST_JWT_TOKEN = "testJwtToken";
    private UserService userService;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserValidationService userValidationService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private MailService mailService;
    @Mock
    private JwtService jwtService;
    @Mock
    private RoleService roleService;
    @Mock
    private ReportedUserRepository reportedUsersRepository;
    @Mock
    private PostRepository postRepository;

    @BeforeEach
    public void setup() {
        userService = new UserServiceImpl(userRepository, postRepository, encoder, userValidationService, authenticationManager
                , mailService, jwtService, roleService, reportedUsersRepository);
    }

    @Test
    public void createUserWithValidDateShouldCreateUser() {
        //arrange
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO("testUser", "testEmail",
                "password", "password", "testBio");

        when(roleService.findRole(USER)).thenReturn(Role.builder().id(1L).authority(USER).build());
        when(mailService.sendVerificationEmail(userRegistrationDTO)).thenReturn("testVerificationCode");
        //act
        userService.createUser(userRegistrationDTO);
        //assert

        //verify(userValidationService, times(1)).validateUserRegistration(eq(userRegistrationDTO));
        //times(1) is default value
        verify(userValidationService).validateUserRegistration(eq(userRegistrationDTO));
        verify(mailService).sendVerificationEmail(eq(userRegistrationDTO));
        verify(roleService).findRole(eq(USER));
        verify(userRepository).save(captor.capture());
        User captured = captor.getValue();
        User expectedUser = User.builder()
                .username(userRegistrationDTO.username())
                .email(userRegistrationDTO.email())
                .password(encoder.encode(userRegistrationDTO.password()))
                .bio(userRegistrationDTO.bio())
                .roles(List.of(Role.builder().id(1L).authority(USER).build()))
                .verificationCode("testVerificationCode")
                .build();
        // Check all fields except for the password
        assertThat(captured).usingRecursiveComparison()
                .ignoringFields("password")
                .isEqualTo(expectedUser);
        // Check the password separately
        assertThat(encoder.matches(userRegistrationDTO.password(), captured.getPassword())).isTrue();
    }

    @Test
    public void createUserWithMismatchPasswordShouldThrowPasswordMismatchExceptionException() {
        // Arrange
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO("testUser", "testEmail",
                "password", "password", "testBio");

        // Let's say that validateUserRegistration() should throw an PasswordMisMatchExistException
        doThrow(PasswordMismatchException.class).when(userValidationService).validateUserRegistration(any(UserRegistrationDTO.class));

        // Act and Assert
        assertThrows(PasswordMismatchException.class, () -> userService.createUser(userRegistrationDTO));
    }

    @Test
    public void createUserWithAlreadyExistUsernameShouldThrowUsernameAlreadyExistException() {
        // Arrange
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO("testUser", "testEmail",
                "password", "password", "testBio");

        // Let's say that validateUserRegistration() should throw an UsernameAlreadyExistException
        doThrow(UsernameAlreadyExist.class).when(userValidationService).validateUserRegistration(any(UserRegistrationDTO.class));

        // Act and Assert
        assertThrows(UsernameAlreadyExist.class, () -> userService.createUser(userRegistrationDTO));
    }

    @Test
    public void createUserWithAlreadyExistEmailShouldThrowEmailAlreadyExistException() {
        // Arrange
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO("testUser", "testEmail",
                "password", "password", "testBio");

        // Let's say that validateUserRegistration() should throw an EmailAlreadyExistException
        doThrow(EmailAlreadyExist.class).when(userValidationService).validateUserRegistration(any(UserRegistrationDTO.class));

        // Act and Assert
        assertThrows(EmailAlreadyExist.class, () -> userService.createUser(userRegistrationDTO));
    }

    @Test
    public void updateUserShouldUpdateUser() {
        //arrange
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO("newBio");
        User userToUpdate = User.builder()
                .id(1L)
                .username("testUsername")
                .email("testEmail")
                .password("testPassword")
                .bio("oldBio")
                .verificationCode("testVerificationCode")
                .isVerified(true)
                .isPrivate(true)
                .build();
        when(jwtService.extractUserId(anyString())).thenReturn(1L);
        when(userValidationService.findUserById(anyLong())).thenReturn(userToUpdate);
        //act
        userService.updateUser(userUpdateDTO, TEST_JWT_TOKEN);

        //assert
        verify(userRepository).save(captor.capture());
        User captured = captor.getValue();
        assertThat(captured.getBio()).isEqualTo("newBio");

        //assertion that checks that the rest of the fields don`t change
        assertThat(captured).usingRecursiveComparison()
                .ignoringFields("bio")
                .isEqualTo(userToUpdate);

    }

    @Test
    public void updateUserWithInvalidUserIdShouldThrowUserNotFoundException() {
        //arrange
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO("newBio");
        doThrow(UserNotFoundException.class).when(userValidationService).findUserById(anyLong());

        //act and assert
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userUpdateDTO, TEST_JWT_TOKEN));
    }

    @Test
    public void followUserShouldThrowBadRequestExceptionIfFollowerIdAndFollowingIdAreEquals() {
        //arrange
        long followedUserId = 1L;
        when(jwtService.extractUserId(anyString())).thenReturn(1L);
        //act and assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> {
                    userService.followUser(followedUserId, TEST_JWT_TOKEN);
                })
                .withMessage(USER_CAN_T_FOLLOW_ITSELF);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void followUserShouldThrowUserNotFoundExceptionIfFollowedUserIdIsInvalid() {
        //arrange
        long followedUserId = 1L;
        when(jwtService.extractUserId(anyString())).thenReturn(2L);
        doThrow(UserNotFoundException.class).when(userValidationService).findUserById(anyLong());
        //act and assert
        assertThrows(UserNotFoundException.class, () -> userService.followUser(followedUserId, TEST_JWT_TOKEN));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void followUserShouldFollowUser() {
        //arrange
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        User followedUser = User.builder()
                .id(1L)
                .username("followedUser")
                .followers(new HashSet<>())
                .build();
        User followingUser = User.builder()
                .id(2L)
                .username("followingUser")
                .build();
        long followedUserId = 1L;
        when(jwtService.extractUserId(anyString())).thenReturn(2L);
        when(userValidationService.findUserById(1L)).thenReturn(followedUser);
        when(userValidationService.findUserById(2L)).thenReturn(followingUser);

        //act
        userService.followUser(followedUserId, TEST_JWT_TOKEN);

        //assert
        verify(userRepository).save(captor.capture());
        User captured = captor.getValue();
        assertThat(captured.getFollowers().contains(followingUser)).isTrue();
        assertThat(captured.getFollowers().size() == 1).isTrue();
        verify(userValidationService).findUserById(eq(1L));
        verify(userValidationService).findUserById(eq(2L));
        verify(userRepository).save(eq(followedUser));
    }

    @Test
    public void loadUserByUsernameShouldReturnUserDetailsWhenUsernameExists() {
        //arrange
        String username = "existingUser";
        User existUser = User.builder().build();
        when(userValidationService.getUsernameForLoginOrThrowException(username)).thenReturn(existUser);

        //act
        UserDetails result = userService.loadUserByUsername(username);

        //assert
        verify(userValidationService).getUsernameForLoginOrThrowException(username);
        assertEquals(existUser, result);
    }

    @Test
    public void loadUserByUsernameShouldThrowUserNotFoundExceptionWhenUsernameIsInvalid() {
        //arrange
        String username = "invalidUser";
        doThrow(UserNotFoundException.class).when(userValidationService).getUsernameForLoginOrThrowException(anyString());
        //act and assert
        assertThrows(UserNotFoundException.class, () -> userService.loadUserByUsername(username));
    }

    @Test
    public void getFollowersShouldThrowUserNotFoundExceptionIfUsernameIsInvalid() {
        //arrange
        when(jwtService.extractUserId(anyString())).thenReturn(1L);
        doThrow(UserNotFoundException.class).when(userValidationService).findUserById(anyLong());
        //act and assert
        assertThrows(UserNotFoundException.class, () -> userService.getFollowers(TEST_JWT_TOKEN));
    }

    @Test
    public void getFollowersShouldReturnListOfUserWithUsernameAndId() {
        //arrange
        when(jwtService.extractUserId(anyString())).thenReturn(1L);
        when(userValidationService.findUserById(anyLong())).thenReturn(User.builder().followers(new HashSet<>()).build());

        //act
        List<UserWithUsernameAndIdDTO> followers = userService.getFollowers(anyString());

        //assert
        assertThat(followers).isNotNull();
    }

    @Test
    public void getFollowingsShouldThrowUserNotFoundExceptionIfUsernameIsInvalid() {
        //arrange
        when(jwtService.extractUserId(anyString())).thenReturn(1L);
        doThrow(UserNotFoundException.class).when(userValidationService).findUserById(anyLong());
        //act and assert
        assertThrows(UserNotFoundException.class, () -> userService.getFollowings(TEST_JWT_TOKEN));
    }

    @Test
    public void getFollowingShouldReturnListOfUserWithUsernameAndId() {
        //arrange
        when(jwtService.extractUserId(anyString())).thenReturn(1L);
        when(userValidationService.findUserById(anyLong())).thenReturn(User.builder().following(new HashSet<>()).build());

        //act
        List<UserWithUsernameAndIdDTO> followers = userService.getFollowings(anyString());

        //assert
        assertThat(followers).isNotNull();
    }

    @Test
    public void setPrivateUserShouldThrowUserNotFoundExceptionIfUsernameIsInvalid() {
        //arrange
        when(jwtService.extractUserId(anyString())).thenReturn(1L);
        doThrow(UserNotFoundException.class).when(userValidationService).findUserById(anyLong());
        //act and assert
        assertThrows(UserNotFoundException.class, () -> userService.setPrivateUser(TEST_JWT_TOKEN));
    }

    @Test
    public void serPrivateUserShouldSetPrivateUser() {
        //arrange
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(jwtService.extractUserId(anyString())).thenReturn(1L);
        User userToBePrivate = User.builder()
                .id(1L)
                .username("testUsername")
                .email("testEmail")
                .password("testPassword")
                .bio("oldBio")
                .verificationCode("testVerificationCode")
                .isVerified(true)
                .isPrivate(false)
                .build();
        when(userValidationService.findUserById(anyLong())).thenReturn(userToBePrivate);

        //act
        userService.setPrivateUser(TEST_JWT_TOKEN);

        //assert
        verify(userRepository).save(captor.capture());
        User captured = captor.getValue();
        assertThat(captured.isPrivate()).isTrue();
        assertThat(captured).usingRecursiveComparison()
                .ignoringFields("isPrivate")
                .isEqualTo(userToBePrivate);

    }

    @Test
    public void reportUserShouldThrowReportedUserAlreadyExistExceptionWhenReportedUserIsAlreadyReported() {
        //arrange
        ReportUserDTO reportUserDTO = new ReportUserDTO(1, "testReason");
        when(jwtService.extractUserId(anyString())).thenReturn(1L);
        when(reportedUsersRepository.findByReporterIdAndReportedId(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(ReportedUser.builder().build()));
        //act and assert
        assertThatExceptionOfType(ReportedUserAlreadyExist.class)
                .isThrownBy(() -> {
                    userService.reportUser(reportUserDTO, TEST_JWT_TOKEN);
                })
                .withMessage("User with id" + reportUserDTO.reportedId() + " is already reported");
        verify(userValidationService, never()).throwExceptionIfUserNotFound(anyLong());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void reportUserShouldThrowUserNotFoundExceptionWhenReportedUserIdIsInvalid() {
        //arrange
        ReportUserDTO reportUserDTO = new ReportUserDTO(1, "testReason");
        when(jwtService.extractUserId(anyString())).thenReturn(1L);
        when(reportedUsersRepository.findByReporterIdAndReportedId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        doThrow(UserNotFoundException.class).when(userValidationService).throwExceptionIfUserNotFound(anyLong());
        //act and assert
        assertThrows(UserNotFoundException.class, () -> userService.reportUser(reportUserDTO, TEST_JWT_TOKEN));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void reportUserShouldReportUser() {
        //arrange
        ArgumentCaptor<ReportedUser> captor = ArgumentCaptor.forClass(ReportedUser.class);
        ReportUserDTO reportUserDTO = new ReportUserDTO(2, "testReason");
        when(jwtService.extractUserId(anyString())).thenReturn(1L);
        ReportedUser expectedReportedUser = ReportedUser.builder()
                .reporterId(1L)
                .reportedId(reportUserDTO.reportedId())
                .reason(reportUserDTO.reason())
                .build();
        when(reportedUsersRepository.findByReporterIdAndReportedId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        //act
        userService.reportUser(reportUserDTO, TEST_JWT_TOKEN);

        // assert
        verify(reportedUsersRepository).save(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedReportedUser);
        verify(userValidationService).throwExceptionIfUserNotFound(eq(reportUserDTO.reportedId()));
        verify(reportedUsersRepository).findByReporterIdAndReportedId(eq(1L), eq(reportUserDTO.reportedId()));
    }

    @Test
    public void gertUserByIdShouldThrowUserNotFoundExceptionWhenReportedUserIdIsInvalid() {
        //arrange
        ReportUserDTO reportUserDTO = new ReportUserDTO(1, "testReason");
        when(jwtService.extractUserId(anyString())).thenReturn(1L);
        when(reportedUsersRepository.findByReporterIdAndReportedId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        doThrow(UserNotFoundException.class).when(userValidationService).throwExceptionIfUserNotFound(anyLong());
        //act and assert
        assertThrows(UserNotFoundException.class, () -> userService.reportUser(reportUserDTO, TEST_JWT_TOKEN));
        verify(userRepository, never()).save(any(User.class));
    }


}
