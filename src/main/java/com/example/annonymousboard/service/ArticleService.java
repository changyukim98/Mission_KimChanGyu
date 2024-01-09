package com.example.annonymousboard.service;

import com.example.annonymousboard.entity.Article;
import com.example.annonymousboard.repo.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        article.setHashtags(extractHashtags(article.getContent()));
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
            article.setHashtags(extractHashtags(content));
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
        return articleRepository.findTopByBoardIdAndIdGreaterThan(boardId, articleId).orElse(null);
    }

    public Article getNextArticleInEntire(Long articleId) {
        Article article = readArticle(articleId);
        Long boardId = article.getBoard().getId();
        return articleRepository.findTopByIdLessThanOrderByIdDesc(articleId).orElse(null);
    }

    public Article getPrevArticleInEntire(Long articleId) {
        Article article = readArticle(articleId);
        return articleRepository.findTopByIdGreaterThan(articleId).orElse(null);
    }

    public List<Article> searchArticleByHashtag(String hashtag) {
        return articleRepository.findByHashtagsContainsOrderByIdDesc(hashtag);
    }

    // 해시태그 추출 로직
    private Set<String> extractHashtags(String content) {
        Set<String> hashtags = new HashSet<>();
        Pattern pattern = Pattern.compile("#([0-9a-zA-Z가-힣]+)"); // #으로 시작하는 단어 패턴

        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String hashtag = matcher.group(); // 매치된 부분 가져오기
            hashtags.add(hashtag);
        }

        return hashtags;
    }
}
