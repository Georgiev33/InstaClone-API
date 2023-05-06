package com.example.demo.service.contracts;

import com.example.demo.model.dto.MessageResponse;

import java.util.List;

public interface MessageService {
    void sendMessage(String message, long receiverId, String token);

    List<MessageResponse> getPageOfMessages(long otherUserId, int offset, int limit, String authToken);
}
