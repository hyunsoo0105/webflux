package com.example.webflux.db.reactive; 

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.example.webflux.db.ContentEntity;

@Repository
public interface ContentReactiveRepository extends ReactiveCrudRepository<ContentEntity,Long> { }