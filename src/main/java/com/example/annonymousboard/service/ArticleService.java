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

    public List<Article> readAllArticle() {
        return articleRepository.findAll();
    }

    public List<Article> readAllArticleDesc() {
        return articleRepository.findAllByOrderByIdDesc();
    }

    public Article readArticle(Long id) {
        Optional<Article> optionalArticle = articleRepository.findById(id);
        return optionalArticle.orElse(null);
    }

    public List<Article> readBoardArticle(Long boardId) {
        return articleRepository.findByBoardIdOrderByIdDesc(boardId);
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

    public List<Article> searchAllArticleByTitle(String title) {
        return articleRepository.findByTitleContainsOrderByIdDesc(title);
    }

    public List<Article> searchAllArticleByContent(String content) {
        return articleRepository.findByContentContainsOrderByIdDesc(content);
    }

    public List<Article> searchBoardArticleByTitle(Long boardId, String title) {
        return articleRepository.findByBoardIdAndTitleContainsOrderByIdDesc(boardId, title);
    }

    public List<Article> searchBoardArticleByContent(Long boardId, String content) {
        return articleRepository.findByBoardIdAndContentContainsOrderByIdDesc(boardId, content);
    }

    public Article getNextArticle(Long articleId) {
        Article article = readArticle(articleId);
        Long boardId = article.getBoard().getId();
        return articleRepository.findTopByBoardIdAndIdLessThanOrderByIdDesc(boardId, articleId).orElse(null);
    }

    public Article getPrevArticle(Long articleId) {
        Article article = readArticle(articleId);
        Long boardId = article.getBoard().getId();
        return articleRepository.findTopByBoardIdAndIdGreaterThanOrderById(boardId, articleId).orElse(null);
    }
}
