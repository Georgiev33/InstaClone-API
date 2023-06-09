package com.example.demo.controller;

import com.example.demo.model.dto.ReportedUsers.ReportUserDTO;
import com.example.demo.model.dto.post.PostResponseDTO;
import com.example.demo.model.dto.user.*;
import com.example.demo.service.contracts.PostService;
import com.example.demo.service.contracts.UserService;
import com.example.demo.service.contracts.UserValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PostService postService;
    private final UserValidationService userValidationService;

    @PostMapping("/report")
    public void reportUser(@RequestBody @Valid ReportUserDTO reportUserDTO, @RequestHeader("Authorization") String authToken) {
        userService.reportUser(reportUserDTO, authToken);
    }

    @PutMapping()
    public void updateUser(@RequestBody UserUpdateDTO userUpdateDTO, @RequestHeader("Authorization") String authToken) {
        userService.updateUser(userUpdateDTO, authToken);
    }

    @PutMapping("/private")
    public void setPrivateUser(@RequestHeader("Authorization") String authToken) {
        userService.setPrivateUser(authToken);
    }

    @PostMapping("/register")
    public void createUser(@RequestBody @Valid UserRegistrationDTO userRegistrationDTO) {
        userService.createUser(userRegistrationDTO);
    }

    @PostMapping("/auth")
    public ResponseEntity<String> login(@RequestBody UserLoginDTO userLoginDTO) {
        return ResponseEntity.ok(userService.login(userLoginDTO));
    }

    @PostMapping("/{followedUserId}")
    public void followUser(@PathVariable long followedUserId, @RequestHeader("Authorization") String authToken) {
        userService.followUser(followedUserId, authToken);
    }
    @GetMapping("/auth/{verificationCode}")
    public ResponseEntity<String> verifyUser(@PathVariable String verificationCode) {
        return userValidationService.verifyUser(verificationCode);
    }

    @GetMapping("/{userId}")
    public UserWithoutPasswordDTO getUserById(@PathVariable long userId, @RequestHeader("Authorization") String authToken){
        return userService.getUserById(userId);
    }
    @GetMapping("/followers")
    public List<UserWithUsernameAndIdDTO> getFollowers(@RequestHeader("Authorization") String authToken) {
        return userService.getFollowers(authToken);
    }

    @GetMapping("/following")
    public List<UserWithUsernameAndIdDTO> getFollowing(@RequestHeader("Authorization") String authToken) {
        return userService.getFollowings(authToken);
    }

    @GetMapping("{userId}/posts")
    public Page<PostResponseDTO> getAllUserPosts(@PathVariable long userId,
                                                 @RequestParam(required = false, defaultValue = "0") int page,
                                                 @RequestParam(required = false, defaultValue = "10") int size) {
        return postService.getAllUserPosts(userId, page, size);
    }

    @GetMapping("/feed")
    public Page<PostResponseDTO> getFeed(@RequestHeader("Authorization") String authToken,
                                         @RequestParam(required = false, defaultValue = "0") int page,
                                         @RequestParam(required = false, defaultValue = "10") int size) {
        return postService.getFeed(authToken, page, size);
    }

    @DeleteMapping()
    public String deleteAccount(@RequestHeader("Authorization") String authToken){
        return userService.deleteAccount(authToken);
    }
    @DeleteMapping("/following/{userId}")
    public void unfollowUser(@PathVariable long userId, @RequestHeader("Authorization") String authToken){
        userService.unfollowUser(userId, authToken);
    }
}
