package com.example.demo.service;

import com.example.demo.model.dto.ReportedUsers.ReportUserDTO;
import com.example.demo.model.dto.ReportedUsers.ReportedUsersResponseDTO;
import com.example.demo.model.entity.ReportedUsers;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.AccessDeniedException;
import com.example.demo.model.exception.ReportedUserAlreadyExist;
import com.example.demo.model.exception.UserNotFoundException;
import com.example.demo.repository.BannedUsersRepository;
import com.example.demo.repository.ReportedUsersRepository;
import com.example.demo.service.contracts.AdminService;
import com.example.demo.service.contracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.demo.util.Constants.ACCESS_DENIED;
import static com.example.demo.util.Constants.ADMIN;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final BannedUsersRepository bannedUsersRepository;
    private final ReportedUsersRepository reportedUsersRepository;
    private final JwtService jwtService;
    private final UserService userService;


    public void reportUser(ReportUserDTO reportUserDTO, String authToken)
            throws ReportedUserAlreadyExist, UserNotFoundException {
        final long reporterId = jwtService.extractUserId(authToken);
        if (isReportExist(reporterId, reportUserDTO.reportedId())) {
            throw new ReportedUserAlreadyExist("User with id" + reportUserDTO.reportedId() + " is already reported");
        }
        userService.validateUserById(reportUserDTO.reportedId());
        reportedUsersRepository.save(ReportedUsers.builder()
                .reporterId(reporterId)
                .reportedId(reportUserDTO.reportedId())
                .reason(reportUserDTO.reason())
                .build());
    }

    public List<ReportedUsersResponseDTO> getReports() {
        Optional<List<ReportedUsers>> reportedUsers = reportedUsersRepository.findAllByReasonIsNotNull();
        return reportedUsers.map(users -> users.stream()
                        .map(r -> new ReportedUsersResponseDTO(r.getReporterId(), r.getReportedId(), r.getReason()))
                        .toList())
                .orElseGet(ArrayList::new);
    }


    public void hasPermission(User targetUser) throws AccessDeniedException {
        if (targetUser.isPrivate() && !isLoggedUserAdmin() && isLoggedUserFollow(targetUser)) {
            throw new AccessDeniedException(ACCESS_DENIED);
        }
    }

    private boolean isLoggedUserFollow(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return user.getFollowers().stream()
                .map(User::getUsername)
                .anyMatch(u -> u.contains(authentication.getName()));
    }

    private boolean isReportExist(long reporterId, long reportedId) {
        return reportedUsersRepository.findByReporterIdAndReportedId(reporterId, reportedId).isPresent();
    }

    private boolean isLoggedUserAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.contains(ADMIN));
    }
}
