package com.vady.iamservice.service;

import com.vady.iamservice.dto.UserDonationDto;
import com.vady.iamservice.exception.ResourceNotFoundException;
import com.vady.iamservice.model.Platform;
import com.vady.iamservice.model.User;
import com.vady.iamservice.model.UserDonation;
import com.vady.iamservice.repository.PlatformRepository;
import com.vady.iamservice.repository.UserDonationRepository;
import com.vady.iamservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDonationService {

    private final UserDonationRepository userDonationRepository;
    private final UserRepository userRepository;
    private final PlatformRepository platformRepository;

    @Transactional(readOnly = true)
    public List<UserDonationDto> getUserDonations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));
        
        return userDonationRepository.findByUser(user).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDonationDto> getUserDonationsByKeycloakId(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "keycloakId", keycloakId));
        
        return userDonationRepository.findByUser(user).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDonationDto addUserDonation(String keycloakId, UserDonationDto donationDto) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "keycloakId", keycloakId));
        
        Platform platform = platformRepository.findById(donationDto.getPlatformId())
                .orElseThrow(() -> new ResourceNotFoundException("Platform", "id", donationDto.getPlatformId().toString()));
        
        // Validate that the donation link starts with the platform's base URL
        validateDonationLink(donationDto.getDonationLink(), platform);
        
        UserDonation userDonation = new UserDonation();
        userDonation.setUser(user);
        userDonation.setPlatform(platform);
        userDonation.setDonationLink(donationDto.getDonationLink());
        
        UserDonation savedDonation = userDonationRepository.save(userDonation);
        return mapToDto(savedDonation);
    }

    @Transactional
    public UserDonationDto updateUserDonation(String keycloakId, Long donationId, UserDonationDto donationDto) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "keycloakId", keycloakId));
        
        UserDonation userDonation = userDonationRepository.findById(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("UserDonation", "id", donationId.toString()));
        
        // Verify that the donation belongs to the user
        if (!userDonation.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Donation does not belong to the user");
        }
        
        // Update platform if changed
        if (donationDto.getPlatformId() != null && 
            !donationDto.getPlatformId().equals(userDonation.getPlatform().getId())) {
            Platform platform = platformRepository.findById(donationDto.getPlatformId())
                    .orElseThrow(() -> new ResourceNotFoundException("Platform", "id", donationDto.getPlatformId().toString()));
            
            // Validate that the donation link starts with the platform's base URL
            validateDonationLink(donationDto.getDonationLink(), platform);
            
            userDonation.setPlatform(platform);
        } else if (donationDto.getDonationLink() != null) {
            // If only the link is being updated, validate with the existing platform
            validateDonationLink(donationDto.getDonationLink(), userDonation.getPlatform());
        }
        
        // Update donation link if provided
        if (donationDto.getDonationLink() != null) {
            userDonation.setDonationLink(donationDto.getDonationLink());
        }
        
        UserDonation updatedDonation = userDonationRepository.save(userDonation);
        return mapToDto(updatedDonation);
    }

    @Transactional
    public void deleteUserDonation(String keycloakId, Long donationId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "keycloakId", keycloakId));
        
        UserDonation userDonation = userDonationRepository.findById(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("UserDonation", "id", donationId.toString()));
        
        // Verify that the donation belongs to the user
        if (!userDonation.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Donation does not belong to the user");
        }
        
        userDonationRepository.delete(userDonation);
    }

    /**
     * Validates that the donation link starts with the platform's base URL
     * 
     * @param donationLink The donation link to validate
     * @param platform The platform to validate against
     * @throws ResponseStatusException if the link is invalid
     */
    private void validateDonationLink(String donationLink, Platform platform) {
        if (donationLink == null || donationLink.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Donation link cannot be empty");
        }
        
        // Check if the link starts with the platform's base URL
        if (!donationLink.startsWith(platform.getBaseUrl())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Donation link must start with the platform's base URL: " + platform.getBaseUrl()
            );
        }
        
        // Additional validation can be added as needed
        // For example, check for malicious URLs, length limits, etc.
    }

    private UserDonationDto mapToDto(UserDonation userDonation) {
        return UserDonationDto.builder()
                .id(userDonation.getId())
                .platformId(userDonation.getPlatform().getId())
                .platformName(userDonation.getPlatform().getName())
                .platformIcon(userDonation.getPlatform().getIcon())
                .donationLink(userDonation.getDonationLink())
                .build();
    }
}