package com.example.demo.controller;

import com.example.demo.model.dto.MessageRequest;
import com.example.demo.model.dto.MessageResponse;
import com.example.demo.service.contracts.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final MessageService messageService;
    @PostMapping("{receiverId}")
    public void sendMessage(@PathVariable long receiverId,
                            @RequestHeader("Authorization") String authToken,
                            @RequestBody MessageRequest message){
        messageService.sendMessage(message.message(),receiverId,authToken);
    }

    @GetMapping("history/{otherUserId}/{offset}/{limit}")
    public List<MessageResponse> getPageOfMessages(@PathVariable long otherUserId,
                                                   @PathVariable int offset,
                                                   @PathVariable int limit,
                                                   @RequestHeader("Authorization") String authToken){
       return messageService.getPageOfMessages(otherUserId, offset, limit, authToken);
    }

}
