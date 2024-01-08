package com.example.annonymousboard.service;

import com.example.annonymousboard.entity.Article;
import com.example.annonymousboard.repo.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;

    public List<Article> readArticleAll() {
        return articleRepository.findAll();
    }

    public Article readArticle(Long id) {
        Optional<Article> optionalArticle = articleRepository.findById(id);
        return optionalArticle.orElse(null);
    }

    public Long saveArticle(Article article) {
        Article saved = articleRepository.save(article);
        return saved.getId();
    }

}
