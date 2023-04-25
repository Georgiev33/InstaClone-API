package com.example.demo.controller;

import com.example.demo.model.dto.PostResponseDTO;
import com.example.demo.model.dto.UserLoginDTO;
import com.example.demo.model.dto.UserRegistrationDTO;
import com.example.demo.model.dto.UserWithUsernameAndIdDTO;
import com.example.demo.service.PostService;
import com.example.demo.service.UserService;
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

    @GetMapping("/auth/{verificationCode}")
    public ResponseEntity<String> verifyUser(@PathVariable String verificationCode) {
        return userService.verifyUser(verificationCode);
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
    public Page<PostResponseDTO> getAllUserPosts(@PathVariable long userId, @RequestParam int page, @RequestParam int size){
        return postService.getAllUserPosts(userId, page, size);
    }
}
