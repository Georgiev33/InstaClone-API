package com.example.demo.service;

import com.example.demo.model.dto.UserLoginDTO;
import com.example.demo.model.dto.UserRegistrationDTO;
import com.example.demo.model.dto.UserWithUsernameAndIdDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.example.demo.util.Constants.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
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
        User user = findUserByUsername(userLoginDTO.getUsername());
        return jwtService.generateToken(Map.of("USER_ID", user.getId()), user);
    }

    public void createUser(UserRegistrationDTO userRegistrationDTO) {
        validateUser(userRegistrationDTO);
        User user = modelMapper.map(userRegistrationDTO, User.class);
        user.setPassword(encoder.encode(userRegistrationDTO.getPassword()));
        user.setRoles(Set.of(roleService.findRole("user")));
        user.setVerificationCode(mailService.sendVerificationEmail(userRegistrationDTO));
        userRepository.save(user);
    }

    public void followUser(long followedUserId, String authToken) {
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
    public UserDetails loadUserByUsername(String username) {
        return findUserByUsername(username);
    }

    public List<UserWithUsernameAndIdDTO> getFollowers(String authToken) {
        long userId = jwtService.extractUserId(authToken);
        return findUserById(userId)
                .getFollowers()
                .stream()
                .map(user -> new UserWithUsernameAndIdDTO(user.getUsername(), user.getId()))
                .toList();
    }

    public List<UserWithUsernameAndIdDTO> getFollowing(String authToken) {
        long userId = jwtService.extractUserId(authToken);
        return findUserById(userId)
                .getFollowing()
                .stream()
                .map(user -> new UserWithUsernameAndIdDTO(user.getUsername(), user.getId()))
                .toList();
    }


    public ResponseEntity<String> verifyUser(String verificationCode) {
        User user = userRepository.findUserByVerificationCodeAndVerificationCodeIsFalse(verificationCode)
                .orElseThrow(() -> new BadRequestException(INVALID_VERIFICATION_CODE));
        user.setVerified(true);
        userRepository.save(user);
        return ResponseEntity.ok(REGISTRATION_SUCCESSFULLY_VERIFIED);
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));
    }

    public User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));
    }

    public void validateUserById(long userId) {
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

    private void validateUser(UserRegistrationDTO userRegistrationDTO) {
        if (!userRegistrationDTO.getPassword().equals(userRegistrationDTO.getConfirmPassword())) {
            throw new BadRequestException(PASSWORDS_MUST_MATCH);
        }
        if (doesUsernameExist(userRegistrationDTO.getUsername())) {
            throw new BadRequestException(USERNAME_ALREADY_EXISTS);
        }

        if (doesEmailExist(userRegistrationDTO.getEmail())) {
            throw new BadRequestException(EMAIL_ALREADY_EXISTS);
        }
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        jwtService.invalidateToken(request.getHeader("Authorization"));
    }
}
