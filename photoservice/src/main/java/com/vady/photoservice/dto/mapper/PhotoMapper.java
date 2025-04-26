package com.vady.photoservice.dto.mapper;

import com.vady.photoservice.dto.PhotoDto;
import com.vady.photoservice.dto.UserDto;
import com.vady.photoservice.model.Photo;
import com.vady.photoservice.model.Tag;
import org.mapstruct.*;


import java.util.Set;
import java.util.stream.Collectors;

@Mapper(uses = {TagMapper.class}, componentModel = "spring")
public interface PhotoMapper {

//    @Mapping(target = "user", qualifiedByName = "toDtoWithFollowStatus")
    @Mapping(target = "tags", source = "photo.tags", qualifiedByName = "tagsToTagNames")
    @Mapping(target = "userId",source = "user.id")
    @Mapping(target = "nickname", source = "user.nickname")
    @Mapping(target = "userProfileImage", source = "user.profileImage")
    @Mapping(target = "likedByCurrentUser", constant = "false")
    @Mapping(target = "id",source = "photo.id")
    @Mapping(target = "title",source = "photo.title")
    @Mapping(target = "description",source = "photo.description")
    @Mapping(target = "url",source = "photo.url")
    @Mapping(target = "likesCount",source = "photo.likesCount")
    @Mapping(target = "commentsCount",constant = "0")
    @Mapping(target = "createdAt",source = "photo.createdAt")
    PhotoDto toDto(Photo photo, UserDto user);

    @Named("toDtoWithLikeStatus")
    default PhotoDto toDto(Photo photo,UserDto user, String currentUser) {
        PhotoDto dto = toDto(photo,user);
        if (currentUser != null) {
            dto.setLikedByCurrentUser(photo.getLikes().stream()
                    .anyMatch(like -> like.getUserId().equals(currentUser)));
        }
        dto.setLikesCount(photo.getLikesCount());
        return dto;
    }

    @Named("tagsToTagNames")
    default Set<String> tagsToTagNames(Set<Tag> tags) {
        if (tags == null) {
            return null;
        }
        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
    }

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "url", ignore = true)
//    @Mapping(target = "user", ignore = true)
//    @Mapping(target = "likes", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "tags", ignore = true)
//    void updatePhotoFromDto(PhotoDto dto, @MappingTarget Photo photo);
}