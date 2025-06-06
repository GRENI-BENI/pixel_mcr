package com.vady.photoservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vady.photoservice.dto.*;
import com.vady.photoservice.dto.mapper.PhotoCardMapper;
import com.vady.photoservice.dto.mapper.PhotoMapper;
import com.vady.photoservice.feign.CommentsFeignClient;
import com.vady.photoservice.feign.UserFeignClient;
import com.vady.photoservice.model.Photo;
import com.vady.photoservice.service.PhotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/photos")
@RequiredArgsConstructor
@Slf4j
public class PhotoController {

    private final PhotoService photoService;
    private final PhotoMapper photoMapper;
    private final PhotoCardMapper photoCardMapper;
    private final CommentsFeignClient commentsFeignClient;
    private final UserFeignClient userFeignClient;

    @GetMapping("/test")
    public ResponseEntity<?> testPhotoComments() {
        return commentsFeignClient.getCommentsByPhoto(1L);
    }

    @GetMapping("/user/{nickname}")
    public ResponseEntity<Page<PhotoCardDto>> getPhotosByNickname(@PathVariable String nickname, @PageableDefault(size = 20) Pageable pageable,@RequestHeader(value ="X-User-ID",required = false) String id) {
        KeycloakIdResponse u=userFeignClient.getUserKeycloakByNickname(nickname).getBody();
        UserDto user = userFeignClient.getUserByNickname(nickname).getBody();
        Page<Photo> photos = photoService.getPhotosByUserKeycloakId(u.keycloakId(), pageable);

        return ResponseEntity.ok(photos.map(x->photoCardMapper.toDto(x,user,id)));
    }

    @PostMapping(value = "/user/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfileImage(
            @RequestPart("image") MultipartFile imageFile,
            @RequestHeader("X-User-ID") String currentUserId) {
        return photoService.updateProfilePicture(currentUserId, imageFile);
    }

//    @GetMapping
//    public ResponseEntity<Page<PhotoDto>> getAllPhotos(
//            @PageableDefault(size = 20) Pageable pageable) {
//        Page<Photo> photos = photoService.getAllPhotos(pageable);
//        return ResponseEntity.ok(photos.map(photo -> photoMapper.toDto(photo, currentUser)));
//    }
//
//    @GetMapping("/user/{nickname}")
//    public ResponseEntity<Page<PhotoCardDto>> getPhotosByNickname(
//            @PathVariable String nickname,
//            @PageableDefault(size = 20) Pageable pageable) {
//        User u=userService.getUserByNickname(nickname);
//        Page<Photo> photos = photoService.getPhotosByUser(u, pageable);
//        return ResponseEntity.ok(photos.map(photo -> photoCardMapper.toDto(photo, currentUser)));
//    }
//
//
//
@GetMapping("/tags")
public ResponseEntity<Page<PhotoCardDto>> getPhotosByMultipleTags(
        @RequestParam Set<String> tags,
        @PageableDefault(size = 20) Pageable pageable,@RequestHeader(required = false,name = "X-User-ID") String userId) {
    Page<PhotoCardDto> photoCards = photoService.getPhotosByTag(tags, userId, pageable);
    return ResponseEntity.ok(photoCards);
}
//
//    @GetMapping("/search")
//    public ResponseEntity<Page<PhotoDto>> searchPhotos(
//            @RequestParam String query,
//            @PageableDefault(size = 20) Pageable pageable) {
//        Page<Photo> photos = photoService.searchPhotos(query, pageable);
//        return ResponseEntity.ok(photos.map(photo -> photoMapper.toDto(photo, currentUser)));
//    }
//
//
//
    @GetMapping("/trending")
public ResponseEntity<Page<PhotoCardDto>> getTrendingPhotos(@PageableDefault(size = 20) Pageable pageable,@RequestHeader(required = false,name = "X-User-ID") String id) {
    Page<PhotoCardDto> photoCards = photoService.getTrendingPhotoCards(id, pageable);
    return ResponseEntity.ok(photoCards);
}

@GetMapping("/ids/user/{keycloakId}")
public ResponseEntity<List<Long>> getPhotoIdsByUserKeycloakId(@PathVariable String keycloakId) {
    return ResponseEntity.ok(photoService.getAllPhotoIdsByUserKeycloakId(keycloakId));
}

public record TotalCommentsCountResponse(Long totalCommentsCount) {}
@GetMapping("/comments/count/user")
public ResponseEntity<TotalCommentsCountResponse> getTotalCommentsCountForUser(@RequestHeader(required = true,name = "X-User-ID") String keycloakId) {
    return ResponseEntity.ok(new TotalCommentsCountResponse(photoService.getTotalCommentsCountForUser(keycloakId)));
}

