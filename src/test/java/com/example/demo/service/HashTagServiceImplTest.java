package com.example.demo.service;

import com.example.demo.model.entity.Hashtag;
import com.example.demo.model.entity.post.Post;
import com.example.demo.repository.HashtagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.*;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class HashTagServiceImplTest {
    @Mock
    private HashtagRepository hashtagRepository;
    private HashTagServiceImpl hashTagService;
    @BeforeEach
    public void setUp() {
       hashTagService = new HashTagServiceImpl(hashtagRepository);
    }

    @Test
    public void addHashtagsWithEmptyListShouldNotCallRepository(){
        //Arrange
        Post post = Post.builder().build();

        //Act
        hashTagService.addHashTags(Optional.empty(), post);
        //Assert
        verify(hashtagRepository, never()).save(any(Hashtag.class));
        verify(hashtagRepository, never()).findByTagName(anyString());
    }
    @Test
    public void addHashTagsWithNewHashtagsShouldSaveHashtags(){
        //Arrange
        Post post = Post.builder().hashtags(new HashSet<>()).build();
        List<String> hashtags = List.of("testTag1", "testTag2");
        when(hashtagRepository.findByTagName(anyString())).thenReturn(Optional.empty());
        when(hashtagRepository.save(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
        //Act
        hashTagService.addHashTags(Optional.of(hashtags), post);

        //Assert
        verify(hashtagRepository, times(2)).findByTagName(anyString());
        verify(hashtagRepository, times(2)).save(any(Hashtag.class));
        assertThat(post.getHashtags()).hasSize(2);
    }

    @Test
    public void addHashtagsWithExistingHashtagsShouldntSaveHashtags(){
        //Arrange
        Post post = Post.builder().hashtags(new HashSet<>()).build();
        List<String> hashtags = List.of("testTag1", "testTag2");
        when(hashtagRepository.findByTagName(anyString())).thenAnswer(new Answer<Optional<Hashtag>>() {
            @Override
            public Optional<Hashtag> answer(InvocationOnMock invocation){
                Object[] arguments = invocation.getArguments();
                Hashtag hashtag = new Hashtag();
                hashtag.setTagName((String) arguments[0]);
                return Optional.of(hashtag);
            }
        });
        //Act
        hashTagService.addHashTags(Optional.of(hashtags), post);

        //Assert
        verify(hashtagRepository, times(2)).findByTagName(anyString());
        verify(hashtagRepository, never()).save(any(Hashtag.class));
        assertThat(post.getHashtags()).hasSize(2);
    }

    @Test
    public void createHashtagShouldSaveHashtag(){
        // Arrange
        String testTagToCreate = "testTag";
        when(hashtagRepository.save(any(Hashtag.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
        //Act
        Hashtag createdHashtag = hashTagService.createHashtag(testTagToCreate);

        //Assert
        verify(hashtagRepository, times(1)).save(any(Hashtag.class));
        assertThat(createdHashtag.getTagName()).isEqualTo(testTagToCreate);

    }
}
