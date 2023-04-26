package com.example.demo.service;

import com.example.demo.model.dto.User.UserLoginDTO;
import com.example.demo.model.dto.User.UserRegistrationDTO;
import com.example.demo.model.dto.User.UserUpdateDTO;
import com.example.demo.model.dto.User.UserWithUsernameAndIdDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.AccessDeniedException;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.demo.util.Constants.*;

@Service
@RequiredArgsConstructor()
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;
    private final JwtService jwtService;
    private final RoleService roleService;

    public String login(UserLoginDTO userLoginDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginDTO.getUsername(),
                        userLoginDTO.getPassword()
                )
        );
        User user = findUserByUsernamedOrThrownException(userLoginDTO.getUsername());
        return jwtService.generateToken(Map.of("USER_ID", user.getId()), user);
    }

    public void createUser(UserRegistrationDTO userRegistrationDTO) {
        validateUserRegistration(userRegistrationDTO);
        User user = User.builder()
                .username(userRegistrationDTO.username())
                .email(userRegistrationDTO.email())
                .password(encoder.encode(userRegistrationDTO.password()))
                .roles(List.of(roleService.findRole(USER)))
                .verificationCode(mailService.sendVerificationEmail(userRegistrationDTO))
                .bio(userRegistrationDTO.bio())
                .build();
        userRepository.save(user);
    }

    public void updateUser(UserUpdateDTO userUpdateDTO, String authToken) {
        long userId = jwtService.extractUserId(authToken);
        User user = findUserByIdOrThrownException(userId);
        User updatedUser = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .verificationCode(user.getVerificationCode())
                .isVerified(user.isVerified())
                .isPrivate(user.isPrivate())
                .bio(userUpdateDTO.bio())
                .build();
        userRepository.save(updatedUser);
    }

    public void followUser(long followedUserId, String authToken) {
        long followerId = jwtService.extractUserId(authToken);
        if (followerId == followedUserId) {
            throw new BadRequestException(USER_CAN_T_FOLLOW_ITSELF);
        }
        User followed = findUserByIdOrThrownException(followedUserId);
        User follower = findUserByIdOrThrownException(followerId);
        followed.getFollowers().add(follower);
        userRepository.save(followed);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return findUserByUsernamedOrThrownException(username);
    }

    public List<UserWithUsernameAndIdDTO> getFollowers(String authToken) {
        long userId = jwtService.extractUserId(authToken);
        return findUserByIdOrThrownException(userId)
                .getFollowers()
                .stream()
                .map(user -> new UserWithUsernameAndIdDTO(user.getUsername(), user.getId()))
                .toList();
    }

    public List<UserWithUsernameAndIdDTO> getFollowing(String authToken) {
        long userId = jwtService.extractUserId(authToken);
        return findUserByIdOrThrownException(userId)
                .getFollowing()
                .stream()
                .map(user -> new UserWithUsernameAndIdDTO(user.getUsername(), user.getId()))
                .toList();
    }


    public ResponseEntity<String> verifyUser(String verificationCode) {
        User user = userRepository.findUserByVerificationCodeAndVerificationCodeIsFalse(verificationCode)
                .orElseThrow(() -> new BadRequestException(INVALID_VERIFICATION_CODE));
        User verifiedUser = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .verificationCode(user.getVerificationCode())
                .isVerified(true)
                .isPrivate(user.isPrivate())
                .bio(user.getBio())
                .build();
        userRepository.save(verifiedUser);
        return ResponseEntity.ok(REGISTRATION_SUCCESSFULLY_VERIFIED);
    }

    public void setPrivateUser(String authToken) {
        long userId = jwtService.extractUserId(authToken);
        User user = findUserByIdOrThrownException(userId);
        User updatedUser = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .verificationCode(user.getVerificationCode())
                .isVerified(user.isVerified())
                .isPrivate(true)
                .bio(user.getBio())
                .build();
        userRepository.save(updatedUser);
    }

    public User findUserByUsernamedOrThrownException(String username) {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));
    }

    public User findUserByIdOrThrownException(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));
    }

    public void validateUserById(long userId) {
        findUserByIdOrThrownException(userId);
    }

    public void hasPermission(User targetUser) {
        if (targetUser.isPrivate() && !AdminService.isLoggedUserAdmin() && !isLoggedUserFollow(targetUser)) {
            throw new AccessDeniedException(ACCESS_DENIED);
        }
    }


    private boolean isLoggedUserFollow(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return user.getFollowers().stream()
                .map(User::getUsername)
                .anyMatch(u -> u.contains(authentication.getName()));
    }

    private boolean doesEmailExist(String email) {
        Optional<User> u = userRepository.findUserByEmail(email);
        return u.isPresent();
    }

    private boolean doesUsernameExist(String username) {
        Optional<User> u = userRepository.findUserByUsername(username);
        return u.isPresent();
    }

    private void validateUserRegistration(UserRegistrationDTO userRegistrationDTO) {
        if (!userRegistrationDTO.password().equals(userRegistrationDTO.confirmPassword())) {
            throw new BadRequestException(PASSWORDS_MUST_MATCH);
        }
        if (doesUsernameExist(userRegistrationDTO.username())) {
            throw new BadRequestException(USERNAME_ALREADY_EXISTS);
        }

        if (doesEmailExist(userRegistrationDTO.email())) {
            throw new BadRequestException(EMAIL_ALREADY_EXISTS);
        }
    }
}
