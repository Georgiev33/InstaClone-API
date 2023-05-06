package com.example.demo.service;

import com.example.demo.model.Postable;
import com.example.demo.model.entity.Hashtag;
import com.example.demo.repository.HashtagRepository;
import com.example.demo.service.contracts.HashTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class HashTagServiceImpl implements HashTagService {
    private final HashtagRepository hashtagRepository;

    @Override
    public void addHashTags(Optional<List<String>> tagsToAdd, Postable post) {
        if (tagsToAdd.isEmpty()) return;
        for (String hashtagToAdd : tagsToAdd.get()) {
            Optional<Hashtag> hashtag = hashtagRepository.findByTagName(hashtagToAdd);
            post.getHashtags().add(hashtag.orElseGet(() -> createHashtag(hashtagToAdd)));
        }
    }
    @Override
    public Hashtag createHashtag(String hashtagName)
    {
        //TODO should add logic to set postable in hashtag
        Hashtag hashtag = new Hashtag();
        hashtag.setTagName(hashtagName);
        return hashtagRepository.save(hashtag);
    }
}