package com.example.demo.controller;

import com.example.demo.model.dto.ReportedUsers.ReportUserDTO;
import com.example.demo.model.dto.post.PostResponseDTO;
import com.example.demo.model.dto.User.UserLoginDTO;
import com.example.demo.model.dto.User.UserRegistrationDTO;
import com.example.demo.model.dto.User.UserUpdateDTO;
import com.example.demo.model.dto.User.UserWithUsernameAndIdDTO;
import com.example.demo.service.AdminServiceImpl;
import com.example.demo.service.PostService;
import com.example.demo.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;
    private final PostService postService;
    private final AdminServiceImpl adminService;

    @GetMapping("/auth/{verificationCode}")
    public ResponseEntity<String> verifyUser(@PathVariable String verificationCode) {
        return userService.verifyUser(verificationCode);
    }

    @PostMapping("/report")
    public void reportUser(@RequestBody ReportUserDTO reportUserDTO, @RequestHeader("Authorization") String authToken) {
        adminService.reportUser(reportUserDTO, authToken);
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
    public void createUser(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        System.out.println("test");
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

    @GetMapping("/followers")
    public List<UserWithUsernameAndIdDTO> getFollowers(@RequestHeader("Authorization") String authToken) {
        return userService.getFollowers(authToken);
    }

    @GetMapping("/following")
    public List<UserWithUsernameAndIdDTO> getFollowing(@RequestHeader("Authorization") String authToken) {
        return userService.getFollowing(authToken);
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
}
