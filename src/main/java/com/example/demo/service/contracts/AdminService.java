package com.example.demo.service.contracts;

import com.example.demo.model.dto.ReportedUsers.ReportUserDTO;
import com.example.demo.model.dto.ReportedUsers.ReportedUsersResponseDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.AccessDeniedException;
import com.example.demo.model.exception.ReportedUserAlreadyExist;
import com.example.demo.model.exception.UserNotFoundException;

import java.util.List;

public interface AdminService {
   void hasPermission(User targetUser) throws AccessDeniedException;

    void reportUser(ReportUserDTO reportUserDTO, String authToken)
            throws ReportedUserAlreadyExist, UserNotFoundException;

    List<ReportedUsersResponseDTO> getReports();

}
