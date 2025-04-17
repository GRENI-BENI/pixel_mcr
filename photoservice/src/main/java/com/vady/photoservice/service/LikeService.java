package com.vady.photoservice.service;

import com.vady.photoservice.exception.ResourceAlreadyExistsException;
import com.vady.photoservice.exception.ResourceNotFoundException;
import com.vady.photoservice.model.Like;
import com.vady.photoservice.model.Photo;
import com.vady.photoservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    
    private final LikeRepository likeRepository;
    private final PhotoService photoService;
    
    public boolean hasUserLikedPhoto(Long userId, Long photoId) {
        Photo photo = photoService.getPhotoById(photoId);
        return likeRepository.existsByUserIdAndPhoto(userId, photo);
    }
    
    @Transactional
    public Like likePhoto(Long userId, Long photoId) {
        Photo photo = photoService.getPhotoById(photoId);
        
        // Check if the user has already liked the photo
        if (likeRepository.existsByUserIdAndPhoto(userId, photo)) {
            throw new ResourceAlreadyExistsException("You have already liked this photo");
        }
        
        Like like = new Like(userId, photo);
        
        return likeRepository.save(like);
    }
    
    @Transactional
    public void unlikePhoto(Long userId, Long photoId) {
        Photo photo = photoService.getPhotoById(photoId);
        
        Like like = likeRepository.findByUserIdAndPhoto(userId, photo)
                .orElseThrow(() -> new ResourceNotFoundException("Like","",""));
        
        likeRepository.delete(like);
    }


//    public List<Long> getUsersWhoLikedPhoto(Long photoId, Pageable pageable) {
//        Photo photo = photoService.getPhotoById(photoId);
//        List<User> users=likeRepository.findAllByPhoto(photo, pageable).map(Like::getUser).getContent();
//        return users;
//    }
}