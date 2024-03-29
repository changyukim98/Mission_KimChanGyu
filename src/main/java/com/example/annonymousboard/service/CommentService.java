package com.example.annonymousboard.service;

import com.example.annonymousboard.entity.Comment;
import com.example.annonymousboard.repo.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    // 댓글 저장
    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    // 댓글 삭제
    public boolean deleteComment(Long commentId, String password) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null || !comment.getPassword().equals(password)) return false;
        commentRepository.deleteById(commentId);
        return true;
    }
}
