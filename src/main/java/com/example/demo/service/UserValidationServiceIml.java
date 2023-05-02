package com.example.demo.service;

import com.example.demo.model.dto.User.UserRegistrationDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.*;
import com.example.demo.repository.BannedUsersRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.contracts.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.demo.util.Constants.*;
import static com.example.demo.util.Constants.EMAIL_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class UserValidationServiceIml implements UserValidationService {
    private final UserRepository userRepository;
    private final BannedUsersRepository bannedUsersRepository;

    @Override
    public User getUsernameForLoginOrThrowException(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
    }

    @Override
    public User getUserOrThrowException(String username) throws UserNotFoundException {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
    }

    @Override
    public User findUserById(long userId) throws UserNotFoundException {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
    }

    @Override
    public void throwExceptionIfUserNotFound(long userId) throws UserNotFoundException {
        findUserById(userId);
    }

    @Override
    public ResponseEntity<String> verifyUser(String verificationCode) throws InvalidValidationCode {
        User user = userRepository.findUserByVerificationCodeAndVerificationCodeIsFalse(verificationCode)
                .orElseThrow(() -> new InvalidValidationCode(INVALID_VERIFICATION_CODE));
        User verifiedUser = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .verificationCode(user.getVerificationCode())
                .isVerified(true)
                .isPrivate(user.isPrivate())
                .bio(user.getBio())
                .build();
        userRepository.save(verifiedUser);
        return ResponseEntity.ok(REGISTRATION_SUCCESSFULLY_VERIFIED);
    }

    @Override
    public void validateUserRegistration(UserRegistrationDTO userRegistrationDTO)
            throws PasswordMismatchException, UsernameAlreadyExist, EmailAlreadyExist {
        if (!userRegistrationDTO.password().equals(userRegistrationDTO.confirmPassword())) {
            throw new PasswordMismatchException(PASSWORDS_MUST_MATCH);
        }
        if (doesUsernameExist(userRegistrationDTO.username())) {
            throw new UsernameAlreadyExist(USERNAME_ALREADY_EXISTS);
        }

        if (doesEmailExist(userRegistrationDTO.email())) {
            throw new EmailAlreadyExist(EMAIL_ALREADY_EXISTS);
        }
    }

    @Override
    public void throwExceptionIfUserNotVerified(boolean verified) throws UserNotVerifiedException {
        if (!verified) {
            throw new UserNotVerifiedException("Check email for verification code");
        }
    }

    @Override
    public void throwExceptionIfUserIsBanned(Long userId) throws BannedUserException {
        if (bannedUsersRepository.findByBannedId(userId).isPresent()) {
            throw new BannedUserException("User is banned");
        }
    }

    public boolean isUserBanned(Long userId) throws BannedUserException {
        return bannedUsersRepository.findByBannedId(userId).isPresent();
    }

    private boolean doesEmailExist(String email) {
        Optional<User> u = userRepository.findUserByEmail(email);
        return u.isPresent();
    }

    private boolean doesUsernameExist(String username) {
        return userRepository.findUserByUsername(username).isPresent();
    }
}
