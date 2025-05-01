package com.vady.iamservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDonationDto {
    private Long id;
    
    @NotNull(message = "Platform ID cannot be null")
    private Long platformId;
    
    private String platformName;
    
    private String platformIcon;
    
    @NotBlank(message = "Donation link cannot be empty")
    private String donationLink;
}