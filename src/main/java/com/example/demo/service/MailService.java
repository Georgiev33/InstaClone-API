package com.example.demo.service;

import com.example.demo.model.dto.UserRegistrationDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.internal.bytebuddy.utility.RandomString;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import static com.example.demo.util.Constants.*;
@Service
@RequiredArgsConstructor

public class MailService {
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
}