package com.joo.pro.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Arrays;

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
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Orders order;

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
    @Enumerated(EnumType.STRING)
    private PICKUP pickup;

    @Column
    private String price;

    public void setStatus(STATUS status) {
        this.status = status;
    }

    @Getter
    public enum STATUS {
        DEFECT("O"),   // 모두 찾아감
        NONE("X");          // 아직 하나도 안 찾아감

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
    public enum PICKUP {
        YES("O"),
        NO("X");

        private String korean;

        PICKUP(String korean) {
            this.korean = korean;
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
        ALTERATION("수선"),
        OTHER("기타");

        private String koreanName;

        SERVICE_TYPE(String koreanName) {
            this.koreanName = koreanName;
        }

        @JsonCreator
        public static SERVICE_TYPE from(String value) {
            if (value == null || value.trim().isEmpty()) {
                return OTHER; // 값이 없으면 에러 대신 기본값으로 처리 (선택 사항)
            }

            for (SERVICE_TYPE type : SERVICE_TYPE.values()) {
                // 1. 영어 이름 (WASH, OTHER 등) 비교
                if (type.name().equalsIgnoreCase(value)) {
                    return type;
                }
                // 2. 한글 이름 (세탁, 기타 등) 비교
                if (type.getKoreanName().equals(value)) {
                    return type;
                }
            }
            // 여기까지 왔다면 진짜 없는 값임
            System.out.println("입력된 잘못된 값: " + value); // 로그로 범인을 잡습니다.
            return OTHER; // 에러를 던지는 대신 '기타'로 보내주면 서비스가 멈추지 않습니다.
        }
    }
}
