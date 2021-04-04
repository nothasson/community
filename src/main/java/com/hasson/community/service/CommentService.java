package com.hasson.community.service;

import com.hasson.community.dao.CommentMapper;
import com.hasson.community.entity.Comment;
import com.hasson.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int findCommentsCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }


}
