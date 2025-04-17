package com.vady.commentservice;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {


    @Mapping(target = "id", ignore = true)
    Comment dtoToEntity(CommentDto commentDto);

    CommentDto entityToDto(Comment comment);
}
