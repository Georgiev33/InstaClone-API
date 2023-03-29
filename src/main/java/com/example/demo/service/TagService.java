package com.example.demo.service;

import com.example.demo.model.entity.Hashtag;
import com.example.demo.model.entity.Post;
import com.example.demo.repository.HashtagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TagService {
    @Autowired
    private HashtagRepository hashtagRepository;
    //add nonexistent tags to db, if they exist add them to posts' set
    public void addHashTags(List<String> tagsToAdd, Set<Hashtag> postHashTags, Post post) {
        for (String postHashtag : tagsToAdd) {
            Hashtag hashtag = hashtagRepository.findByTagName(postHashtag);
            if(hashtag == null){
                hashtag = createHashtag(postHashtag, post);
            }
            postHashTags.add(hashtag);
        }
    }

    private Hashtag createHashtag(String hashtagName, Post post) {
        Hashtag hashtag = new Hashtag();
        hashtag.getPosts().add(post);
        hashtag.setTagName(hashtagName);
        return hashtagRepository.save(hashtag);
    }
}
