package com.vady.commentservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }


    public List<Comment> getCommentsByPhoto(Long photoId) {
        return commentRepository.findAllByPhotoId(photoId);
    }
}
