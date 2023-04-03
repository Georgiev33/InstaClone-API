package com.example.demo.service;

import com.example.demo.model.dto.UserLoginDTO;
import com.example.demo.model.dto.UserRegistrationDTO;
import com.example.demo.model.dto.UserWithUsernameAndIdDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.UserServiceHelper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.example.demo.util.Constants.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder encoder;
    private final UserServiceHelper userServiceHelper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public String login(UserLoginDTO userLoginDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginDTO.getUsername(),
                        userLoginDTO.getPassword()
                )
        );
        User user = userServiceHelper.findUserByUsername(userLoginDTO.getUsername());
        return jwtService.generateToken(Map.of("USER_ID", user.getId()), user);
    }

    public void createUser(UserRegistrationDTO userRegistrationDTO) {
        userServiceHelper.validateUser(userRegistrationDTO);
        User user = modelMapper.map(userRegistrationDTO, User.class);
        user.setPassword(encoder.encode(userRegistrationDTO.getPassword()));
        user.setVerificationCode(userServiceHelper.sendVerificationEmail(userRegistrationDTO));
//        user.setRole(Role.USER);
        userRepository.save(user);
    }

    public void followUser(long followedUserId, String authToken) {
        long followerId = jwtService.extractUserId(authToken);
        if (followerId == followedUserId) {
            throw new BadRequestException(USER_CAN_T_FOLLOW_ITSELF);
        }
        User followed = userServiceHelper.findUserById(followedUserId);
        User follower = userServiceHelper.findUserById(followerId);
        followed.getFollowers().add(follower);
        userRepository.save(followed);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userServiceHelper.findUserByUsername(username);
    }

    public List<UserWithUsernameAndIdDTO> getFollowers(String authToken) {
        long userId = jwtService.extractUserId(authToken);
        return userServiceHelper
                .findUserById(userId)
                .getFollowers()
                .stream()
                .map(user -> new UserWithUsernameAndIdDTO(user.getUsername(), user.getId()))
                .toList();
    }

    public List<UserWithUsernameAndIdDTO> getFollowing(String authToken) {
        long userId = jwtService.extractUserId(authToken);
        return userServiceHelper
                .findUserById(userId)
                .getFollowing()
                .stream()
                .map(user -> new UserWithUsernameAndIdDTO(user.getUsername(), user.getId()))
                .toList();
    }
}
