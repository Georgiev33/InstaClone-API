package com.example.demo.service;

import com.example.demo.model.dto.User.UserLoginDTO;
import com.example.demo.model.dto.User.UserRegistrationDTO;
import com.example.demo.model.dto.User.UserUpdateDTO;
import com.example.demo.model.dto.User.UserWithUsernameAndIdDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.*;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.contracts.UserService;
import com.example.demo.service.contracts.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.demo.util.Constants.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final UserValidationService userValidationService;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;
    private final JwtService jwtService;
    private final RoleService roleService;
@Override
    public String login(UserLoginDTO userLoginDTO) throws BannedUserException,
            UsernameNotFoundException, UserNotVerifiedException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginDTO.getUsername(),
                        userLoginDTO.getPassword()
                )
        );
        User user = userValidationService.getUsernameForLoginOrThrowException(userLoginDTO.getUsername());
        userValidationService.throwExceptionIfUserIsBanned(user.getId());
        userValidationService.throwExceptionIfUserNotVerified(user.isVerified());
        return jwtService.generateToken(Map.of("USER_ID", user.getId()), user);
    }
    @Override
    public void createUser(UserRegistrationDTO userRegistrationDTO)
            throws PasswordMismatchException, UsernameAlreadyExist, EmailAlreadyExist {
        userValidationService.validateUserRegistration(userRegistrationDTO);
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
    @Override
    public void updateUser(UserUpdateDTO userUpdateDTO, String authToken) throws UserNotFoundException {
        long userId = jwtService.extractUserId(authToken);
        User user = userValidationService.findUserById(userId);
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
    @Override
    public void followUser(long followedUserId, String authToken) throws BadRequestException {
        long followerId = jwtService.extractUserId(authToken);
        if (followerId == followedUserId) {
            throw new BadRequestException(USER_CAN_T_FOLLOW_ITSELF);
        }
        User followed = userValidationService.findUserById(followedUserId);
        User follower = userValidationService.findUserById(followerId);
        followed.getFollowers().add(follower);
        userRepository.save(followed);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, BannedUserException {
        User user = userValidationService.getUsernameForLoginOrThrowException(username);
        userValidationService.throwExceptionIfUserIsBanned(user.getId());
        return user;
    }
    @Override
    public List<UserWithUsernameAndIdDTO> getFollowers(String authToken) throws UserNotFoundException {
        long userId = jwtService.extractUserId(authToken);
        return userValidationService.findUserById(userId)
                .getFollowers()
                .stream()
                .map(user -> new UserWithUsernameAndIdDTO(user.getUsername(), user.getId()))
                .toList();
    }
    @Override
    public List<UserWithUsernameAndIdDTO> getFollowing(String authToken) throws UserNotFoundException {
        long userId = jwtService.extractUserId(authToken);
        return userValidationService.findUserById(userId)
                .getFollowing()
                .stream()
                .map(user -> new UserWithUsernameAndIdDTO(user.getUsername(), user.getId()))
                .toList();
    }
    @Override
    public void setPrivateUser(String authToken) throws UserNotFoundException {
        long userId = jwtService.extractUserId(authToken);
        User user = userValidationService.findUserById(userId);
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
}