    @GetMapping("/{id}")
    public ResponseEntity<PhotoDto> getPhotoById(
            @PathVariable Long id,@RequestHeader(required = false,name = "X-User-ID") String currentUserId) {
        Photo photo = photoService.getById(id);
        UserDto user=userFeignClient.getCurrentUserByKeycloak(photo.getUserId()).getBody();
        return ResponseEntity.ok(photoMapper.toDto(photo,user,currentUserId));
    }

    public record CreatePhotoRequest(
            MultipartFile image,
            String title,
            String description,
            List<String> tags
    ) {}



//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<PhotoDto> createPhoto(
//            @RequestPart("image") MultipartFile image,
//            @RequestPart("title") String title,
//            @RequestPart("description") String description,
//            @RequestPart("tags") String tagsJson,
//            @RequestHeader(name = "X-User-ID") String currentUser) throws JsonProcessingException {
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        List<String> tags = objectMapper.readValue(tagsJson, new TypeReference<List<String>>() {});
//
//        CreatePhotoRequest request = new CreatePhotoRequest(image, title, description, tags);
//        UserDto user=userFeignClient.getCurrentUserByKeycloak(currentUser).getBody();
//        Photo photo = photoService.createPhoto(currentUser, request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(photoMapper.toDto(photo, user));
//    }
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<PhotoDto> createPhoto(
        @RequestPart("image") MultipartFile image,
        @RequestPart("title") String title,
        @RequestPart("description") String description,
        @RequestPart("tags") String tagsJson,
        @RequestHeader(name = "X-User-ID") String currentUser) throws JsonProcessingException {

    log.info("Starting photo creation process for user: {}", currentUser);
    log.info("Received image: name={}, size={}, contentType={}", 
             image.getOriginalFilename(), 
             image.getSize(), 
             image.getContentType());
    log.info("Title: {}", title);
    log.info("Description: {}", description);
    log.info("Tags JSON: {}", tagsJson);
    
    try {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> tags = objectMapper.readValue(tagsJson, new TypeReference<>() {});
        log.info("Parsed tags: {}", tags);

        CreatePhotoRequest request = new CreatePhotoRequest(image, title, description, tags);
        log.info("Created request object: {}", request);
        
        log.info("Fetching user information from IAM service");
        ResponseEntity<UserDto> userResponse = userFeignClient.getCurrentUserByKeycloak(currentUser);
        if (userResponse == null || userResponse.getBody() == null) {
            log.error("Failed to retrieve user information from IAM service");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
        UserDto user = userResponse.getBody();
        log.info("Retrieved user: id={}, nickname={}", user.getId(), user.getNickname());
        
        log.info("Calling photoService.createPhoto");
        try {
            Photo photo = photoService.createPhoto(currentUser, request);
            log.info("Photo created successfully: id={}", photo.getId());
            PhotoDto photoDto = photoMapper.toDto(photo, user);
            log.info("Photo mapped to DTO: {}", photoDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(photoDto);
        } catch (Exception e) {
            log.error("Error in photoService.createPhoto", e);
            throw e;
        }
    } catch (JsonProcessingException e) {
        log.error("Error parsing tags JSON: {}", tagsJson, e);
        throw e;
    } catch (Exception e) {
        log.error("Unexpected error in createPhoto", e);
        throw e;
    }
}
    
    @GetMapping("/count/user/{keycloakId}")
public ResponseEntity<Long> getPhotoCountByUserKeycloakId(@PathVariable String keycloakId) {
    long count = photoService.getPhotoCountByUserKeycloakId(keycloakId);
    return ResponseEntity.ok(count);
}

    @PutMapping("/{id}")
    public ResponseEntity<PhotoDto> updatePhoto(
            @PathVariable Long id,
            @RequestBody @Valid PhotoDto photoDto,
            @RequestHeader(required = false,name = "X-User-ID") String currentUser) {
        Photo photo = photoService.updatePhoto(id, currentUser, photoDto);
        UserDto userDto=userFeignClient.getCurrentUserByKeycloak(currentUser).getBody();
        return ResponseEntity.ok(photoMapper.toDto(photo, userDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePhoto(
            @PathVariable Long id,
            @RequestHeader(required = false,name = "X-User-ID") String currentUser) {
        photoService.deletePhoto(id, currentUser);
        return ResponseEntity.ok("{\"message\": \"Photo deleted successfully\" }");
    }
}