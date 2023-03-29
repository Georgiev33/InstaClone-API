package com.example.demo.service;

import com.example.demo.model.dto.UserLoginDTO;
import com.example.demo.model.dto.UserRegistrationDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    private final ModelMapper modelMapper = new ModelMapper();
    @Mock
    private BCryptPasswordEncoder encoder;
    @Mock
    private MailSender mailSender;

    private final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    private UserService underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userRepository,modelMapper,encoder,mailSender,simpleMailMessage);
    }

    @Test
    void mismatchedRepeatedPasswordShouldThrowBadRequest(){
        //given
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setPassword("password");
        registrationDTO.setConfirmPassword("passwor");
        //when

        //then
        assertThatThrownBy(() -> underTest.createUser(registrationDTO)).hasMessage("Passwords must match");

    }
    @Test
    void matchingRepeatedPasswordShouldntThrowBadRequest(){
        //given
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setPassword("password");
        registrationDTO.setConfirmPassword("password");

        //when

        //then
        assertThat(catchThrowableOfType(() -> underTest.createUser(registrationDTO), BadRequestException.class)).isNull();

    }
    @Test
    void existingUsernameShouldThrowBadRequest(){
        //given
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setPassword("password");
        registrationDTO.setConfirmPassword("password");
        registrationDTO.setUsername("venko");
        // when
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(new User()));
        // then
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> underTest.createUser(registrationDTO))
                .withMessage("Username already exists");
    }
    @Test
    void nonexistentUsernameShouldntThrowBadRequest(){
        //given
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setPassword("password");
        registrationDTO.setConfirmPassword("password");
        registrationDTO.setUsername("venko");
        // when
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.empty());
        //then

        assertThat(catchThrowableOfType(() -> underTest.createUser(registrationDTO), BadRequestException.class)).isNull();
    }

    @Test
    void existingEmailShouldThrowBadRequest(){
        //given
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setPassword("password");
        registrationDTO.setConfirmPassword("password");
        registrationDTO.setEmail("testmail@gmail.com");
        registrationDTO.setUsername("venko");
        // when
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(new User()));

        //then
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> underTest.createUser(registrationDTO))
                .withMessage("Email already exists");
    }
    @Test
    void nonexistentEmailShouldntThrowBadRequest(){
        //given
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setPassword("password");
        registrationDTO.setConfirmPassword("password");
        registrationDTO.setEmail("testmail@gmail.com");
        registrationDTO.setUsername("venko");
        // when
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.empty());

        //then
        assertThat(catchThrowableOfType(() -> underTest.createUser(registrationDTO), BadRequestException.class)).isNull();
    }

    @Test
    void sendVerificationEmailMethodShouldSendEmail(){
        //given
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setPassword("password");
        registrationDTO.setConfirmPassword("password");
        registrationDTO.setEmail("testemail@gmail.com");
        registrationDTO.setUsername("venko");

        //
        underTest.createUser(registrationDTO);

        //then
        assertThat(Objects.requireNonNull(simpleMailMessage.getTo())[0]).isEqualTo(registrationDTO.getEmail());
        assertThat(simpleMailMessage.getText()).startsWith("http://localhost:8080/user/");
        assertThat(simpleMailMessage.getSubject()).isEqualTo("do.not.reply");
        verify(mailSender).send(simpleMailMessage);
    }

    @Test
    void nonExistentLoginUsernameShouldThrowBadRequest(){
        //given
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUsername("venko");
        userLoginDTO.setPassword("password");
        //when
            when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.empty());
        //then
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> underTest.login(userLoginDTO))
                .withMessage("Bad credentials");
    }

    @Test
    void existingLoginUsernameShouldntThrowBadRequest(){
        //given
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUsername("venko");
        userLoginDTO.setPassword("password");
        User user = new User();
        user.setPassword(userLoginDTO.getPassword());
        user.setVerified(true);
        //when
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
        when(encoder.matches(anyString(), anyString())).thenReturn(true);

        //then
        assertThat(catchThrowableOfType(() -> underTest.login(userLoginDTO), BadRequestException.class)).isNull();
    }
    @Test
    void wrongLoginPasswordShouldThrowBadRequest(){
        //given
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUsername("venko");
        userLoginDTO.setPassword("password");
        //when
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(new User()));
        when(encoder.matches(any(), any())).thenReturn(false);
        //then
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> underTest.login(userLoginDTO))
                .withMessage("Bad credentials");
    }
    @Test
    void correctLoginPasswordShouldntThrowBadRequest(){
        //given
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUsername("venko");
        userLoginDTO.setPassword("password");
        User user = new User();
        user.setVerified(true);
        //when
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
        when(encoder.matches(any(), any())).thenReturn(true);
        //then
        assertThat(catchThrowableOfType(() -> underTest.login(userLoginDTO), BadRequestException.class)).isNull();
    }


    @Test
    void unverifiedUserShouldThrowBadRequest(){
        //given
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUsername("venko");
        userLoginDTO.setPassword("password");
        User user = new User();
        user.setVerified(false);
        //when
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
        when(encoder.matches(any(),any())).thenReturn(true);
        //then
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> underTest.login(userLoginDTO))
                .withMessage("Your account isn`t verified");
    }

    @Test
    void verifiedUserShouldntThrowBadRequest(){
        //given
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUsername("venko");
        userLoginDTO.setPassword("password");
        User user = new User();
        user.setVerified(true);
        //when
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
        when(encoder.matches(any(),any())).thenReturn(true);
        //then
        assertThat(catchThrowableOfType(() -> underTest.login(userLoginDTO), BadRequestException.class)).isNull();
    }
}