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

    public boolean deleteArticle(Long id, String password) {
        Article article = readArticle(id);
        if (password.equals(article.getPassword())) {
            articleRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean updateArticle(
            Long id,
            String password,
            String writer,
            String title,
            String content
    ) {
        Article article = readArticle(id);
        if (article.getPassword().equals(password)) {
            article.setWriter(writer);
            article.setTitle(title);
            article.setContent(content);
            articleRepository.save(article);
            return true;
        }
        return false;
    }
}
