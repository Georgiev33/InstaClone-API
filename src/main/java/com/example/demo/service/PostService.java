package com.example.demo.service;

import com.example.demo.model.dto.CreatePostDTO;
import com.example.demo.model.dto.PostResponseDTO;
import com.example.demo.model.entity.Hashtag;
import com.example.demo.model.entity.Post;
import com.example.demo.model.entity.PostContent;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.repository.HashtagRepository;
import com.example.demo.repository.PostContentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.time.LocalDateTime;

import java.util.List;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {

    public static final String MEDIA_URI = "/post/media/";
    public static final String HTTP_LOCALHOST = "http://localhost:";
    @Autowired
    private String serverPort;
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
    @Autowired
    private TagService tagService;


    @Transactional
    public PostResponseDTO createPost(CreatePostDTO dto, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if(dto.getContent() == null){
            throw new BadRequestException("Post content is required.");
        }

        Post post = new Post();
        post.setDateCreated(LocalDateTime.now());
        post.setCaption(dto.getCaption());
        post.setUser(user);

        tagService.addHashTags(dto.getHashtags(), post.getHashtags(), post);

        Post saved = postRepository.save(post);

        PostResponseDTO responseDTO = modelMapper.map(saved,PostResponseDTO.class);
        setResponseUrl(responseDTO, post.getId());
        responseDTO.setHashtags(dto.getHashtags());


        for(MultipartFile file : dto.getContent()){
            String fileName = fileService.saveFile(file, userId);
            PostContent content = new PostContent();
            content.setPost(post);
            content.setContentUrl(HTTP_LOCALHOST + serverPort + "/post/content/" + fileName);
            contentRepository.save(content);
        }

        return responseDTO;
    }

    private void setResponseUrl(PostResponseDTO responseDTO, Long postId) {
        String contentUrl = HTTP_LOCALHOST + serverPort + MEDIA_URI + postId;
        responseDTO.setContentUrl(contentUrl);
    }

    public List<String> getAllPostUrls(long postId) {
        List<PostContent> postContents = contentRepository.findAllByPostId(postId)
                .orElseThrow(() -> new BadRequestException("Invalid postId"));

        return postContents.stream().map(PostContent::getContentUrl).collect(Collectors.toList());
    }

    public File getContent(String fileName) {
        return fileService.getFile(fileName);
    }


}
