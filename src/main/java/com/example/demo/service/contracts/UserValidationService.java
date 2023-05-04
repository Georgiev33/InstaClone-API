package com.example.demo.service.contracts;

import com.example.demo.model.dto.user.UserRegistrationDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserValidationService {
    User getUsernameForLoginOrThrowException(String username) throws UsernameNotFoundException;

    User getUserOrThrowException(String username)throws UserNotFoundException;

    User findUserById(long userId) throws UserNotFoundException;

    void throwExceptionIfUserNotFound(long userId) throws UserNotFoundException;
    ResponseEntity<String> verifyUser(String verificationCode)throws InvalidValidationCode;
    void validateUserRegistration(UserRegistrationDTO userRegistrationDTO)
            throws PasswordMismatchException, UsernameAlreadyExist, EmailAlreadyExist;
    void throwExceptionIfUserNotVerified(boolean verified) throws UserNotVerifiedException;
    void throwExceptionIfUserIsBanned(Long id) throws BannedUserException;
    boolean isUserBanned(Long id);
}

