package com.example.annonymousboard.repo;

import com.example.annonymousboard.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Article, Long> {
}
