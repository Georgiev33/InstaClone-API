package com.example.demo.service.contracts;

import com.example.demo.model.dto.ReportedUsers.ReportUserDTO;
import com.example.demo.model.dto.user.*;
import com.example.demo.model.exception.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;


public interface UserService extends UserDetailsService {
    String login(UserLoginDTO userLoginDTO) throws BannedUserException;

    void createUser(UserRegistrationDTO userRegistrationDTO)
            throws PasswordMismatchException, UsernameAlreadyExist, EmailAlreadyExist;

    void updateUser(UserUpdateDTO userUpdateDTO, String authToken);

    void followUser(long followedUserId, String authToken) throws UserNotFoundException;

    List<UserWithUsernameAndIdDTO> getFollowers(String authToken);

    List<UserWithUsernameAndIdDTO> getFollowing(String authToken);

    void setPrivateUser(String authToken);

    void reportUser(ReportUserDTO reportUserDTO, String authToken)
            throws ReportedUserAlreadyExist, UserNotFoundException;

    UserWithoutPasswordDTO getUserById(long userId) throws UserNotFoundException;

    String deleteAccount(String authToken);
}
