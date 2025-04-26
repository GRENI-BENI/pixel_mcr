package com.vady.commentservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserExtendedDto extends UserDto{
    String keycloakId;
}
