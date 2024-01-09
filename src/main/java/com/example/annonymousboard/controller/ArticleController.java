package com.example.annonymousboard.controller;

import com.example.annonymousboard.entity.Article;
import com.example.annonymousboard.entity.Board;
import com.example.annonymousboard.entity.Comment;
import com.example.annonymousboard.service.ArticleService;
import com.example.annonymousboard.service.BoardService;
import com.example.annonymousboard.service.CommentService;
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
    private final CommentService commentService;

    @GetMapping("/{articleId}")
    public String articleView(
            @PathVariable("articleId") Long id,
            Model model
    ) {
        model.addAttribute("article", articleService.readArticle(id));
        model.addAttribute("nextArticle", articleService.getNextArticle(id));
        model.addAttribute("prevArticle", articleService.getPrevArticle(id));
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
            @PathVariable("articleId") Long articleId,
            @RequestParam("password") String password,
            Model model
    ) {
        if (articleService.deleteArticle(articleId, password)) {
            return "redirect:/boards/";
        } else {
            model.addAttribute("articleId", articleId);
            return "article/delete-failed";
        }
    }

    @GetMapping("/{articleId}/update")
    public String updateArticleForm(
            @PathVariable("articleId") Long articleId,
            Model model
    ) {
        Article article = articleService.readArticle(articleId);
        model.addAttribute("article", article);
        return "article/article-update";
    }

    @PostMapping("/{articleId}/update")
    public String updateArticle(
            @PathVariable("articleId") Long articleId,
            @RequestParam("password") String password,
            @RequestParam("writer") String writer,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            Model model
    ) {
        // 성공시
        if (articleService.updateArticle(articleId, password, writer, title, content)) {
            return String.format("redirect:/article/%d", articleId);
        } else {
            model.addAttribute("articleId", articleId);
            return "article/update-failed";
        }
    }

    @PostMapping("/{articleId}/comment/")
    public String createComment(
            @PathVariable("articleId") Long articleId,
            @RequestParam("comment_writer") String writer,
            @RequestParam("comment_password") String password,
            @RequestParam("comment_content") String content
    ) {
        Article article = articleService.readArticle(articleId);
        Comment comment = new Comment();
        comment.setArticle(article);
        comment.setWriter(writer);
        comment.setPassword(password);
        comment.setContent(content);
        commentService.saveComment(comment);
        return String.format("redirect:/article/%d", articleId);
    }

    @GetMapping("/{articleId}/comment/{commentId}/delete")
    public String deleteCommentView(
            @PathVariable("articleId") Long articleId,
            @PathVariable("commentId") Long commentId,
            Model model
    ) {
        model.addAttribute("articleId", articleId);
        model.addAttribute("commentId", commentId);
        return "comment/comment-delete";
    }

    @PostMapping("/{articleId}/comment/{commentId}/delete")
    public String deleteComment(
            @PathVariable("articleId") Long articleId,
            @PathVariable("commentId") Long commentId,
            @RequestParam("password") String password,
            Model model
    ) {
        if (commentService.deleteComment(commentId, password)) {
            return "redirect:/article/" + articleId;
        } else {
            model.addAttribute("articleId", articleId);
            return "comment/delete-failed";
        }
    }
}
