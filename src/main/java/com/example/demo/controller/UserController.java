package com.example.demo.controller;

import com.example.demo.model.dto.UserLoginDTO;
import com.example.demo.model.dto.UserRegistrationDTO;
import com.example.demo.model.dto.UserWithUsernameAndIdDTO;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("{verificationCode}")
    public ResponseEntity<String> verifyUser(@PathVariable String verificationCode) {
        return userService.verifyUser(verificationCode);
    }

    @PostMapping("/register")
    public void createUser(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        userService.createUser(userRegistrationDTO);
    }

    @PostMapping("/auth")
    public ResponseEntity<String> login(@RequestBody UserLoginDTO userLoginDTO) {
        return ResponseEntity.ok(userService.login(userLoginDTO));
    }

//    @PostMapping("/logout")
//    public void logout(HttpSession httpSession) {
//        httpSession.invalidate();
//    }

    @PostMapping("/{followedUserId}")
    public void followUser(@PathVariable long followedUserId, @RequestHeader("Authorization") String authToken) {
        userService.followUser(followedUserId, authToken);
    }

    @GetMapping("/followers")
    public List<UserWithUsernameAndIdDTO> getFollowers(@RequestHeader("Authorization") String authToken){
       return userService.getFollowers(authToken);
    }
    @GetMapping("/following")
    public List<UserWithUsernameAndIdDTO> getFollowing(@RequestHeader("Authorization") String authToken){
        return userService.getFollowing(authToken);
    }


}
