package com.example.demo.service.contracts;

import com.example.demo.model.dto.User.UserRegistrationDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserValidationService {
    User validateUsernameForLogin(String username) throws UsernameNotFoundException;

    User findUserByUsername(String username)throws UserNotFoundException;

    User findUserById(long userId) throws UserNotFoundException;

    void validateUserById(long userId) throws UserNotFoundException;
    ResponseEntity<String> verifyUser(String verificationCode)throws InvalidValidationCode;
    void validateUserRegistration(UserRegistrationDTO userRegistrationDTO)
            throws PasswordMismatchException, UsernameAlreadyExist, EmailAlreadyExist;
    void isUserVerified(boolean verified) throws UserNotVerifiedException;
    void isUserBanned(Long id) throws BannedUserException;
}
