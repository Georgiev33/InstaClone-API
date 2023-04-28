package com.example.demo.service.contracts;

import com.example.demo.model.dto.User.UserLoginDTO;
import com.example.demo.model.dto.User.UserRegistrationDTO;
import com.example.demo.model.dto.User.UserUpdateDTO;
import com.example.demo.model.dto.User.UserWithUsernameAndIdDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;


public interface UserService extends UserDetailsService {
    String login(UserLoginDTO userLoginDTO)throws UserNotFoundException, AuthenticationException;

    void createUser(UserRegistrationDTO userRegistrationDTO)
            throws PasswordMismatchException, UsernameAlreadyExist, EmailAlreadyExist;

    void updateUser(UserUpdateDTO userUpdateDTO, String authToken);

    void followUser(long followedUserId, String authToken)throws UserNotFoundException;


    List<UserWithUsernameAndIdDTO> getFollowers(String authToken);

    List<UserWithUsernameAndIdDTO> getFollowing(String authToken);


    ResponseEntity<String> verifyUser(String verificationCode)throws InvalidValidationCode;

    void setPrivateUser(String authToken);

    User findUserByUsername(String username) throws UserNotFoundException;

    User findUserById(long userId) throws UserNotFoundException;

    void validateUserById(long userId) throws UserNotFoundException;

}
