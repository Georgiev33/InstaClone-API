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
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Autowired
    private HashtagRepository hashtagRepository;
    @Value("${server.port}")
    private  String serverPort;
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
        for (String postHashtag : dto.getHashtags()) {
            Hashtag hashtag = hashtagRepository.findByTagName(postHashtag);
            if(hashtag == null){
               hashtag = createHashtag(postHashtag, post);
            }
            post.getHashtags().add(hashtag);
        }
        Post saved = postRepository.save(post);

        PostResponseDTO responseDTO = modelMapper.map(saved,PostResponseDTO.class);
        String contentUrl = "http://localhost:" + serverPort + "/post/media/" + post.getId();
        responseDTO.setContentUrl(contentUrl);
        responseDTO.setHashtags(dto.getHashtags());
        for(MultipartFile file : dto.getContent()){
            String fileName = fileService.saveFile(file, userId);
            PostContent content = new PostContent();
            content.setPost(post);
            content.setContentUrl("http://localhost:" + serverPort + "/post/content/" + fileName);
            contentRepository.save(content);

        }
        return responseDTO;
    }

    private Hashtag createHashtag(String hashtagName, Post post) {
        Hashtag hashtag = new Hashtag();
        hashtag.getPosts().add(post);
        hashtag.setTagName(hashtagName);
        return hashtagRepository.save(hashtag);
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
