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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/article")
public class ArticleController {
    private final BoardService boardService;
    private final ArticleService articleService;
    private final CommentService commentService;

    // article 읽기 화면
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

    // article 작성 폼
    @GetMapping("/create")
    public String createArticleForm(
            Model model
    ) {
        model.addAttribute("boards", boardService.readBoardAll());
        return "article/article-create";
    }

    // article 생성 처리
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

    // article 삭제 폼
    @GetMapping("/{articleId}/delete")
    public String deleteArticleForm(
            @PathVariable("articleId") Long articleId,
            Model model
    ) {
        model.addAttribute("articleId", articleId);
        return "/article/article-delete";
    }

    // article 삭제 처리
    @PostMapping("/{articleId}/delete")
    public String deleteArticle(
            @PathVariable("articleId") Long articleId,
            @RequestParam("password") String password,
            Model model
    ) throws IOException {
        // 삭제 성공시
        if (articleService.deleteArticle(articleId, password)) {
            return "redirect:/boards/";
        // 실패시
        } else {
            model.addAttribute("articleId", articleId);
            return "article/delete-failed";
        }
    }

    // article 수정 폼
    @GetMapping("/{articleId}/update")
    public String updateArticleForm(
            @PathVariable("articleId") Long articleId,
            Model model
    ) {
        Article article = articleService.readArticle(articleId);
        model.addAttribute("article", article);
        return "article/article-update";
    }

    // article 수정
    @PostMapping("/{articleId}/update")
    public String updateArticle(
            @PathVariable("articleId") Long articleId,
            @RequestParam("password") String password,
            @RequestParam("writer") String writer,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            Model model
    ) {
        // 수정 성공 시
        if (articleService.updateArticle(articleId, password, writer, title, content)) {
            return String.format("redirect:/article/%d", articleId);
        // 수정 실패 시
        } else {
            model.addAttribute("articleId", articleId);
            return "article/update-failed";
        }
    }

    // 댓글 추가 요청 처리
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

    // 댓글 삭제 요청 폼
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

    // 댓글 삭제 요청 처리
    @PostMapping("/{articleId}/comment/{commentId}/delete")
    public String deleteComment(
            @PathVariable("articleId") Long articleId,
            @PathVariable("commentId") Long commentId,
            @RequestParam("password") String password,
            Model model
    ) {
        // 삭제 성공 시
        if (commentService.deleteComment(commentId, password)) {
            return "redirect:/article/" + articleId;
        // 삭제 실패 시
        } else {
            model.addAttribute("articleId", articleId);
            return "comment/delete-failed";
        }
    }

    // 해시태그를 이용한 검색
    @GetMapping("/hashtag")
    public String searchArticleByHashtag(
            @RequestParam("hashtag") String hashtag,
            Model model
    ) {
        model.addAttribute("hashtag", hashtag);
        model.addAttribute("articles", articleService.searchArticleByHashtag(hashtag));
        return "article/article-search-hashtag";
    }

    // 게시글에 이미지 추가 처리
    @PostMapping("/{articleId}/image/add")
    public String addImageToPost(
            @PathVariable("articleId") Long articleId,
            @RequestParam("upload_image") MultipartFile image,
            @RequestParam("password") String password,
            Model model
    ) throws IOException {
        // 이미지 추가 성공 시
        if (articleService.saveArticleWithImage(articleId, image, password)) {
            return "redirect:/article/" + articleId;
        // 실패 시
        } else {
            model.addAttribute("articleId", articleId);
            return "article/image-add-failed";
        }
    }

    // 게시글에 이미지 삭제 폼
    @GetMapping("/{articleId}/image/delete")
    public String deleteImageView(
            @PathVariable("articleId") Long articleId,
            @RequestParam("image") String image,
            Model model
    ) {
        model.addAttribute("articleId", articleId);
        model.addAttribute("image", image);
        return "article/image-delete";
    }

    // 이미지 삭제 처리
    @PostMapping("/{articleId}/image/delete")
    public String deleteImage(
            @PathVariable("articleId") Long articleId,
            @RequestParam("password") String password,
            @RequestParam("image") String image,
            Model model
    ) throws IOException {
        // 이미지 삭제 성공 시
        if (articleService.deleteImageFromArticle(articleId, password, image)) {
            return "redirect:/article/" + articleId;
        // 이미지 삭제 실패 시
        } else {
            model.addAttribute("articleId", articleId);
            return "article/image-delete-failed";
        }
    }
}
