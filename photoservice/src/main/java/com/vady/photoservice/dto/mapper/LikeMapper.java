package com.vady.photoservice.dto.mapper;

import com.vady.photoservice.dto.LikeDto;
import com.vady.photoservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    @Mapping(target = "photoId", source = "photo.id")
    LikeDto toDto(Like like);
}