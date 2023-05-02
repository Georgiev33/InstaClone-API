package com.example.demo.service.contracts;

import com.example.demo.model.dto.banUser.BanUserDTO;
import com.example.demo.model.dto.banUser.UnbanUserDTO;
import com.example.demo.model.exception.BannedUserException;
import com.example.demo.model.exception.UserAlreadyBannedException;
import com.example.demo.model.exception.UserNotFoundException;

public interface BanUserService {
    void banUser(BanUserDTO banUserDTO, String authToken) throws UserNotFoundException, UserAlreadyBannedException;
    void unbanUser(UnbanUserDTO unbanUserDTO, String authToken) throws UserNotFoundException, BannedUserException;
}
