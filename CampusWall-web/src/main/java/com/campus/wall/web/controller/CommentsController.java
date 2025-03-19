package com.campus.wall.web.controller;


import com.campus.framework.dao.entity.Comments;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    /**
     * 获取动态评论列表 根评论
     * @param dynamicId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/list")
    public ResponseResult commentslist(Long dynamicId, Integer pageNum, Integer pageSize) {
        return commentsService.commentList(dynamicId,pageNum,pageSize);
    }

    /**
     * 获取子评论
     * @param commentId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/getChildren")
    public ResponseResult getChildren(Long commentId, Integer pageNum, Integer pageSize) {
        return commentsService.getChildren(commentId,pageNum,pageSize);
    }

    /**
     * 新增评论
     * @param comments
     * @return
     */
    @PostMapping("/addComment")
    public ResponseResult addComment(@RequestBody Comments comments) {
        return commentsService.addComment(comments);
    }
}
