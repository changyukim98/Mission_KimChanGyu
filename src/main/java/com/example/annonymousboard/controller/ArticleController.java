package com.example.annonymousboard.controller;

import com.example.annonymousboard.entity.Article;
import com.example.annonymousboard.entity.Board;
import com.example.annonymousboard.service.ArticleService;
import com.example.annonymousboard.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/article")
public class ArticleController {
    private final BoardService boardService;
    private final ArticleService articleService;

    @GetMapping("/{articleId}")
    public String articleView(
            @PathVariable("articleId") Long id,
            Model model
    ) {
        model.addAttribute("article", articleService.readArticle(id));
        return "article/article-view";
    }

    @GetMapping("/create")
    public String createArticleView(
            Model model
    ) {
        model.addAttribute("boards", boardService.readBoardAll());
        return "article/article-create";
    }

    @PostMapping("/create")
    public String createArticle(
            @RequestParam("boardId") Long boardId,
            @RequestParam("writer") String writer,
            @RequestParam("password") String password,
            @RequestParam("title") String title,
            @RequestParam("content") String content
    ) {
        Board board = boardService.readBoard(boardId);

        Article article = new Article();
        article.setBoard(board);
        article.setWriter(writer);
        article.setPassword(password);
        article.setTitle(title);
        article.setContent(content);
        Long articleId = articleService.saveArticle(article);
        return "redirect:/article/" + articleId;
    }

    @GetMapping("/{articleId}/delete")
    public String deleteArticleView(
            @PathVariable("articleId") Long articleId,
            Model model
    ) {
        model.addAttribute("articleId", articleId);
        return "/article/article-delete";
    }

    @PostMapping("/{articleId}/delete")
    public String deleteArticle(
            @PathVariable("articleId") Long articleId
    ) {
        articleService.deleteArticle(articleId);
        return "redirect:/boards/";
    }
}
