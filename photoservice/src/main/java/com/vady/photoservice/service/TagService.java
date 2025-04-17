package com.vady.photoservice.service;

import com.vady.photoservice.model.Tag;
import com.vady.photoservice.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    
    private final TagRepository tagRepository;
    
    public List<Tag> getPopularTags(int limit) {
        return tagRepository.findTopTagsByPhotoCount(limit);
    }
    
    public List<Tag> searchTags(String query, int limit) {
        return tagRepository.findByNameContainingIgnoreCaseOrderByNameAsc(query, limit);
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }
}