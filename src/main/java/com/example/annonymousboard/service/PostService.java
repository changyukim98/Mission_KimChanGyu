package com.example.annonymousboard.service;

import com.example.annonymousboard.entity.Article;
import com.example.annonymousboard.repo.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public List<Article> readPostAll() {
        return postRepository.findAll();
    }

}
