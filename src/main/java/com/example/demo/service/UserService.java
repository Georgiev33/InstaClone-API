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

import static com.example.demo.util.Constants.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder encoder;
    private final MailSender mailSender;
    private final SimpleMailMessage simpleMailMessage;

    public UserService(@Autowired UserRepository userRepository, @Autowired ModelMapper modelMapper,
                       @Autowired BCryptPasswordEncoder encoder, @Autowired MailSender mailSender,
                       @Autowired SimpleMailMessage simpleMailMessage) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.encoder = encoder;
        this.mailSender = mailSender;
        this.simpleMailMessage = simpleMailMessage;
    }


    public Long login(UserLoginDTO userLoginDTO) {
        User user = findUserByUsername(userLoginDTO.getUsername())
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
        validateUser(userRegistrationDTO);
        User user = modelMapper.map(userRegistrationDTO, User.class);
        user.setPassword(encoder.encode(userRegistrationDTO.getPassword()));
        user.setVerificationCode(sendVerificationEmail(userRegistrationDTO));
        userRepository.save(user);
    }

    public ResponseEntity<String> verifyUser(String verificationCode) {
        User user = userRepository.findUserByVerificationCodeAndVerificationCodeIsFalse(verificationCode)
                .orElseThrow(() -> new BadRequestException(INVALID_VERIFICATION_CODE));
        user.setVerified(true);
        userRepository.save(user);
        return ResponseEntity.ok(REGISTRATION_SUCCESSFULLY_VERIFIED);
    }

    private boolean doesEmailExist(String email) {
        Optional<User> u = userRepository.findUserByEmail(email);
        return u.isPresent();
    }

    private boolean doesUsernameExist(String username) {
        Optional<User> u = userRepository.findUserByUsername(username);
        return u.isPresent();
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
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

    private String sendVerificationEmail(UserRegistrationDTO userRegistrationDTO) {
        String verificationCode = RandomString.make(INT_64);
        simpleMailMessage.setTo(userRegistrationDTO.getEmail());
        simpleMailMessage.setText(APPLICATION_URL + verificationCode);
        simpleMailMessage.setSubject(DO_NOT_REPLY);
        new Thread(() -> mailSender.send(simpleMailMessage)).start();
        return verificationCode;
    }
}
