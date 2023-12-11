package com.example.webflux.db;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Table(name = "content")
@Data
public class ContentEntity {
    @Id 
    Long idx;

    String title;

    String content;

    @CreatedDate
    LocalDateTime createDt;

    @LastModifiedDate
    LocalDateTime editDt;
}
