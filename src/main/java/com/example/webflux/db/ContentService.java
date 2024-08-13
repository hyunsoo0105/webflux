package com.example.webflux.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.webflux.db.jpa.ContentJpaRepository;
import com.example.webflux.db.reactive.ContentReactiveRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service 
public class ContentService {
    ContentReactiveRepository contentReactiveRepository;
    ContentJpaRepository contentJpaRepository;

    
    public ContentService(
        @Autowired ContentReactiveRepository contentReactiveRepository,
        @Autowired ContentJpaRepository contentJpaRepository 
    ) {
        this.contentReactiveRepository = contentReactiveRepository;
        this.contentJpaRepository = contentJpaRepository;
    }

    @Transactional(transactionManager = "r2dbcTransactionManager")
    public Mono<ContentEntity> insertContentR2dbc(ContentEntity contentEntity) {
        return contentReactiveRepository.save(contentEntity);
    }

    @Transactional(transactionManager = "jpaTransactionManger")
    public Mono<ContentEntity> insetContentJpa(ContentEntity contentEntity) {
        return Mono.just(contentJpaRepository.save(contentEntity));
    }

    public Flux<ContentEntity> selectAllContentR2dbc() {
        return contentReactiveRepository.findAll();
    }
    public Flux<ContentEntity> selectAllContentJpa() {
        return Flux.fromIterable(contentJpaRepository.findAll());
    }
}