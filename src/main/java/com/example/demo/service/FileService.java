package com.example.demo.service;

import com.example.demo.model.exception.BadRequestException;
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
import java.util.Arrays;
import java.util.List;

import static com.example.demo.util.Constants.*;

@Service
@Slf4j
public class FileService {

    private final List<String> AVAILABLE_EXTENSIONS = Arrays.asList(IMAGE_JPEG, IMAGE_PNG, VIDEO_MP_4, VIDEO_M_4_V);

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    public String saveFile(MultipartFile content, Long uId) {
        String fileExtension = content.getContentType();
        if (!AVAILABLE_EXTENSIONS.contains(fileExtension)) {
            throw new BadRequestException(INVALID_FILE_TYPE);
        }

        String fileName = System.nanoTime() + uId + content.getOriginalFilename().replaceAll("\\s", "");
        Path pathToFile = Paths.get(UPLOADS);

        try {
            Files.copy(content.getInputStream(), pathToFile.resolve(fileName));
            logger.info(FILE + fileName + WAS_SAVED_SUCCESSFULLY);
        } catch (IOException e) {
            logger.error(ERROR_WHILE_SAVING_FILE + fileName + EMPTY + e.getMessage());
            throw new BadRequestException(AN_ERROR_OCCURRED_WHILE_SAVING_FILE);
        }
        return fileName;
    }

    public File getFile(String fileName) {
        Path pathToFile = Paths.get(UPLOADS);
        System.out.println(pathToFile);
        File file = new File(pathToFile + File.separator + fileName);
        if (!file.exists()) {
            throw new BadRequestException(FILE_DOESN_T_EXIST);
        }
        return file;
    }
}
