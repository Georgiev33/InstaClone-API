package com.example.demo.service.contracts;

import com.example.demo.model.dto.user.UserRegistrationDTO;
import org.modelmapper.internal.bytebuddy.utility.RandomString;

import static com.example.demo.util.Constants.*;

public interface MailService {
     String sendVerificationEmail(UserRegistrationDTO userRegistrationDTO);
}
