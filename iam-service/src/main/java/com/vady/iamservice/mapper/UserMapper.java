package com.vady.iamservice.mapper;

import com.vady.iamservice.dto.UserDonationDto;
import com.vady.iamservice.dto.UserDto;
import com.vady.iamservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "profileImage", source = "profileImage")
    default UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        
        List<UserDonationDto> donations = user.getDonations().stream()
                .map(donation -> UserDonationDto.builder()
                        .id(donation.getId())
                        .platformId(donation.getPlatform().getId())
                        .platformName(donation.getPlatform().getName())
                        .platformIcon(donation.getPlatform().getIcon())
                        .donationLink(donation.getDonationLink())
                        .build())
                .collect(Collectors.toList());
        
        return UserDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .about(user.getAbout())
                .profileImage(user.getProfileImage())
                .followersCount(user.getFollowers().size())
                .followingCount(user.getFollowing().size())
                // Set other fields as needed
                .donations(donations)
                .build();
    }
    
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "followingCount", expression = "java(user.getFollowing().size())")
    @Mapping(target = "followersCount", expression = "java(user.getFollowers().size())")
    @Mapping(target = "followedByCurrentUser", ignore = true)
    UserDto toPublicDto(User user);

    @Named("toPublicDtoWithFollowStatus")
    default UserDto toPublicDto(User user, User currentUser) {
        UserDto dto = toPublicDto(user);
        if (currentUser != null) {
            dto.setFollowedByCurrentUser(user.getFollowers().stream()
                    .anyMatch(follower -> follower.getId().equals(currentUser.getId())));
        }
        return dto;
    }

}