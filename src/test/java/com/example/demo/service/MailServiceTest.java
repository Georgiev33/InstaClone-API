package com.example.demo.service;

import com.example.demo.model.dto.User.UserRegistrationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.Objects;

import static com.example.demo.util.Constants.APPLICATION_URL_USER;
import static com.example.demo.util.Constants.DO_NOT_REPLY;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

    @Mock
    private MailSender mailSender;

    private final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

    private MailService underTest;
    @BeforeEach
    void setUp() {
        underTest = new MailService(simpleMailMessage, mailSender);
    }

    @Test
    void sendVerificationEmailMethodShouldSendEmail(){
        //given
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO("TEST_USER", "TEST_MAIL",
                "PASSWORD","PASSWORD");

        //
        underTest.sendVerificationEmail(registrationDTO);

        //then
        assertThat(Objects.requireNonNull(simpleMailMessage.getTo())[0]).isEqualTo(registrationDTO.getEmail());
        assertThat(simpleMailMessage.getText()).startsWith(APPLICATION_URL_USER);
        assertThat(simpleMailMessage.getSubject()).isEqualTo(DO_NOT_REPLY);
        verify(mailSender).send(simpleMailMessage);
    }
}
