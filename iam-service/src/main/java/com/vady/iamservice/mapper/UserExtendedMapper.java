package com.vady.iamservice.mapper;

import com.vady.iamservice.dto.UserExtendedDto;
import com.vady.iamservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserExtendedMapper {

    @Mapping(target = "profileImage", source = "profileImage")
    @Mapping(target = "followingCount", expression = "java(user.getFollowing().size())")
    @Mapping(target = "followersCount", expression = "java(user.getFollowers().size())")
    @Mapping(target = "followedByCurrentUser", ignore = true)
    UserExtendedDto toDto(User user);
    
    @Named("toExtendedDtoWithFollowStatus")
    default UserExtendedDto toDto(User user, User currentUser) {
        UserExtendedDto dto = toDto(user);
        if (currentUser != null) {
            dto.setFollowedByCurrentUser(currentUser.getFollowing().contains(user));
        }
        return dto;
    }
}