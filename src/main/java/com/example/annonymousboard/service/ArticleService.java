package com.example.annonymousboard.service;

import com.example.annonymousboard.entity.Article;
import com.example.annonymousboard.repo.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;

    // 모든 게시글 가져오기
    public List<Article> readAllArticle() {
        return articleRepository.findAll();
    }

    // 게시글을 역순으로 가져오기
    public List<Article> readAllArticleDesc() {
        return articleRepository.findAllByOrderByIdDesc();
    }

    // id로 게시글을 가져오기
    public Article readArticle(Long id) {
        Optional<Article> optionalArticle = articleRepository.findById(id);
        return optionalArticle.orElse(null);
    }

    // 특정 게시판의 게시글만 가져오기
    public List<Article> readBoardArticle(Long boardId) {
        return articleRepository.findByBoardIdOrderByIdDesc(boardId);
    }

    // 게시글 저장
    public Long saveArticle(Article article) {
        // 게시글 저장 전 hashtag 저장
        article.setHashtags(extractHashtags(article.getContent()));
        Article saved = articleRepository.save(article);
        return saved.getId();
    }

    // 게시글 삭제
    public boolean deleteArticle(Long id, String password) throws IOException {
        Article article = readArticle(id);
        if (password.equals(article.getPassword())) {
            deleteAllImageFromArticle(id);
            articleRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 게시글 수정
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
            // 수정 시 해시태그 재추출 및 저장
            article.setHashtags(extractHashtags(content));
            articleRepository.save(article);
            return true;
        }
        return false;
    }

    // 제목을 기준으로 전체 게시글 검색
    public List<Article> searchAllArticleByTitle(String title) {
        return articleRepository.findByTitleContainsOrderByIdDesc(title);
    }

    // 내용을 기준으로 전체 게시글 검색
    public List<Article> searchAllArticleByContent(String content) {
        return articleRepository.findByContentContainsOrderByIdDesc(content);
    }

    // 제목을 기준으로 특정 게시판의 게시글 검색
    public List<Article> searchBoardArticleByTitle(Long boardId, String title) {
        return articleRepository.findByBoardIdAndTitleContainsOrderByIdDesc(boardId, title);
    }

    // 내용을 기준으로 특정 게시판의 게시글 검색
    public List<Article> searchBoardArticleByContent(Long boardId, String content) {
        return articleRepository.findByBoardIdAndContentContainsOrderByIdDesc(boardId, content);
    }

    // 게시글의 특정 게시판 내부의 다음글 가져오기
    public Article getNextArticle(Long articleId) {
        Article article = readArticle(articleId);
        Long boardId = article.getBoard().getId();
        return articleRepository.findTopByBoardIdAndIdLessThanOrderByIdDesc(boardId, articleId).orElse(null);
    }

    // 게시글의 특정 게시판 내부의 이전글 가져오기
    public Article getPrevArticle(Long articleId) {
        Article article = readArticle(articleId);
        Long boardId = article.getBoard().getId();
        return articleRepository.findTopByBoardIdAndIdGreaterThan(boardId, articleId).orElse(null);
    }

    // 전체 게시글에서 다음글 가져오기
    public Article getNextArticleInEntire(Long articleId) {
        return articleRepository.findTopByIdLessThanOrderByIdDesc(articleId).orElse(null);
    }

    // 전체 게시글에서 이전글 가져오기
    public Article getPrevArticleInEntire(Long articleId) {
        return articleRepository.findTopByIdGreaterThan(articleId).orElse(null);
    }

    // 해시태그를 이용해 게시글 검색
    public List<Article> searchArticleByHashtag(String hashtag) {
        return articleRepository.findByHashtagsContainsOrderByIdDesc(hashtag);
    }

    // 게시글에 이미지 추가
    public boolean saveArticleWithImage(Long articleId, MultipartFile file, String password) throws IOException {
        Article article = readArticle(articleId);
        if (article == null || !article.getPassword().equals(password)) {
            return false;
        }
        if (!file.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String filePath = System.getProperty("user.dir") + "/src/main/resources/static/files/" + fileName;

            // 이미지를 서버에 저장
            File dest = new File(filePath);
            dest.mkdirs();
            file.transferTo(dest);

            // 이미지 경로를 Entity에 추가
            article.getImages().add(fileName);

            articleRepository.save(article);
            return true;
        }
        return false;
    }

    // 게시글의 이미지 전부 삭제
    public void deleteAllImageFromArticle(Long articleId) throws IOException {
        Article article = readArticle(articleId);
        if (article == null) return;
        List<String> images = article.getImages();
        for (String image : images) {
            deleteFile(image);
        }
        images.clear();
    }

    // 특정 게시글의 이미지 삭제
    public boolean deleteImageFromArticle(Long articleId, String password, String image) throws IOException {
        Article article = readArticle(articleId);
        if (article == null || !article.getPassword().equals(password)) return false;
        List<String> images = article.getImages();
        images.remove(image);
        articleRepository.save(article);
        deleteFile(image);
        return true;
    }

    // 파일 삭제
    public void deleteFile(String fileName) throws IOException {
        String filePath = System.getProperty("user.dir") + "/src/main/resources/static/files/" + fileName;
        Files.deleteIfExists(Paths.get(filePath));
    }

    // 해시태그 추출 로직
    private Set<String> extractHashtags(String content) {
        Set<String> hashtags = new HashSet<>();
        Pattern pattern = Pattern.compile("#([0-9a-zA-Z가-힣]+)"); // #으로 시작하는 단어 패턴

        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String hashtag = matcher.group(); // 매치된 부분 가져오기
            hashtags.add(hashtag); // set에 저장
        }

        return hashtags;
    }
}
