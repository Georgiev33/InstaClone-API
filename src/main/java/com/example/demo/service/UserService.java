package com.example.demo.service;

import com.example.demo.model.dto.UserLoginDTO;
import com.example.demo.model.dto.UserRegistrationDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.UserServiceHelper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.demo.util.Constants.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder encoder;
    private final UserServiceHelper userServiceHelper;


    public UserService(@Autowired UserRepository userRepository, @Autowired ModelMapper modelMapper,
                       @Autowired BCryptPasswordEncoder encoder, @Autowired UserServiceHelper userServiceHelper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.encoder = encoder;
        this.userServiceHelper = userServiceHelper;
    }

    public Long login(UserLoginDTO userLoginDTO) {
        User user = userServiceHelper.findUserByUsername(userLoginDTO.getUsername())
                .orElseThrow(() -> new BadRequestException(BAD_CREDENTIALS));
        if (!encoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            throw new BadRequestException(BAD_CREDENTIALS);
        }
        if (!user.isVerified()) {
            throw new BadRequestException(YOUR_ACCOUNT_ISN_T_VERIFIED);
        }
        return user.getId();
    }

    public void createUser(UserRegistrationDTO userRegistrationDTO) {
        userServiceHelper.validateUser(userRegistrationDTO);
        User user = modelMapper.map(userRegistrationDTO, User.class);
        user.setPassword(encoder.encode(userRegistrationDTO.getPassword()));
        user.setVerificationCode(userServiceHelper.sendVerificationEmail(userRegistrationDTO));
        userRepository.save(user);
    }

    public void followUser(long followedUserId, long followerId) {
        User followed = userServiceHelper.findUserById(followedUserId);
        User follower = userServiceHelper.findUserById(followerId);
        follower.getFollowing().add(followed);
        userRepository.save(follower);
    }
}
