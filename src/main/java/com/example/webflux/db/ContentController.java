package com.example.webflux.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Mono<?> insertContentR2dbc(@RequestBody ContentEntity contentEntity, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.debug("written by : {}", customUserDetails.getUsername());
        return contentService.insertContentR2dbc(contentEntity);
    }

    @PostMapping(value = {"/jpa"})
    public Mono<?> insertContentJpa(@RequestBody ContentEntity contentEntity, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.debug("written by : {}", customUserDetails.getUsername());
        return contentService.insetContentJpa(contentEntity);
    }

    @GetMapping("/r2dbc")
    public Mono<?> selectAllContentR2dbc() {
        return contentService.selectAllContentR2dbc().collectList();
    }
    
    @GetMapping("/jpa")
    public Mono<?> selectAllContentJpa() {
        return contentService.selectAllContentJpa().collectList();
    }
}
