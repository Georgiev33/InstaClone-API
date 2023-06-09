package com.example.demo.service;

import com.example.demo.model.dto.BanHistoryDTO;
import com.example.demo.model.dto.banUser.BanUserDTO;
import com.example.demo.model.dto.banUser.UnbanUserDTO;
import com.example.demo.model.entity.ban.BanHistory;
import com.example.demo.model.entity.ban.BannedUsers;
import com.example.demo.model.exception.BannedUserException;
import com.example.demo.model.exception.UserAlreadyBannedException;
import com.example.demo.model.exception.UserNotBannedException;
import com.example.demo.model.exception.UserNotFoundException;
import com.example.demo.repository.ban.BannedHistory;
import com.example.demo.repository.ban.BannedUsersRepository;
import com.example.demo.service.contracts.BanUserService;
import com.example.demo.service.contracts.JwtService;
import com.example.demo.service.contracts.UserValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.demo.util.constants.Constants.HOUR_IN_MILLISECONDS;

@Service
@RequiredArgsConstructor
public class BanUserServiceImpl implements BanUserService {
    private final UserValidationService userValidationService;
    private final JwtService jwtService;
    private final BannedUsersRepository bannedUsersRepository;
    private final BannedHistory bannedHistory;

    @Override
    @Transactional
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

        bannedHistory.save(BanHistory.builder()
                .bannedUserId(banUserDTO.userIdToBan())
                .adminId(adminId)
                .banStartDate(LocalDateTime.now())
                .banEndDate(LocalDateTime.now().plusHours(banUserDTO.hoursToBan()))
                .reason(banUserDTO.reason())
                .isBanned(true)
                .build());

    }

    @Override
    @Transactional
    public void unbanUser(UnbanUserDTO unbanUserDTO, String authToken) throws UserNotFoundException, BannedUserException {
        userValidationService.throwExceptionIfUserNotFound(unbanUserDTO.userIdToUnban());
        final long adminId = jwtService.extractUserId(authToken);
        bannedUsersRepository.delete(findBannedUser(unbanUserDTO.userIdToUnban()));
        bannedHistory.save(BanHistory.builder()
                .bannedUserId(unbanUserDTO.userIdToUnban())
                .adminId(adminId)
                .banEndDate(LocalDateTime.now())
                .reason(unbanUserDTO.reason())
                .isBanned(false)
                .build());
    }

    @Override
    public Page<BanHistoryDTO> banHistory(long bannedId, int page, int size) throws UserNotFoundException {
        if (bannedId == -1) {
            return new PageImpl<>(bannedHistory.findAll(PageRequest.of(page, size))
                    .stream()
                    .map(banHistory -> new BanHistoryDTO(banHistory.getBannedUserId(),
                            banHistory.getAdminId(),
                            banHistory.getReason(),
                            banHistory.getBanStartDate(),
                            banHistory.getBanEndDate(),
                            banHistory.isBanned())).toList());
        }
        userValidationService.throwExceptionIfUserNotFound(bannedId);
        return new PageImpl<>(bannedHistory.findAllByBannedUserId(bannedId, PageRequest.of(page, size))
                .stream()
                .map(banHistory -> new BanHistoryDTO(banHistory.getBannedUserId(),
                        banHistory.getAdminId(),
                        banHistory.getReason(),
                        banHistory.getBanStartDate(),
                        banHistory.getBanEndDate(),
                        banHistory.isBanned())).toList());
    }

    @Scheduled(fixedRate = HOUR_IN_MILLISECONDS)
    @Transactional()
    public void unbanUsersWhichBansAreExpired(){
        List<BannedUsers> bannedUsers = bannedUsersRepository.findAllByBanEndDateBefore(LocalDateTime.now());
        bannedUsersRepository.deleteAll(bannedUsers);
        bannedHistory.saveAll(bannedUsers.stream().map(bu ->BanHistory.builder()
                .bannedUserId(bu.getBannedId())
                .adminId(-1)
                .banEndDate(LocalDateTime.now())
                .reason("Cron job auto unban")
                .isBanned(false)
                .build()).toList());
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
