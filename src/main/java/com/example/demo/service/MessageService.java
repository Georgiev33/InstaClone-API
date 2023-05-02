package com.example.demo.service;

import com.example.demo.model.dto.MessageResponse;

import com.example.demo.model.entity.Notification;
import com.example.demo.model.entity.User;

import com.example.demo.repository.NotificationRepository;
import com.example.demo.service.contracts.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
public class MessageService {
    private final UserValidationService userValidationService;
    private final JwtService jwtService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaAdmin kafkaAdmin;
    private final Properties chatHistoryConsumerProperties;
    private final NotificationRepository notificationRepository;


    public void sendMessage(String message, long receiverId, String token) {
        User receiver = userValidationService.findUserById(receiverId);
        long senderId = jwtService.extractUserId(token);
        User sender = userValidationService.findUserById(senderId);
        String topicName = generateRoomName(senderId, receiverId);
        NewTopic chatRoomTopic = new NewTopic(topicName, 2, (short) 1);
        kafkaAdmin.createOrModifyTopics(chatRoomTopic);

        kafkaTemplate.send(topicName, sender.getUsername() + ":" + message);


        Notification notification = Notification.builder().notification(sender.getUsername()
                        + " has sent you a new message: \n" + message)
                .user(receiver)
                .dateCreated(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }

    private String generateRoomName(Long senderId, Long receiverId) {
        if (senderId > receiverId) {
            return senderId + receiverId.toString();
        }
        return receiverId + senderId.toString();
    }

    public List<MessageResponse> getPageOfMessages(long otherUserId, int offset, int limit, String authToken) {
        userValidationService.validateUserById(otherUserId);
        long senderId = jwtService.extractUserId(authToken);
        String topicName = generateRoomName(senderId, otherUserId);
        List<MessageResponse> messageResponses = new ArrayList<>();

        Consumer<String, String> consumer = new KafkaConsumer<>(chatHistoryConsumerProperties);
        consumer.assign(Collections.singleton(new TopicPartition(topicName, 0)));
        long latestOffset = consumer.endOffsets(Collections.singleton(new TopicPartition(topicName, 0)))
                .get(new TopicPartition(topicName, 0));

        long desiredOffset = latestOffset - offset;
        if (desiredOffset < 0) {
            desiredOffset = 0;
        }

        consumer.seek(new TopicPartition(topicName, 0), desiredOffset);
        int counter = 0;
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(3000));
        for (ConsumerRecord<String, String> record : records) {
            if (counter == limit) {
                break;
            }
            messageResponses.add(new MessageResponse(record.value()));
            counter++;
        }
        consumer.close();
        return messageResponses;
    }
}
