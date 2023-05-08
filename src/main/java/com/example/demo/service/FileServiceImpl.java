package com.example.demo.service;

import com.example.demo.model.entity.post.Post;
import com.example.demo.model.entity.post.PostContent;
import com.example.demo.model.exception.FileNotFoundException;
import com.example.demo.model.exception.FileSaveException;
import com.example.demo.model.exception.InvalidFileTypeException;
import com.example.demo.repository.PostContentRepository;
import com.example.demo.service.contracts.FileService;
import com.example.demo.util.constants.MessageConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.demo.util.constants.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final String serverPort;
    private final PostContentRepository postContentRepository;

    private final List<String> AVAILABLE_EXTENSIONS = Arrays.asList(IMAGE_JPEG, IMAGE_PNG, VIDEO_MP_4, VIDEO_M_4_V);

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String createContent(MultipartFile file, long userId) {

        String fileName = saveFile(file, userId);
        return (HTTP_LOCALHOST + serverPort + STORY_CONTENT + fileName);
    }

    public void createContent(List<MultipartFile> files, long userId, Post post) {
        List<PostContent> postContents = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = saveFile(file, userId);
            PostContent content = PostContent.builder()
                    .post(post)
                    .contentUrl(HTTP_LOCALHOST + serverPort + POST_CONTENT + fileName)
                    .build();
            post.getContentUrls().add(content);
          postContents.add(content);
        }
        postContentRepository.saveAll(postContents);
    }

    public String saveFile(MultipartFile content, Long uId) throws InvalidFileTypeException, FileSaveException {
        String fileExtension = content.getContentType();
        if (!AVAILABLE_EXTENSIONS.contains(fileExtension)) {
            throw new InvalidFileTypeException(MessageConstants.INVALID_FILE_TYPE);
        }

        String fileName = System.nanoTime() + uId + content.getOriginalFilename().replaceAll("\\s", "");
        Path pathToFile = Paths.get(UPLOADS);

        try {
            Files.copy(content.getInputStream(), pathToFile.resolve(fileName));
            logger.info(FILE + fileName + MessageConstants.WAS_SAVED_SUCCESSFULLY);
        } catch (IOException e) {
            logger.error(MessageConstants.ERROR_WHILE_SAVING_FILE + fileName + EMPTY + e.getMessage());
            throw new FileSaveException(MessageConstants.AN_ERROR_OCCURRED_WHILE_SAVING_FILE);
        }
        return fileName;
    }

    public File getFile(String fileName) throws FileNotFoundException{
        Path pathToFile = Paths.get(UPLOADS);
        File file = new File(pathToFile + File.separator + fileName);
        if (!file.exists()) {
            throw new FileNotFoundException(MessageConstants.FILE_DOESN_T_EXIST);
        }
        return file;
    }
}
