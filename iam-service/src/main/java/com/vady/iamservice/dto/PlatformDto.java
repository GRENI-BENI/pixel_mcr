package com.vady.iamservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformDto {
    private Long id;
    
    @NotBlank(message = "Platform name cannot be empty")
    private String name;
    
    private String icon;
    
    @NotBlank(message = "Base URL cannot be empty")
    private String baseUrl;
}