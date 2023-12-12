package com.example.webflux.db.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.webflux.db.ContentEntity;


@Repository
public interface ContentJpaRepository extends JpaRepository<ContentEntity, Long> { }
