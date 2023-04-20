package com.example.demo.model;

import com.example.demo.model.entity.Hashtag;

import java.util.Set;

public interface Postable {
    Set<Hashtag> getHashtags();
}
