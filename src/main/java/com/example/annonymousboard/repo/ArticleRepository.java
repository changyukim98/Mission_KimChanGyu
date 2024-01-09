package com.example.annonymousboard.repo;

import com.example.annonymousboard.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findAllByOrderByIdDesc();
    List<Article> findByBoardIdOrderByIdDesc(Long boardId);
}
