package com.example.annonymousboard.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @ManyToOne
    private Board board;
    private String title;
    private String content;
    private String writer;
    private String password;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;
}
