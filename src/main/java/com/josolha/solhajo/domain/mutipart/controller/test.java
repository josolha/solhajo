package com.josolha.solhajo.domain.mutipart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class test {

    @PostMapping("/api/upload")
    public ResponseEntity<String> uploadFile() {
        return ResponseEntity.ok("File and text uploaded successfully!");
    }
}
