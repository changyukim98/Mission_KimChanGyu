package com.example.annonymousboard.service;

import com.example.annonymousboard.entity.Board;
import com.example.annonymousboard.repo.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public List<Board> readBoardAll() {
        return boardRepository.findAll();
    }
}
