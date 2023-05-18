package com.example.demo.service;

import com.example.demo.model.dto.ReportedUsers.ReportHistoryResponseDTO;
import com.example.demo.model.dto.banUser.BanUserDTO;
import com.example.demo.model.dto.banUser.HandleReportDTO;
import com.example.demo.model.entity.report.ReportHistory;
import com.example.demo.model.entity.report.ReportedUser;
import com.example.demo.repository.report.ReportHistoryRepository;
import com.example.demo.repository.report.ReportedUserRepository;
import com.example.demo.service.contracts.BanUserService;
import com.example.demo.service.contracts.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;


import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {

    @Mock
    private ReportedUserRepository reportedUserRepository;
    @Mock
    private ReportHistoryRepository reportHistoryRepository;
    @Mock
    private  BanUserService banUserService;
    @Mock
    private JwtService jwtService;

    private AdminServiceImpl adminService;
    private static final String AUTH_TOKEN = "dummyAuthToken";
    @BeforeEach
    public void setUp() {
        this.adminService = new AdminServiceImpl(reportedUserRepository, reportHistoryRepository, banUserService, jwtService);
    }

    @Test
    public void getReportHistoryWithMinusOneReportedIdShouldGetPageOfReports(){
        //Arrange
        ReportHistory testReport = ReportHistory.builder().reportedId(2).build();
        ReportHistory testReport2 = ReportHistory.builder().reportedId(3).build();
        ReportHistory testReport3 = ReportHistory.builder().reportedId(4).build();
        int pageSize = 3;
        int page = 0;
        List<ReportHistory> reportHistories = List.of(testReport, testReport2, testReport3);
        long reportedId = -1;
        when(reportHistoryRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(reportHistories) {
        });
        //Act
        Page<ReportHistoryResponseDTO> resultPage = adminService.getReportHistory(reportedId,page,pageSize);
        //Assert
        verify(reportedUserRepository, never()).findAllByReportedId(anyLong());
        assertThat(resultPage.getTotalPages()).isEqualTo(1);
        assertThat(resultPage.getSize()).isEqualTo(3);

    }

    @Test
    public void getReportHistoryWithReportIdShouldReturnPageOfReportsForThatId(){
        //Arrange
        ReportHistory testReport = ReportHistory.builder().reportedId(3).build();
        ReportHistory testReport2 = ReportHistory.builder().reportedId(3).build();
        ReportHistory testReport3 = ReportHistory.builder().reportedId(3).build();
        int pageSize = 3;
        int page = 0;
        List<ReportHistory> reportHistories = List.of(testReport, testReport2, testReport3);
        long reportedId = 3;
        when(reportHistoryRepository.findAllByReportedId(anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(reportHistories) {
        });
        //Act
        Page<ReportHistoryResponseDTO> resultPage = adminService.getReportHistory(reportedId,page,pageSize);
        //Assert
        verify(reportedUserRepository, never()).findAll(any(PageRequest.class));
        assertThat(resultPage.getTotalPages()).isEqualTo(1);
        assertThat(resultPage.getSize()).isEqualTo(3);
    }
    @Test
    public void handleReportWithStatusTrueShouldBanUserAndDeleteAllActiveReports(){
        //Arrange
        ReportedUser reportToHandle = ReportedUser.builder().id(1L).build();
        ReportedUser otherUserReport1 = ReportedUser.builder().build();
        ReportedUser otherUserReport2 = ReportedUser.builder().build();
        List<ReportedUser> reportedUserList = List.of(reportToHandle, otherUserReport1, otherUserReport2);
        HandleReportDTO handleReportDTO = new HandleReportDTO(1, "test reason", true, 72);
        when(reportedUserRepository.findAllByReportedId(anyLong())).thenReturn(reportedUserList);
        when(reportedUserRepository.findById(anyLong())).thenReturn(Optional.of(reportToHandle));
        ArgumentCaptor<BanUserDTO> banUserDTOArgumentCaptor = ArgumentCaptor.forClass(BanUserDTO.class);
        ArgumentCaptor<List> reportHistoryListCaptor = ArgumentCaptor.forClass(List.class);
        //Act
        adminService.handleReport(AUTH_TOKEN, handleReportDTO);
        //Assert
        verify(banUserService, times(1)).banUser(banUserDTOArgumentCaptor.capture(), anyString());
        BanUserDTO capturedBanUserDTO = banUserDTOArgumentCaptor.getValue();
        assertThat(capturedBanUserDTO.userIdToBan()).isEqualTo(reportToHandle.getReportedId());
        assertThat(capturedBanUserDTO.reason()).isEqualTo(handleReportDTO.reason());
        assertThat(capturedBanUserDTO.hoursToBan()).isEqualTo(handleReportDTO.hoursToBan());


        verify(reportedUserRepository, times(1)).deleteAll(eq(reportedUserList));
        verify(reportHistoryRepository, times(1)).saveAll(reportHistoryListCaptor.capture());
        List<ReportHistory> capturedReportedUsers = reportHistoryListCaptor.getValue();
        assertThat(capturedReportedUsers).hasSize(3);
        verify(reportedUserRepository, never()).delete(any(ReportedUser.class));
        verify(reportHistoryRepository, never()).save(any(ReportHistory.class));

    }
    @Test
    public void handleReportWithStatusFalseShouldntBanUserAndDeleteAllActiveReports(){
        //Arrange
        ReportedUser reportToHandle = ReportedUser.builder().id(1L).build();


        HandleReportDTO handleReportDTO = new HandleReportDTO(1, "test reason", false, 0);
        ArgumentCaptor<ReportHistory> reportHistoryArgumentCaptor = ArgumentCaptor.forClass(ReportHistory.class);
        when(reportedUserRepository.findById(anyLong())).thenReturn(Optional.of(reportToHandle));

        //Act
        adminService.handleReport(AUTH_TOKEN, handleReportDTO);
        //Assert
        verify(banUserService, never()).banUser(any(BanUserDTO.class), anyString());
        verify(reportedUserRepository, never()).findAllByReportedId(anyLong());
        verify(reportedUserRepository, never()).deleteAll(anyList());
        verify(reportHistoryRepository, never()).saveAll(anyList());

        verify(reportedUserRepository, times(1)).delete(eq(reportToHandle));
        verify(reportHistoryRepository, times(1)).save(reportHistoryArgumentCaptor.capture());
        ReportHistory capturedReportHistory = reportHistoryArgumentCaptor.getValue();
        assertThat(capturedReportHistory.getReportedId()).isEqualTo(reportToHandle.getReportedId());
        assertThat(capturedReportHistory.isStatus()).isEqualTo(false);
    }

}
