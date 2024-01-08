package com.example.annonymousboard.controller;

import com.example.annonymousboard.service.BoardService;
import com.example.annonymousboard.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {
    private final BoardService boardService;
    private final ArticleService articleService;

    @GetMapping("/")
    public String boardList(Model model) {
        model.addAttribute("boards", boardService.readBoardAll());
        return "board/board-list";
    }
    @GetMapping("/entire")
    public String entireView(
            Model model
    ) {
        model.addAttribute("articles", articleService.readArticleAll());
        return "board/entire-articles";
    }

    @GetMapping("/{boardId}")
    public String boardView(
            @PathVariable("boardId") Long boardId,
            Model model
    ) {
        model.addAttribute("board", boardService.readBoard(boardId));
        return "board/board-articles";
    }
}
