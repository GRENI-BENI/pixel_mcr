package com.vady.iamservice.mapper;

import com.vady.iamservice.dto.PlatformDto;
import com.vady.iamservice.model.Platform;
import org.springframework.stereotype.Component;

@Component
public class PlatformMapper {

    public PlatformDto toDto(Platform platform) {
        if (platform == null) {
            return null;
        }
        
        return PlatformDto.builder()
                .id(platform.getId())
                .name(platform.getName())
                .icon(platform.getIcon())
                .baseUrl(platform.getBaseUrl())
                .build();
    }

    public Platform toEntity(PlatformDto dto) {
        if (dto == null) {
            return null;
        }
        
        Platform platform = new Platform();
        platform.setId(dto.getId());
        platform.setName(dto.getName());
        platform.setIcon(dto.getIcon());
        platform.setBaseUrl(dto.getBaseUrl());
        
        return platform;
    }
}