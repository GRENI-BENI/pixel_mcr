package com.vady.iamservice.mapper;

import com.vady.iamservice.dto.UserDto;
import com.vady.iamservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "profileImage", source = "profileImage")
    UserDto toDto(User user);
    
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