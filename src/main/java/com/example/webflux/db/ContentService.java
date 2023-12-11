package com.example.webflux.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.webflux.db.reactive.ContentReactiveRepository;

import reactor.core.publisher.Mono;

@Service 
public class ContentService {
    ContentReactiveRepository contentReactiveRepository;

    
    public ContentService(
        @Autowired ContentReactiveRepository contentReactiveRepository
    ) {
        this.contentReactiveRepository = contentReactiveRepository;
    }

    @Transactional
    public Mono<ContentEntity> insertContentR2dbc(ContentEntity contentEntity) {
        return contentReactiveRepository.save(contentEntity);
    }
}