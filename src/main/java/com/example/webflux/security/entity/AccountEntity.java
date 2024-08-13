package com.example.webflux.security.entity;

import java.time.LocalDateTime;

import org.springframework.data.domain.Persistable;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Builder 사용시
 * AllArgsConstructor, NoArgsConstructor를 사용하여 jpa 사용에 조회시 오류 발생 방지
 * jakarta = jpa
 * org.springframework.data = r2dbc
 */

@Data
@Builder
@Accessors(chain=true)
@AllArgsConstructor
@NoArgsConstructor
@jakarta.persistence.Entity
@org.springframework.data.relational.core.mapping.Table(name = "account")
@jakarta.persistence.Table(name = "account")
@jakarta.persistence.EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
public class AccountEntity implements Persistable<String>{
    // @org.springframework.data.annotation.Id
    // @jakarta.persistence.Id
    // @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    // Long idx;

    @org.springframework.data.annotation.Id
    @jakarta.persistence.Id
    @jakarta.persistence.Column(columnDefinition = "", nullable = false, unique = true)
    String email;

    @jakarta.persistence.Column(columnDefinition = "", nullable = false)
    String password;
    
    @jakarta.persistence.Column(nullable=false)
    @Builder.Default
    Boolean isDelete=false;

    @jakarta.persistence.Column(nullable=false)
    @Builder.Default
    Boolean isBan=false;

    @org.springframework.data.annotation.CreatedDate
    LocalDateTime createDt;

    @org.springframework.data.annotation.LastModifiedDate
    LocalDateTime editDt;


    /**
     * R2DBC에서 가공하거나 email 같은 id 사용 시 R2DBC는 id가 있는 경우 update 로직만 수행
     * Persistable 상속하여 insert할지 update할지 선택 가능 (추가로 version이란 방법도 존재함)
     * 해당 값에 Transient을 사용하여 영속성을 제거하여 사용한다 (쉽게 얘기해서 디비에 저장하지 않는다)
     */
    @org.springframework.data.annotation.Transient
    @jakarta.persistence.Transient
    @Builder.Default
    @JsonIgnore
    private boolean isNew = false;

    @Override
    @Nullable
    public String getId() {
        return this.email;
    }

    @JsonIgnore
    @Override
    public boolean isNew() {
        return this.isNew;
    }
}