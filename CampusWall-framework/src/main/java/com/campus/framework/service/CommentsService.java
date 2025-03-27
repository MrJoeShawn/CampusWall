package com.campus.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.framework.dao.entity.Comments;
import com.campus.framework.dao.repository.ResponseResult;

/**
 * 评论表(Comments)表服务接口
 *
 * @author makejava
 * @since 2024-10-05 17:41:14
 */
public interface CommentsService extends IService<Comments> {

    ResponseResult commentList(Long dynamicId, Integer pageNum, Integer pageSize);

    ResponseResult getChildren(Long commentId, Integer pageNum, Integer pageSize);

    ResponseResult addComment(Comments comments);

    ResponseResult getAllComments(Integer pageNum, Integer pageSize);
}

