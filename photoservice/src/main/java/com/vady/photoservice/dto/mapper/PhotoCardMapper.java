package com.vady.photoservice.dto.mapper;

import com.vady.photoservice.dto.PhotoCardDto;
import com.vady.photoservice.dto.UserDto;
import com.vady.photoservice.model.Photo;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface PhotoCardMapper {

    /**
     * Maps a Photo entity to a PhotoCardDto
     *
     * @param photo The photo entity to map
     * @return The mapped PhotoCardDto
     */
    @Mapping(source = "url", target = "url")
//    @Mapping(source = "user.nickname", target = "nickname")
//    @Mapping(source = "user.profileImage", target = "userProfileImage")
    @Mapping(target = "likedByCurrentUser", ignore = true)
    PhotoCardDto toDto(Photo photo);
    
    /**
     * Maps a Photo entity to a PhotoCardDto with like status
     *
     * @param photo The photo entity to map
     * @param user The current user to check like status
     * @return The mapped PhotoCardDto with like status
     */
    @Mapping(source = "photo.url", target = "url")
    @Mapping(source = "photo.id", target = "id")
    @Mapping(source = "user.nickname", target = "nickname")
    @Mapping(source = "user.profileImage", target = "userProfileImage")
    @Mapping(target = "likedByCurrentUser", expression = "java(isPhotoLikedByUser(photo, currentUserId))")
    PhotoCardDto toDto(Photo photo,UserDto user, String currentUserId);

    /**
     * Maps a list of Photo entities to a list of PhotoCardDtos
     *
     * @param photos The list of photo entities to map
     * @return The list of mapped PhotoCardDtos
     */
    Iterable<PhotoCardDto> toDtoList(Iterable<Photo> photos);
    

    
    /**
     * Helper method to determine if a photo is liked by the current user
     */
    @Named("isPhotoLikedByUser")
    default boolean isPhotoLikedByUser(Photo photo, String userId) {
        if (userId==null || userId.isEmpty()  || photo == null || photo.getLikes() == null) {
            return false;
        }

        return photo.getLikes().stream()
                .anyMatch(like -> like.getUserId().equals(userId));
    }
    

}