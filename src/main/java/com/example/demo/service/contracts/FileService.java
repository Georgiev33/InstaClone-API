package com.example.demo.service.contracts;

import com.example.demo.model.entity.post.Post;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface FileService{
    String createContent(MultipartFile file, long userId);
    void createContent(List<MultipartFile> files, long userId, Post post);
    String saveFile(MultipartFile content, Long uId);
    File getFile(String fileName);
}
