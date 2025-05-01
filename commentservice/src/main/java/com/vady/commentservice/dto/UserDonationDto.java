package com.vady.commentservice.dto;

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
    private Long platformId;
    private String platformName;
    private String platformIcon;
    private String donationLink;
}