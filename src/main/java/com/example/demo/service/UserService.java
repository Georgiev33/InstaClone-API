package com.example.demo.service;

import com.example.demo.model.dto.UserLoginDTO;
import com.example.demo.model.dto.UserRegistrationDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private MailSender mailSender;
    @Autowired
    private SimpleMailMessage simpleMailMessage;

    public Long login(UserLoginDTO userLoginDTO) {
        User user = findUserByUsername(userLoginDTO.getUsername())
                .orElseThrow(() -> new BadRequestException("Bad credentials"));
        if (!encoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            throw new BadRequestException("Bad credentials");
        }
        if(!user.isVerified()){
            throw new BadRequestException("Your account isn`t verified");
        }
        return user.getId();
    }

    public void createUser(UserRegistrationDTO userRegistrationDTO) {
        if (!userRegistrationDTO.getPassword().equals(userRegistrationDTO.getConfirmPassword())) {
            throw new BadRequestException("Passwords must match");
        }
        if (isUserAvailable(userRegistrationDTO.getUsername())) {
            throw new BadRequestException("Username already exist");
        }
        if (isEmailAvailable(userRegistrationDTO.getEmail())) {
                throw new BadRequestException("Email already exist");
        }

        User user = modelMapper.map(userRegistrationDTO, User.class);
        user.setPassword(encoder.encode(userRegistrationDTO.getPassword()));
        String verificationCode = RandomString.make(64);
        user.setVerificationCode(verificationCode);
        simpleMailMessage.setTo(userRegistrationDTO.getEmail());
        simpleMailMessage.setText("http://localhost:8080/user/" + verificationCode);
        simpleMailMessage.setSubject("do.not.reply");
        mailSender.send(simpleMailMessage);
        userRepository.save(user);
    }

    public ResponseEntity<String> verifyUser(String verificationCode){
        User user = userRepository.findUserByVerificationCodeAndVerificationCodeIsFalse(verificationCode)
                .orElseThrow(() ->new BadRequestException("Invalid verification code"));
        user.setVerified(true);
        userRepository.save(user);
        return ResponseEntity.ok("Registration successfully verified");
    }

    public boolean isEmailAvailable(String email) {
        Optional<User> u = userRepository.findUserByEmail(email);
        return u.isPresent();
    }

    public boolean isUserAvailable(String username) {
        Optional<User> u = userRepository.findUserByUsername(username);
        return u.isPresent();
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }
}
