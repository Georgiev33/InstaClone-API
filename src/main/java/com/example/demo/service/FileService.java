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

@Service
@Slf4j
public class FileService {

    private final List<String> AVAILABLE_EXTENSIONS = Arrays.asList("image/jpeg","image/png","video/mp4","video/m4v");


    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    public String saveFile(MultipartFile content, Long uId) {
        String fileExtension = content.getContentType();
        if(!AVAILABLE_EXTENSIONS.contains(fileExtension)){
            throw new BadRequestException("Invalid file type.");
        }

        String fileName = System.nanoTime() + uId + content.getOriginalFilename().replaceAll("\\s", "");
        Path pathToFile = Paths.get("uploads");

        try {
           Files.copy(content.getInputStream(), pathToFile.resolve(fileName));
           logger.info("File " + fileName + " was saved successfully.");
        } catch (IOException e) {
           logger.error("Error while saving file " + fileName + " " + e.getMessage());
           throw new BadRequestException("An error occurred while saving file.");
        }
        return fileName;
    }

    public File getFile(String fileName) {
        Path pathToFile = Paths.get("uploads");
        System.out.println(pathToFile);
        File file = new File(pathToFile + File.separator +  fileName);
        if(!file.exists()){
            throw new BadRequestException("File doesn't exist.");
        }
        return file;
    }
}
