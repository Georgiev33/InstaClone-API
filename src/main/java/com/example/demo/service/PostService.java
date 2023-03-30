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

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.time.LocalDateTime;

import java.util.List;

import java.util.stream.Collectors;

import static com.example.demo.util.Constants.*;

@Service
public class PostService {
    private final String serverPort;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final PostContentRepository contentRepository;
    private final TagService tagService;

    public PostService(@Autowired String serverPort, @Autowired FileService fileService,
                       @Autowired UserRepository userRepository, @Autowired PostRepository postRepository,
                       @Autowired ModelMapper modelMapper, @Autowired PostContentRepository contentRepository,
                       @Autowired TagService tagService) {
        this.serverPort = serverPort;
        this.fileService = fileService;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.modelMapper = modelMapper;
        this.contentRepository = contentRepository;
        this.tagService = tagService;
    }


    @Transactional
    public PostResponseDTO createPost(CreatePostDTO dto, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        if (dto.getContent() == null) {
            throw new BadRequestException(POST_CONTENT_IS_REQUIRED1);
        }

        Post post = new Post();
        post.setDateCreated(LocalDateTime.now());
        post.setCaption(dto.getCaption());
        post.setUser(user);

        tagService.addHashTags(dto.getHashtags(), post);

        Post saved = postRepository.save(post);

        PostResponseDTO responseDTO = modelMapper.map(saved, PostResponseDTO.class);
        setResponseUrl(responseDTO, post.getId());
        responseDTO.setHashtags(dto.getHashtags());


        for (MultipartFile file : dto.getContent()) {
            String fileName = fileService.saveFile(file, userId);
            PostContent content = new PostContent();
            content.setPost(post);
            content.setContentUrl(HTTP_LOCALHOST + serverPort + POST_CONTENT + fileName);
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
                .orElseThrow(() -> new BadRequestException(INVALID_POST_ID));

        return postContents.stream().map(PostContent::getContentUrl).collect(Collectors.toList());
    }

    public File getContent(String fileName) {
        return fileService.getFile(fileName);
    }


}
