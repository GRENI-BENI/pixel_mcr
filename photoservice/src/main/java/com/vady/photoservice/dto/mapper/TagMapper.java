package com.vady.photoservice.dto.mapper;

import com.vady.photoservice.dto.TagDto;
import com.vady.photoservice.model.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface TagMapper {

    @Mapping(target = "photosCount", expression = "java(tag.getPhotos().size())")
    TagDto toDto(Tag tag);
}