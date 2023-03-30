package com.example.demo.controller;

import com.example.demo.model.dto.UserLoginDTO;
import com.example.demo.model.dto.UserRegistrationDTO;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.service.UserService;
import com.example.demo.util.UserServiceHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.util.Constants.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final UserServiceHelper userServiceHelper;

    public UserController(@Autowired UserService userService, @Autowired UserServiceHelper userServiceHelper) {
        this.userService = userService;
        this.userServiceHelper = userServiceHelper;
    }

    @GetMapping("{verificationCode}")
    public ResponseEntity<String> verifyUser(@PathVariable String verificationCode) {
        return userServiceHelper.verifyUser(verificationCode);
    }

    @PostMapping()
    public void createUser(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        userService.createUser(userRegistrationDTO);
    }

    @PostMapping("/auth")
    public void login(@RequestBody UserLoginDTO userLoginDTO, HttpSession httpSession) {
        Long userId = userService.login(userLoginDTO);
        if (httpSession.getAttribute(LOGGED) != null) {
            throw new BadRequestException(YOU_ARE_ALREADY_LOGGED);
        }
        httpSession.setAttribute(LOGGED, true);
        httpSession.setAttribute(USER_ID, userId);
    }

    @PostMapping("/logout")
    public void logout(HttpSession httpSession) {
        httpSession.invalidate();
    }

    @PostMapping("/{followedUserId}")
    public void followUser(@PathVariable long followedUserId, HttpSession httpSession) {
        userService.followUser(followedUserId, (long) httpSession.getAttribute(USER_ID));
    }

}
