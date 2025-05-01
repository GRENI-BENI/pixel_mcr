package com.vady.iamservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonationRequest {
    @NotNull(message = "Platform ID cannot be null")
    private Long platformId;
    
    @NotBlank(message = "Donation link cannot be empty")
    private String donationLink;
}