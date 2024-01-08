package com.example.annonymousboard.entity;

import jakarta.persistence.*;
import lombok.Data;

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
}
