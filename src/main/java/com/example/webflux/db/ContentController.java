package com.example.webflux.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.webflux.exception.CustomResponseEntity;
import com.example.webflux.security.CustomUserDetails;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/content")
@Slf4j
public class ContentController {
    ContentService contentService;

    ContentController(
        @Autowired ContentService contentService
    ) {
        this.contentService = contentService;
    }

    @PostMapping(value = {"/", "/r2dbc"})
    public Mono<ResponseEntity<?>> insertContentR2dbc(@RequestBody ContentEntity contentEntity, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.debug("written by : {}", customUserDetails.getUsername());
        return contentService.insertContentR2dbc(contentEntity)
            .map(data -> CustomResponseEntity.success(data))
        ;
    }

    @PostMapping(value = {"/jpa"})
    public Mono<ResponseEntity<?>> insertContentJpa(@RequestBody ContentEntity contentEntity, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.debug("written by : {}", customUserDetails.getUsername());
        return contentService.insetContentJpa(contentEntity)
            .map(data -> CustomResponseEntity.success(data))
        ;
    }

    @GetMapping("/r2dbc")
    public Mono<ResponseEntity<?>> selectAllContentR2dbc() {
        return contentService.selectAllContentR2dbc().collectList()
            .map(data -> CustomResponseEntity.success(data))
        ;
    }
    
    @GetMapping("/jpa")
    public Mono<ResponseEntity<?>> selectAllContentJpa() {
        return contentService.selectAllContentJpa().collectList()
            .map(data -> CustomResponseEntity.success(data))
        ;
    }
}
