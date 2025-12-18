package com.joo.pro.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "clothes")
@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Clothes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String clothesType;

    @Column(name = "createdAt", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId")
    @JsonIgnore
    private Order orderId;

    //    상의인지 하의인지
    @Column(length = 300)
    @Enumerated(EnumType.STRING)
    private CATEGORY category;

    //   손님이 옷을 찾아갔는지, 혹은 몇 벌만 찾아가고 나머지는 아직 안 찾아간 상태인지 추적하기 위한 필드
    @Column
    private String comment;

    //    드라이, 세탁, 다림질 등 어떤 서비스를 요청했는지 기록
    @Column(columnDefinition = "VARCHAR(100)")
    @Enumerated(EnumType.STRING)
    private SERVICE_TYPE serviceType;

    @Column(columnDefinition = "VARCHAR(100)")
    @Enumerated(EnumType.STRING)
    private STATUS status;

    @Column
    private String price;

    public void setStatus(STATUS status) {
        this.status = status;
    }

    @Getter
    public enum STATUS {
        DEFECT("이상 있음"),   // 모두 찾아감
        NONE("이상 없음");          // 아직 하나도 안 찾아감

        private String korean;

        STATUS(String korean) {
            this.korean = korean;
        }

        @JsonCreator
        public static STATUS from(String value) throws IllegalAccessException {
            if (value == null) {
                throw new IllegalAccessException("상태 값이 비었습니다");
            }
            switch (value.toLowerCase()) {
                case "defect":
                case "Defect":
                case "DEFECT":
                    return DEFECT;
                case "none":
                case "NONE":
                case "None":
                    return NONE;
                default:
                    throw new IllegalAccessException("잘못됨");
            }
        }
    }


    @Getter
    public enum CATEGORY {
        TOP("상의"),
        BOTTOM("하의"),
        ETC("기타");

        private String korean;

        CATEGORY(String korean) {
            this.korean = korean;
        }

        @JsonCreator
        public static CATEGORY from(String value) throws IllegalAccessException {
            if (value == null) {
                throw new IllegalAccessException("카테고리 값이 비었습니다");
            }
            switch (value.toLowerCase()) {
                case "top":
                case "TOP":
                case "Top":
                case "상의":
                    return TOP;
                case "bottom":
                case "BOTTOM":
                case "Bottom":
                case "하의":
                    return BOTTOM;
                case "ETC":
                case "Etc":
                case "etc":
                case "기타":
                    return ETC;
                default:
                    throw new IllegalAccessException("잘못됨");
            }
        }
    }

    @Getter
    public enum SERVICE_TYPE {
        WASH("세탁"),
        DRY_CLEAN("드라이"),
        IRON("다림질"),
        ALTERATION("수선");

        private String koreanName;

        SERVICE_TYPE(String koreanName) {
            this.koreanName = koreanName;
        }

        @JsonCreator
        public static SERVICE_TYPE from(String value) throws IllegalAccessException {
            if (value == null) {
                throw new IllegalAccessException("카테고리 값이 비었습니다");
            }
            switch (value.toLowerCase()) {
                case "wash":
                case "WASH":
                case "Wash":
                    return WASH;
                case "dry_clean":
                case "DRY_CLEAN":
                case "Dry_Clean":
                    return DRY_CLEAN;
                case "iron":
                case "IRON":
                case "Iron":
                    return IRON;
                case "alteration":
                case "ALTERATION":
                case "Alteration":
                    return ALTERATION;
                default:
                    throw new IllegalAccessException("잘못됨");
            }
        }
    }
}
