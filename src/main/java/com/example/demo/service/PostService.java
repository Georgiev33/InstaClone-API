package com.example.demo.service;

import com.example.demo.model.dto.CreatePostDTO;
import com.example.demo.model.dto.PostResponseDTO;
import com.example.demo.model.entity.Hashtag;
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

import java.util.Set;
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
    private final HashTagService hashTagService;

    public PostService(@Autowired String serverPort, @Autowired FileService fileService,
                       @Autowired UserRepository userRepository, @Autowired PostRepository postRepository,
                       @Autowired ModelMapper modelMapper, @Autowired PostContentRepository contentRepository,
                       @Autowired HashTagService hashTagService) {
        this.serverPort = serverPort;
        this.fileService = fileService;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.modelMapper = modelMapper;
        this.contentRepository = contentRepository;
        this.hashTagService = hashTagService;
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

        hashTagService.addHashTags(dto.getHashtags(), post);

        Post saved = postRepository.save(post);

        for (MultipartFile file : dto.getContent()) {
            String fileName = fileService.saveFile(file, userId);
            PostContent content = new PostContent();
            content.setPost(post);
            content.setContentUrl(HTTP_LOCALHOST + serverPort + POST_CONTENT + fileName);
            contentRepository.save(content);
        }

        return mapPostToPostResponseDto(saved);
    }

    public List<String> getAllPostUrls(long postId) {
        List<PostContent> postContents = contentRepository.findAllByPostId(postId)
                .orElseThrow(() -> new BadRequestException(INVALID_POST_ID));

        return postContents.stream().map(PostContent::getContentUrl).collect(Collectors.toList());
    }

    public File getContent(String fileName) {
        return fileService.getFile(fileName);
    }

    private void setResponseUrl(PostResponseDTO responseDTO, Long postId) {
        String contentUrl = HTTP_LOCALHOST + serverPort + MEDIA_URI + postId;
        responseDTO.setContentUrl(contentUrl);
    }
    private void setHashTags(PostResponseDTO responseDTO, Set<Hashtag> hashtags) {
        for (Hashtag hashtag : hashtags) {
            responseDTO.getHashtags().add(hashtag.getTagName());
        }
    }

    private PostResponseDTO mapPostToPostResponseDto(Post post){
        PostResponseDTO responseDTO = new PostResponseDTO();
        responseDTO.setId(post.getId());
        responseDTO.setCaption(post.getCaption());
        responseDTO.setDateCreated(post.getDateCreated());
        setResponseUrl(responseDTO, post.getId());
        setHashTags(responseDTO, post.getHashtags());
        return responseDTO;
    }
}
