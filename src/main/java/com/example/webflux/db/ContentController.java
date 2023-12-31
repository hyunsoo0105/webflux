package com.example.webflux.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/content")
public class ContentController {
    ContentService contentService;

    ContentController(
        @Autowired ContentService contentService
    ) {
        this.contentService = contentService;
    }

    @PostMapping(value = {"/", "/r2dbc"})
    public Mono<?> insertContentR2dbc(@RequestBody ContentEntity contentEntity) {
        return contentService.insertContentR2dbc(contentEntity);
    }

    @PostMapping(value = {"/jpa"})
    public Mono<?> insertContentJpa(@RequestBody ContentEntity contentEntity) {
        return contentService.insetContentJpa(contentEntity);
    }
}
