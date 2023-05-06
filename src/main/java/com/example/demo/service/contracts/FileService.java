package com.example.demo.service.contracts;

import com.example.demo.model.entity.post.Post;
import com.example.demo.model.exception.FileSaveException;
import com.example.demo.model.exception.InvalidFileTypeException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface FileService{
    String createContent(MultipartFile file, long userId);
    void createContent(List<MultipartFile> files, long userId, Post post);
    String saveFile(MultipartFile content, Long uId) throws InvalidFileTypeException, FileSaveException;
    File getFile(String fileName);
}
