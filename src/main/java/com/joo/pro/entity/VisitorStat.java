package com.joo.pro.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
public class VisitorStat {

    @Id
    private LocalDate statDate; // 날짜 자체가 기본키 (PK)

    private int todayCount;
    private long totalCount;

    public void incrementToday() {
        this.todayCount++;
        this.totalCount++;
    }
}
