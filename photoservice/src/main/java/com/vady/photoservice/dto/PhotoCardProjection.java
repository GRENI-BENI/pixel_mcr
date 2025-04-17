package com.vady.photoservice.dto;

public interface PhotoCardProjection {
    String getId();
    String getUrl();
    String getNickname();
    String getUserProfileImage();
    long getLikesCount();
    boolean getIsLiked();
}
