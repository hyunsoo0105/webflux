package com.example.webflux;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/test")
public class TestController {
    
    @GetMapping(path = { "/", "" })
    public Mono<String> test() {
        return Mono.just("test");
    }
}
