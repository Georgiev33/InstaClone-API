package com.example.demo.util;

import com.example.demo.model.dto.UserRegistrationDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.internal.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.example.demo.util.Constants.*;

@Component
@RequiredArgsConstructor
public class UserServiceHelper {

    private final UserRepository userRepository;

    private final SimpleMailMessage simpleMailMessage;

    private final MailSender mailSender;


    public String sendVerificationEmail(UserRegistrationDTO userRegistrationDTO) {
        String verificationCode = RandomString.make(INT_64);
        simpleMailMessage.setTo(userRegistrationDTO.getEmail());
        simpleMailMessage.setText(APPLICATION_URL_USER + verificationCode);
        simpleMailMessage.setSubject(DO_NOT_REPLY);
        new Thread(() -> mailSender.send(simpleMailMessage)).start();
        return verificationCode;
    }

    public boolean doesEmailExist(String email) {
        Optional<User> u = userRepository.findUserByEmail(email);
        return u.isPresent();
    }

    public boolean doesUsernameExist(String username) {
        Optional<User> u = userRepository.findUserByUsername(username);
        return u.isPresent();
    }

    public User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));
    }

    public void validateUser(UserRegistrationDTO userRegistrationDTO) {
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

    public ResponseEntity<String> verifyUser(String verificationCode) {
        User user = userRepository.findUserByVerificationCodeAndVerificationCodeIsFalse(verificationCode)
                .orElseThrow(() -> new BadRequestException(INVALID_VERIFICATION_CODE));
        user.setVerified(true);
        userRepository.save(user);
        return ResponseEntity.ok(REGISTRATION_SUCCESSFULLY_VERIFIED);
    }
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }
}
