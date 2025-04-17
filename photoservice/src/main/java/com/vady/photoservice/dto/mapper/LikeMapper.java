package com.vady.photoservice.dto.mapper;

import com.vady.photoservice.dto.LikeDto;
import com.vady.photoservice.model.Like;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LikeMapper {

//    @Mapping(target = "user", qualifiedByName = "toDtoWithFollowStatus")
    LikeDto toDto(Like like);
}