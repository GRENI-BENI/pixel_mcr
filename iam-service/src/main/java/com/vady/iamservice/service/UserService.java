package com.vady.iamservice.service;

import com.vady.iamservice.dto.UserProfileResponse;
import com.vady.iamservice.exception.UserNotFoundException;
import com.vady.iamservice.mapper.UserMapper;
import com.vady.iamservice.model.User;
import com.vady.iamservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException("User not found with keycloakId: " + keycloakId));
        
        return userMapper.toUserProfileResponse(user);
    }
}