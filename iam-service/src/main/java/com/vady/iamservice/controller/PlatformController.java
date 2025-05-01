package com.vady.iamservice.controller;

import com.vady.iamservice.dto.PlatformDto;
import com.vady.iamservice.service.PlatformService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/platforms")
public class PlatformController {

    private final PlatformService platformService;

    @GetMapping
    public ResponseEntity<List<PlatformDto>> getAllPlatforms() {
        return ResponseEntity.ok(platformService.getAllPlatforms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlatformDto> getPlatformById(@PathVariable Long id) {
        return ResponseEntity.ok(platformService.getPlatformById(id));
    }


}