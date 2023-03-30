package com.example.demo.service;

import com.example.demo.model.dto.CreatePostDTO;
import com.example.demo.model.dto.PostResponseDTO;
import com.example.demo.model.entity.Post;
import com.example.demo.model.entity.User;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.repository.PostContentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

import static com.example.demo.util.Constants.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private FileService fileService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private PostContentRepository contentRepository;
    @Mock
    private TagService tagService;
    @Value("${server.port}")
    private String serverPort;
    private PostService underTest;

    @BeforeEach
    void setUp() {
        underTest = new PostService(serverPort, fileService, userRepository,
                postRepository, modelMapper, contentRepository, tagService);
    }

    @Test
    void nonExistentUserIdShouldThrowNotFound() {
        //given
        CreatePostDTO createPostDTO = new CreatePostDTO();

        //when
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //then
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> underTest.createPost(createPostDTO, 1L))
                .withMessage(USER_NOT_FOUND);
    }

    @Test
    void existingUserIdShouldntThrowNotFound() {
        //given
        CreatePostDTO createPostDTO = new CreatePostDTO();
        createPostDTO.setContent(new ArrayList<>());
        //when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(modelMapper.map(any(), any())).thenReturn(new PostResponseDTO());
        //then
        assertThat(catchThrowableOfType(() -> underTest.createPost(createPostDTO, 1L), NotFoundException.class)).isNull();
    }

    @Test
    void createPostRequestWithNoContentShouldThrowBadRequest() {
        //given
        CreatePostDTO createPostDTO = new CreatePostDTO();

        //when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        //then
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> underTest.createPost(createPostDTO, 1L))
                .withMessage(POST_CONTENT_IS_REQUIRED);

    }

    @Test
    void createPostRequestWithContentShouldntThrowBadRequest() {
        //given
        CreatePostDTO createPostDTO = new CreatePostDTO();
        createPostDTO.setContent(new ArrayList<>());
        //when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(modelMapper.map(any(), any())).thenReturn(new PostResponseDTO());
        //then
        assertThat(catchThrowableOfType(() -> underTest.createPost(createPostDTO, 1L), BadRequestException.class)).isNull();
    }


    @Test
    void createPostRequestWithTagsShouldInvokeAddHashTags() {
        //given
        CreatePostDTO createPostDTO = new CreatePostDTO();
        createPostDTO.setContent(new ArrayList<>());
        createPostDTO.setHashtags(new ArrayList<>());
        //when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(modelMapper.map(any(), any())).thenReturn(new PostResponseDTO());
        //then
        underTest.createPost(createPostDTO, 1L);
        verify(tagService).addHashTags(anyList(), any(Post.class));
    }

    @Test
    void createPostRequestWithoutTagsShouldntInvokeAddHashTags() {
        //given
        CreatePostDTO createPostDTO = new CreatePostDTO();
        createPostDTO.setContent(new ArrayList<>());
        //when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(modelMapper.map(any(), any())).thenReturn(new PostResponseDTO());
        //then
        underTest.createPost(createPostDTO, 1L);
        verify(tagService, never()).addHashTags(anyList(), any(Post.class));
    }

    @Test
    @SneakyThrows
    void createPostWithMultipleFilesShouldInvokeSaveFile() {
        //given
        CreatePostDTO createPostDTO = new CreatePostDTO();
        createPostDTO.setContent(new ArrayList<>());
        ArrayList<MultipartFile> mockFiles = new ArrayList<>();

        mockFiles.add(new MockMultipartFile("test", "testStr".getBytes(StandardCharsets.UTF_8)));
        mockFiles.add(new MockMultipartFile("test2", "testString".getBytes(StandardCharsets.UTF_8)));

        createPostDTO.setContent(mockFiles);
        //when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(modelMapper.map(any(), any())).thenReturn(new PostResponseDTO());
        //then
        underTest.createPost(createPostDTO, 1L);
        verify(fileService, times(2)).saveFile(any(), anyLong());
    }
}