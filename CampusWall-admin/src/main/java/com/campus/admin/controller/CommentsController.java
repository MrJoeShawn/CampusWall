package com.campus.admin.controller;

import com.campus.framework.dao.entity.Comments;
import com.campus.framework.dao.enums.AppHttpCodeEnum;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    /**
     * 获取所有评论列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 评论列表
     */
    @GetMapping("/comment/list")
    public ResponseResult getAllComments(@RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        // 校验分页参数，防止非法访问
        if (pageNum <= 0 || pageSize <= 0) {
            return ResponseResult.errorResult(400, "分页参数无效");
        }
        return commentsService.getAllComments(pageNum, pageSize);
    }


    /**
     * 获取评论的子评论列表
     * @param commentId 评论 ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 子评论列表
     */
    @GetMapping("/comment/children")
    public ResponseResult getChildren(@RequestParam Long commentId,
                                      @RequestParam Integer pageNum,
                                      @RequestParam Integer pageSize) {
        return commentsService.getChildren(commentId, pageNum, pageSize);
    }

    /**
     * 添加评论
     * @param comments 评论对象
     * @return 操作结果
     */
    @PostMapping("/comment/add")
    public ResponseResult addComment(@RequestBody Comments comments) {
        return commentsService.addComment(comments);
    }

    /**
     * 删除评论
     * @param commentId 评论 ID
     * @return 操作结果
     */
    @DeleteMapping("/comment/delete/{commentId}")
    public ResponseResult deleteComment(@PathVariable Long commentId) {
        // 你可以根据需求添加更多的业务逻辑来处理删除操作
        return commentsService.removeById(commentId) ? ResponseResult.okResult() : ResponseResult.errorResult(AppHttpCodeEnum.valueOf("删除失败"));
    }

    /**
     * 更新评论
     * @param commentId 评论 ID
     * @param comments 更新的评论内容
     * @return 操作结果
     */
    @PutMapping("/comment/update/{commentId}")
    public ResponseResult updateComment(@PathVariable Integer commentId, @RequestBody Comments comments) {
        comments.setCommentId(commentId);  // 确保评论ID更新
        boolean isUpdated = commentsService.updateById(comments);
        return isUpdated ? ResponseResult.okResult() : ResponseResult.errorResult(AppHttpCodeEnum.valueOf("更新失败"));
    }
}

