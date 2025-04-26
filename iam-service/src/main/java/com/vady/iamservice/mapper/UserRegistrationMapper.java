package com.vady.iamservice.mapper;


import com.vady.iamservice.dto.UserProfileResponse;
import com.vady.iamservice.dto.UserRegistrationRequest;
import com.vady.iamservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserRegistrationMapper {

    /**
     * Maps a UserRegistrationRequest to a User entity
     * Note: keycloakId must be set separately
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "keycloakId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(UserRegistrationRequest request);

    /**
     * Maps a User entity to a UserProfileResponse
     */
    UserProfileResponse toProfileResponse(User user);

    /**
     * Updates a User entity with data from a UserRegistrationRequest
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "keycloakId", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateUserFromRequest(UserRegistrationRequest request, @MappingTarget User user);
}