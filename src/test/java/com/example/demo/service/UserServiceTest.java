//package com.example.demo.service;
//
//import com.example.demo.model.dto.User.UserLoginDTO;
//import com.example.demo.model.dto.User.UserRegistrationDTO;
//import com.example.demo.model.entity.User;
//import com.example.demo.model.exception.BadRequestException;
//import com.example.demo.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.modelmapper.ModelMapper;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import java.util.Optional;
//
//import static com.example.demo.util.Constants.*;
//import static org.assertj.core.api.AssertionsForClassTypes.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class UserServiceTest {
//    private static final String TEST_USER = "venko";
//    private static final String PASSWORD = "password";
//    private static final String WRONG_PASSWORD = "passwor";
//    private static final String TEST_MAIL = "testmail@gmail.com";
//    @Mock
//    private UserRepository userRepository;
//    private final ModelMapper modelMapper = new ModelMapper();
//    @Mock
//    private BCryptPasswordEncoder encoder;
//    private  UserService underTest;
//    @Mock
//    private JwtService jwtService;
//    @Mock
//    private  AuthenticationManager authenticationManager;
//    @Mock
//    private MailService mailService;
//
//    @BeforeEach
//    void setUp() {
//        underTest = new UserService(userRepository,modelMapper,encoder,authenticationManager,mailService,jwtService);
//    }
//
//    @Test
//    void mismatchedRepeatedPasswordShouldThrowBadRequest(){
//        //given
//        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(null,null,
//                PASSWORD, WRONG_PASSWORD);
//        //when
//
//        //then
//        assertThatThrownBy(() -> underTest.createUser(registrationDTO)).hasMessage(PASSWORDS_MUST_MATCH);
//
//    }
//    @Test
//    void matchingRepeatedPasswordShouldntThrowBadRequest(){
//        //given
//        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(null,null,
//                PASSWORD,PASSWORD);
//
//        //when
//
//        //then
//        assertThat(catchThrowableOfType(() -> underTest.createUser(registrationDTO), BadRequestException.class)).isNull();
//
//    }
//    @Test
//    void existingUsernameShouldThrowBadRequest(){
//        //given
//        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(TEST_USER,null,
//                PASSWORD,PASSWORD);
//        // when
//        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(new User()));
//        // then
//        assertThatExceptionOfType(BadRequestException.class)
//                .isThrownBy(() -> underTest.createUser(registrationDTO))
//                .withMessage(USERNAME_ALREADY_EXISTS);
//    }
//    @Test
//    void nonexistentUsernameShouldntThrowBadRequest(){
//        //given
//        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(TEST_USER,null,
//                PASSWORD,PASSWORD);
//        // when
//        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.empty());
//        //then
//
//        assertThat(catchThrowableOfType(() -> underTest.createUser(registrationDTO), BadRequestException.class)).isNull();
//    }
//
//    @Test
//    void existingEmailShouldThrowBadRequest(){
//        //given
//        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(TEST_USER, TEST_MAIL,
//                PASSWORD,PASSWORD);
//        // when
//        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(new User()));
//
//        //then
//        assertThatExceptionOfType(BadRequestException.class)
//                .isThrownBy(() -> underTest.createUser(registrationDTO))
//                .withMessage(EMAIL_ALREADY_EXISTS);
//    }
//    @Test
//    void nonexistentEmailShouldntThrowBadRequest(){
//        //given
//        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(TEST_USER, TEST_MAIL,
//                PASSWORD,PASSWORD);
//        // when
//        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.empty());
//
//        //then
//        assertThat(catchThrowableOfType(() -> underTest.createUser(registrationDTO), BadRequestException.class)).isNull();
//    }
//
//    @Test
//    void nonExistentLoginUsernameShouldThrowBadRequest(){
//        //given
//        UserLoginDTO userLoginDTO = new UserLoginDTO(TEST_USER, PASSWORD);
//        //when
//            when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.empty());
//        //then
//        assertThatExceptionOfType(BadRequestException.class)
//                .isThrownBy(() -> underTest.login(userLoginDTO))
//                .withMessage(BAD_CREDENTIALS);
//    }
//
//    @Test
//    void existingLoginUsernameShouldntThrowBadRequest(){
//        //given
//        UserLoginDTO userLoginDTO = new UserLoginDTO(TEST_USER, PASSWORD);
//        User user = new User();
//        user.setPassword(userLoginDTO.getPassword());
//        user.setVerified(true);
//        //when
//        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
//        when(encoder.matches(anyString(), anyString())).thenReturn(true);
//
//        //then
//        assertThat(catchThrowableOfType(() -> underTest.login(userLoginDTO), BadRequestException.class)).isNull();
//    }
//    @Test
//    void wrongLoginPasswordShouldThrowBadRequest(){
//        //given
//        UserLoginDTO userLoginDTO = new UserLoginDTO(TEST_USER, PASSWORD);
//        //when
//        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(new User()));
//        when(encoder.matches(any(), any())).thenReturn(false);
//        //then
//        assertThatExceptionOfType(BadRequestException.class)
//                .isThrownBy(() -> underTest.login(userLoginDTO))
//                .withMessage(BAD_CREDENTIALS);
//    }
//    @Test
//    void correctLoginPasswordShouldntThrowBadRequest(){
//        //given
//        UserLoginDTO userLoginDTO = new UserLoginDTO(TEST_USER, PASSWORD);
//        User user = new User();
//        user.setVerified(true);
//        //when
//        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
//        when(encoder.matches(any(), any())).thenReturn(true);
//        //then
//        assertThat(catchThrowableOfType(() -> underTest.login(userLoginDTO), BadRequestException.class)).isNull();
//    }
//
//
//    @Test
//    void unverifiedUserShouldThrowBadRequest(){
//        //given
//        UserLoginDTO userLoginDTO = new UserLoginDTO(TEST_USER, PASSWORD);
//        User user = new User();
//        user.setVerified(false);
//        //when
//        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
//        when(encoder.matches(any(),any())).thenReturn(true);
//        //then
//        assertThatExceptionOfType(BadRequestException.class)
//                .isThrownBy(() -> underTest.login(userLoginDTO))
//                .withMessage(YOUR_ACCOUNT_ISN_T_VERIFIED);
//    }
//
//    @Test
//    void verifiedUserShouldntThrowBadRequest(){
//        //given
//        UserLoginDTO userLoginDTO = new UserLoginDTO(TEST_USER, PASSWORD);
//        User user = new User();
//        user.setVerified(true);
//        //when
//        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
//        when(encoder.matches(any(),any())).thenReturn(true);
//        //then
//        assertThat(catchThrowableOfType(() -> underTest.login(userLoginDTO), BadRequestException.class)).isNull();
//    }
//}