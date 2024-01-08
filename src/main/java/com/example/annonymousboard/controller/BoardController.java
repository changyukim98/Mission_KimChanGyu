package com.example.annonymousboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/boards")
public class BoardController {
    @GetMapping("/{boardId}")
    @ResponseBody
    public String boardView(
            @PathVariable("boardId") Long boardId
    ) {
        return "test";
    }
}
