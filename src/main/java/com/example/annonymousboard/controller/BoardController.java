package com.example.annonymousboard.controller;

import com.example.annonymousboard.entity.Article;
import com.example.annonymousboard.entity.Board;
import com.example.annonymousboard.service.BoardService;
import com.example.annonymousboard.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {
    private final BoardService boardService;
    private final ArticleService articleService;

    // 게시판 목록
    @GetMapping("/")
    public String boardList(Model model) {
        model.addAttribute("boards", boardService.readBoardAll());
        return "board/board-list";
    }

    // 전체 게시판 View
    @GetMapping("/entire")
    public String entireView(
            Model model
    ) {
        model.addAttribute("articles", articleService.readAllArticleDesc());
        return "board/entire-articles";
    }

    @GetMapping("/entire/{articleId}")
    public String readArticleInEntire(
            @PathVariable("articleId") Long articleId,
            Model model
    ) {
        model.addAttribute("article", articleService.readArticle(articleId));
        model.addAttribute("nextArticle", articleService.getNextArticleInEntire(articleId));
        model.addAttribute("prevArticle", articleService.getPrevArticleInEntire(articleId));
        return "board/entire-article-view";
    }

    // 전체 게시판 검색 결과
    @GetMapping("/entire/search")
    public String entireSearch(
            @RequestParam("search_text") String query,
            @RequestParam("search_opt") String opt,
            Model model
    ) {
        if (opt.equals("제목")) {
            model.addAttribute("articles", articleService.searchAllArticleByTitle(query));
        } else {
            model.addAttribute("articles", articleService.searchAllArticleByContent(query));
        }
        model.addAttribute("search_text", query);
        model.addAttribute("search_opt", opt);
        return "board/entire-search";
    }

    // 특정 게시판 View
    @GetMapping("/{boardId}")
    public String boardView(
            @PathVariable("boardId") Long boardId,
            Model model
    ) {
        Board board = boardService.readBoard(boardId);
        model.addAttribute("board", board);
        return "board/board-articles";
    }

    @GetMapping("/{boardId}/search")
    public String boardSearch(
            @PathVariable("boardId") Long boardId,
            @RequestParam("search_text") String query,
            @RequestParam("search_opt") String opt,
            Model model
    ) {
        if (opt.equals("제목")) {
            model.addAttribute("articles", articleService.searchBoardArticleByTitle(boardId, query));
        } else {
            model.addAttribute("articles", articleService.searchBoardArticleByContent(boardId, query));
        }
        model.addAttribute("board", boardService.readBoard(boardId));
        model.addAttribute("search_text", query);
        model.addAttribute("search_opt", opt);
        return "board/board-search";
    }
}