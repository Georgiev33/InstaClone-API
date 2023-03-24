package com.example.demo.service;

import com.example.demo.model.dto.CreatePostDTO;
import com.example.demo.model.dto.PostResponseDTO;
import com.example.demo.model.entity.Post;
import com.example.demo.model.entity.PostContent;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.repository.PostContentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.Value;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private FileService fileService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PostContentRepository contentRepository;

    @Transactional
    public PostResponseDTO createPost(CreatePostDTO dto, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if(dto.getContent() == null){
            throw new BadRequestException("Can't upload a post with no media.");
        }

        Post post = new Post();
        post.setDateCreated(LocalDateTime.now());
        post.setCaption(dto.getCaption());
        post.setUser(user);
        Post saved = postRepository.save(post);

        PostResponseDTO responseDTO = modelMapper.map(saved,PostResponseDTO.class);
        String contentUrl = "http://localhost:8080/post/media/" + post.getId();
        responseDTO.setContentUrl(contentUrl);
        for(MultipartFile file : dto.getContent()){
            String fileName = fileService.saveFile(file, userId);
            PostContent content = new PostContent();
            content.setPost(post);
            content.setContentUrl("http://localhost:8080/post/content/" + fileName);
            contentRepository.save(content);

        }
        return responseDTO;
    }


    public List<String> getAllPostUrls(long postId) {
        List<PostContent> postContents = contentRepository.findAllByPostId(postId)
                .orElseThrow(() -> new BadRequestException("Invalid postId"));

        return postContents.stream().map(postContent -> postContent.getContentUrl()).collect(Collectors.toList());
    }

    public File getContent(String fileName) {
        return fileService.getFile(fileName);
    }
}
