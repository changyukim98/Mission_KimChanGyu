package com.example.annonymousboard.controller;

import com.example.annonymousboard.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/")
    public String boardList(Model model) {
        model.addAttribute("boards", boardService.readBoardAll());
        System.out.println(boardService.readBoardAll());
        return "board-list";
    }
    @GetMapping("/entire")
    @ResponseBody
    public String entireView() {
        return "전체 게시판입니다.";
    }

    @GetMapping("/{boardId}")
    public String boardView(
            @PathVariable("boardId") Long boardId
    ) {
        return null;
    }
}
