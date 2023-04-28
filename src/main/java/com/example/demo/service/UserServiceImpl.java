package com.example.demo.service;

import com.example.demo.model.dto.User.UserLoginDTO;
import com.example.demo.model.dto.User.UserRegistrationDTO;
import com.example.demo.model.dto.User.UserUpdateDTO;
import com.example.demo.model.dto.User.UserWithUsernameAndIdDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.*;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.contracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.demo.util.Constants.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;
    private final JwtService jwtService;
    private final RoleService roleService;

    public String login(UserLoginDTO userLoginDTO) throws UserNotFoundException, AuthenticationException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginDTO.getUsername(),
                        userLoginDTO.getPassword()
                )
        );
        User user = findUserByUsername(userLoginDTO.getUsername());
        return jwtService.generateToken(Map.of("USER_ID", user.getId()), user);
    }

    public void createUser(UserRegistrationDTO userRegistrationDTO)
            throws PasswordMismatchException, UsernameAlreadyExist, EmailAlreadyExist {
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

    public void updateUser(UserUpdateDTO userUpdateDTO, String authToken) throws UserNotFoundException {
        long userId = jwtService.extractUserId(authToken);
        User user = findUserById(userId);
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

    public void followUser(long followedUserId, String authToken) throws BadRequestException {
        long followerId = jwtService.extractUserId(authToken);
        if (followerId == followedUserId) {
            throw new BadRequestException(USER_CAN_T_FOLLOW_ITSELF);
        }
        User followed = findUserById(followedUserId);
        User follower = findUserById(followerId);
        followed.getFollowers().add(follower);
        userRepository.save(followed);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
        return findUserByUsername(username);
    }

    public List<UserWithUsernameAndIdDTO> getFollowers(String authToken) throws UserNotFoundException {
        long userId = jwtService.extractUserId(authToken);
        return findUserById(userId)
                .getFollowers()
                .stream()
                .map(user -> new UserWithUsernameAndIdDTO(user.getUsername(), user.getId()))
                .toList();
    }

    public List<UserWithUsernameAndIdDTO> getFollowing(String authToken) throws UserNotFoundException {
        long userId = jwtService.extractUserId(authToken);
        return findUserById(userId)
                .getFollowing()
                .stream()
                .map(user -> new UserWithUsernameAndIdDTO(user.getUsername(), user.getId()))
                .toList();
    }


    public ResponseEntity<String> verifyUser(String verificationCode) throws InvalidValidationCode {
        User user = userRepository.findUserByVerificationCodeAndVerificationCodeIsFalse(verificationCode)
                .orElseThrow(() -> new InvalidValidationCode(INVALID_VERIFICATION_CODE));
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

    public void setPrivateUser(String authToken) throws UserNotFoundException {
        long userId = jwtService.extractUserId(authToken);
        User user = findUserById(userId);
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

    public User findUserByUsername(String username) throws UserNotFoundException {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
    }

    public User findUserById(long userId) throws UserNotFoundException {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
    }

    public void validateUserById(long userId) throws UserNotFoundException {
        findUserById(userId);
    }

    private boolean doesEmailExist(String email) {
        Optional<User> u = userRepository.findUserByEmail(email);
        return u.isPresent();
    }

    private boolean doesUsernameExist(String username) {
        Optional<User> u = userRepository.findUserByUsername(username);
        return u.isPresent();
    }

    private void validateUserRegistration(UserRegistrationDTO userRegistrationDTO)
            throws PasswordMismatchException, UsernameAlreadyExist, EmailAlreadyExist {
        if (!userRegistrationDTO.password().equals(userRegistrationDTO.confirmPassword())) {
            throw new PasswordMismatchException(PASSWORDS_MUST_MATCH);
        }
        if (doesUsernameExist(userRegistrationDTO.username())) {
            throw new UsernameAlreadyExist(USERNAME_ALREADY_EXISTS);
        }

        if (doesEmailExist(userRegistrationDTO.email())) {
            throw new EmailAlreadyExist(EMAIL_ALREADY_EXISTS);
        }
    }
}
