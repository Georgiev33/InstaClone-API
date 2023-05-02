package com.example.demo.service;

import com.example.demo.model.dto.banUser.BanUserDTO;
import com.example.demo.model.dto.banUser.UnbanUserDTO;
import com.example.demo.model.entity.BannedUsers;
import com.example.demo.model.exception.BannedUserException;
import com.example.demo.model.exception.UserAlreadyBannedException;
import com.example.demo.model.exception.UserNotBannedException;
import com.example.demo.model.exception.UserNotFoundException;
import com.example.demo.repository.BannedUsersRepository;
import com.example.demo.service.contracts.BanUserService;
import com.example.demo.service.contracts.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BanUserServiceImpl implements BanUserService {
    private final UserValidationService userValidationService;
    private final JwtService jwtService;
    private final BannedUsersRepository bannedUsersRepository;

    @Override
    public void banUser(BanUserDTO banUserDTO, String authToken) throws UserNotFoundException, UserAlreadyBannedException {
        userValidationService.throwExceptionIfUserNotFound(banUserDTO.userIdToBan());
        final long adminId = jwtService.extractUserId(authToken);
        isBannedUserIsAlreadyBanned(banUserDTO.userIdToBan());
        bannedUsersRepository.save(
                new BannedUsers(
                        banUserDTO.userIdToBan(),
                        adminId,
                        banUserDTO.reason(),
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(banUserDTO.hoursToBan())));
    }

    @Override
    public void unbanUser(UnbanUserDTO unbanUserDTO, String authToken) throws UserNotFoundException, BannedUserException {
        userValidationService.throwExceptionIfUserNotFound(unbanUserDTO.userIdToUnban());
        bannedUsersRepository.delete(findBannedUser(unbanUserDTO.userIdToUnban()));
    }

    private BannedUsers findBannedUser(Long userId) throws BannedUserException {
        return bannedUsersRepository.findByBannedId(userId)
                .orElseThrow(() -> new UserNotBannedException("User is not banned"));
    }

    private void isBannedUserIsAlreadyBanned(Long userId) throws UserAlreadyBannedException {
        if (bannedUsersRepository.findByBannedId(userId).isPresent()) {
            throw new UserAlreadyBannedException("User is already banned");
        }
    }
}
