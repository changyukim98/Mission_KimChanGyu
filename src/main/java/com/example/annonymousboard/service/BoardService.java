package com.example.annonymousboard.service;

import com.example.annonymousboard.entity.Board;
import com.example.annonymousboard.repo.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    // 모든 게시판을 가져오기
    public List<Board> readBoardAll() {
        return boardRepository.findAll();
    }

    // 특정 게시판을 가져오기
    public Board readBoard(Long id) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        return optionalBoard.orElse(null);
    }
}
