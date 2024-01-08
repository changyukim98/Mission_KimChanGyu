package com.example.annonymousboard.controller;

import com.example.annonymousboard.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/article")
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/{articleId}")
    public String articleView(
            @PathVariable("articleId") Long id,
            Model model
    ) {
        model.addAttribute("article", articleService.readArticle(id));
        return "article-view";
    }

}
