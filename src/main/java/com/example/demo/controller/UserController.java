package com.example.demo.controller;

import com.example.demo.model.dto.UserLoginDTO;
import com.example.demo.model.dto.UserRegistrationDTO;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping()
    public void createUser(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        userService.createUser(userRegistrationDTO);
    }

    @PostMapping("/auth")
    public void login(@RequestBody UserLoginDTO userLoginDTO, HttpSession httpSession) {
        userService.login(userLoginDTO);
        if (httpSession.getAttribute("LOGGED") != null)
            throw new BadRequestException("You are already logged");
        httpSession.setAttribute("LOGGED", true);
    }

    @PostMapping("/logout")
    public void logout(HttpSession httpSession) {
        httpSession.invalidate();
    }

}
