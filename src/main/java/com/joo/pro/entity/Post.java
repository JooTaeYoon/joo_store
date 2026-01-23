package com.joo.pro.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String author;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private int likes = 0;
    private int comments = 0; // 댓글 수 카운트 (성능을 위해 필드로 관리)

    private LocalDateTime createdAt = LocalDateTime.now();

    // 게시글 삭제 시 관련 댓글들도 함께 삭제되도록 설정
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();
}
