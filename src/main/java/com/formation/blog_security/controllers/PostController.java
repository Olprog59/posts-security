package com.formation.blog_security.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PostController {

    @GetMapping("/posts")
    public String getPosts() {
        log.info(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        return "Liste des posts";
    }
}
