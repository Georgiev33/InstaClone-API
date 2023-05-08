package com.example.demo.service;

import com.example.demo.model.dto.user.UserRegistrationDTO;
import com.example.demo.service.contracts.MailService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.internal.bytebuddy.utility.RandomString;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import static com.example.demo.util.constants.Constants.*;

@Service
@RequiredArgsConstructor

public class MailServiceImpl implements MailService {
    private final SimpleMailMessage simpleMailMessage;
    private final MailSender mailSender;
    @Override
    public String sendVerificationEmail(UserRegistrationDTO userRegistrationDTO) {
        String verificationCode = RandomString.make(INT_64);
        simpleMailMessage.setTo(userRegistrationDTO.email());
        simpleMailMessage.setText(APPLICATION_URL_USER + "auth/" + verificationCode);
        simpleMailMessage.setSubject(DO_NOT_REPLY);
        new Thread(() -> mailSender.send(simpleMailMessage)).start();
        return verificationCode;
    }
}
