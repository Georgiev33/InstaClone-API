package com.example.demo.controller;

import com.example.demo.model.dto.UserLoginDTO;
import com.example.demo.model.dto.UserRegistrationDTO;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("{verificationCode}")
    public ResponseEntity<String> verifyUser(@PathVariable String verificationCode) {
        return userService.verifyUser(verificationCode);
    }

    @PostMapping()
    public void createUser(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        userService.createUser(userRegistrationDTO);
    }

    @PostMapping("/auth")
    public void login(@RequestBody UserLoginDTO userLoginDTO, HttpSession httpSession) {
        Long userId = userService.login(userLoginDTO);
        if (httpSession.getAttribute("LOGGED") != null) {
            throw new BadRequestException("You are already logged");
        }
        httpSession.setAttribute("LOGGED", true);
        httpSession.setAttribute("USER_ID", userId);
    }

    @PostMapping("/logout")
    public void logout(HttpSession httpSession) {
        httpSession.invalidate();
    }

}
