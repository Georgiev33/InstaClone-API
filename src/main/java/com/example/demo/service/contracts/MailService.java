package com.example.demo.service.contracts;

import com.example.demo.model.dto.user.UserRegistrationDTO;

public interface MailService {
     String sendVerificationEmail(UserRegistrationDTO userRegistrationDTO);
}
