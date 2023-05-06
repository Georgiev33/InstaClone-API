package com.example.demo.service.contracts;

import com.example.demo.model.Postable;
import com.example.demo.model.entity.Hashtag;

import java.util.List;
import java.util.Optional;

public interface HashTagService {
    void addHashTags(Optional<List<String>> tagsToAdd, Postable post);

     Hashtag createHashtag(String hashtagName);
}
