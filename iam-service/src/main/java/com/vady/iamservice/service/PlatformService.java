package com.vady.iamservice.service;

import com.vady.iamservice.dto.PlatformDto;
import com.vady.iamservice.model.Platform;
import com.vady.iamservice.repository.PlatformRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlatformService {

    private final PlatformRepository platformRepository;

    @Transactional(readOnly = true)
    public List<PlatformDto> getAllPlatforms() {
        return platformRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlatformDto getPlatformById(Long id) {
        Platform platform = platformRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Platform not found with id: " + id));
        return mapToDto(platform);
    }

    @Transactional
    public PlatformDto createPlatform(PlatformDto platformDto) {
        Platform platform = new Platform();
        platform.setName(platformDto.getName());
        platform.setIcon(platformDto.getIcon());
        platform.setBaseUrl(platformDto.getBaseUrl());
        
        Platform savedPlatform = platformRepository.save(platform);
        return mapToDto(savedPlatform);
    }

    @Transactional
    public PlatformDto updatePlatform(Long id, PlatformDto platformDto) {
        Platform platform = platformRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Platform not found with id: " + id));
        
        platform.setName(platformDto.getName());
        platform.setIcon(platformDto.getIcon());
        platform.setBaseUrl(platformDto.getBaseUrl());
        
        Platform updatedPlatform = platformRepository.save(platform);
        return mapToDto(updatedPlatform);
    }

    @Transactional
    public void deletePlatform(Long id) {
        if (!platformRepository.existsById(id)) {
            throw new EntityNotFoundException("Platform not found with id: " + id);
        }
        platformRepository.deleteById(id);
    }

    private PlatformDto mapToDto(Platform platform) {
        return PlatformDto.builder()
                .id(platform.getId())
                .name(platform.getName())
                .icon(platform.getIcon())
                .baseUrl(platform.getBaseUrl())
                .build();
    }
}