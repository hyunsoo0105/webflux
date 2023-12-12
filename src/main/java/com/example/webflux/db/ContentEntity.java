package com.example.webflux.db;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Table(name = "content")
@Data
@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "content")
@jakarta.persistence.EntityListeners(AuditingEntityListener.class)
public class ContentEntity {
    @Id 
    @jakarta.persistence.Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    Long idx;

    String title;

    @jakarta.persistence.Column(columnDefinition = "TEXT")
    String content;

    @CreatedDate
    LocalDateTime createDt;

    @LastModifiedDate
    LocalDateTime editDt;
}
