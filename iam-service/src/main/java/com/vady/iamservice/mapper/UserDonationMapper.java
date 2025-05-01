package com.vady.iamservice.mapper;

import com.vady.iamservice.dto.UserDonationDto;
import com.vady.iamservice.model.UserDonation;
import org.springframework.stereotype.Component;

@Component
public class UserDonationMapper {

    public UserDonationDto toDto(UserDonation donation) {
        if (donation == null) {
            return null;
        }
        
        return UserDonationDto.builder()
                .id(donation.getId())
                .platformId(donation.getPlatform().getId())
                .platformName(donation.getPlatform().getName())
                .platformIcon(donation.getPlatform().getIcon())
                .donationLink(donation.getDonationLink())
                .build();
    }
}