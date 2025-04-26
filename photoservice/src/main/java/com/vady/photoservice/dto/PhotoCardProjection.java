package com.vady.photoservice.dto;

public interface PhotoCardProjection {
    String getId();
    String getUrl();
    String getUserId();
    long getLikesCount();
    boolean getIsLiked();
}
