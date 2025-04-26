package com.vady.commentservice;
import com.vady.commentservice.dto.CommentDto;
import com.vady.commentservice.dto.UserExtendedDto;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CommentMapper {

    public CommentDto entityToDto(Comment comment, Map<String, UserExtendedDto> userInfoMap) {
        CommentDto dto = new CommentDto();
        dto.setContent(comment.getContent());
        dto.setPhotoId(comment.getPhotoId());

        // Get user info from the map
        UserExtendedDto userInfo = userInfoMap.get(comment.getUserKeycloakId());
        if (userInfo != null) {
            dto.setUserId(userInfo.getId());

            CommentDto.UserDto userDto = new CommentDto.UserDto();
            userDto.setNickname(userInfo.getNickname());
            userDto.setProfilePicture(userInfo.getProfileImage());
            dto.setUser(userDto);
        }

        return dto;
    }
}