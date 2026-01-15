package com.joo.pro.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name="clothes_picture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class ClothesPicture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 실제 서버 폴더에 저장된 난수화된 이름 (중복 방지용)
    private String storedFileName;

    // 사용자가 올린 실제 파일명 (복구 및 출력용)
    private String originalFileName;

    // 파일이 저장된 물리적 폴더 경로 (예: C:/uploads/clothes/)
    private String filePos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clothes_id")
    @JsonIgnore
    private Clothes clothes;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public ClothesPicture(String storedFileName, String originalFileName, String filePos) {
        this.storedFileName = storedFileName;
        this.originalFileName = originalFileName;
        this.filePos = filePos;
        this.createdAt = LocalDateTime.now();
    }
}
