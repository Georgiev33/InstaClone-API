package com.example.demo.service;

import com.example.demo.model.entity.Story;
import com.example.demo.model.exception.InvalidMultipartFileException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.example.demo.util.Constants.*;

@Service
@RequiredArgsConstructor
public class ContentService {
    private final String serverPort;
    private final FileService fileService;
    public void createStoryContent(MultipartFile file, long userId, Story story){
        if(file == null){
            throw new InvalidMultipartFileException(STORY_CONTENT_IS_REQUIRED1);
        }
        String fileName = fileService.saveFile(file, userId);
        story.setContentUrl(HTTP_LOCALHOST + serverPort + STORY_CONTENT + fileName);
    }
}
