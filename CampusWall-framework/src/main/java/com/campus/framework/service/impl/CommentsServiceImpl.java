package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.framework.constants.SystemConstants;
import com.campus.framework.dao.entity.Comments;
import com.campus.framework.dao.mapper.CommentsMapper;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.dao.vo.CommentVo;
import com.campus.framework.dao.vo.PageVo;
import com.campus.framework.service.CommentsService;
import com.campus.framework.service.UsersService;
import com.campus.framework.untils.BeanCopyUtils;
import com.campus.framework.untils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论表(Comments)表服务实现类
 *
 * @author makejava
 * @since 2024-10-05 17:41:14
 */
@Service("commentsService")
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements CommentsService {

    @Autowired
    private UsersService userService;

    /**
     * 查询评论列表
     * @param dynamicId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ResponseResult commentList(Long dynamicId, Integer pageNum, Integer pageSize) {
        // 查找对应文章的根评论
        LambdaQueryWrapper<Comments> wrapper = new LambdaQueryWrapper<>();
        // 根评论对应的id为-1
        wrapper.eq(Comments::getDynamicId, dynamicId);
        wrapper.eq(Comments::getParentCommentId, SystemConstants.COMMENT_ISROOT);
        // 分页查询
        Page<Comments> page = new Page<>(pageNum, pageSize);
        page(page, wrapper);
        List<CommentVo> commentVoList = toCommentVoList(page.getRecords());
        return ResponseResult.okResult(new PageVo(commentVoList, page.getTotal()));
    }

    /**
     * 查询子评论及孙评论
     * @param commentId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ResponseResult getChildren(Long commentId, Integer pageNum, Integer pageSize) {
        // 查询直接子评论
        LambdaQueryWrapper<Comments> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comments::getParentCommentId, commentId);
        wrapper.orderByAsc(Comments::getCreatedAt);

        Page<Comments> page = new Page<>(pageNum, pageSize);
        page(page, wrapper);

        // 获取直接子评论的 ID
        List<Integer> childCommentIds = page.getRecords().stream()
                .map(Comments::getCommentId)
                .collect(Collectors.toList());

        // 查询孙评论
        if (!childCommentIds.isEmpty()) {
            LambdaQueryWrapper<Comments> childWrapper = new LambdaQueryWrapper<>();
            childWrapper.in(Comments::getParentCommentId, childCommentIds);
            childWrapper.orderByAsc(Comments::getCreatedAt);

            // 执行孙评论查询
            Page<Comments> childPage = new Page<>(pageNum, pageSize);
            page(childPage, childWrapper);

            // 合并子评论和孙评论
            List<Comments> allComments = new ArrayList<>(page.getRecords());
            allComments.addAll(childPage.getRecords());

            List<CommentVo> comments = toCommentVoList(allComments);
            return ResponseResult.okResult(new PageVo(comments, page.getTotal() + childPage.getTotal()));
        }

        // 仅返回子评论
        List<CommentVo> comments = toCommentVoList(page.getRecords());
        return ResponseResult.okResult(new PageVo(comments, page.getTotal()));
    }


    /**
     * 添加评论
     * @param comments
     * @return
     */
    @Override
    public ResponseResult addComment(Comments comments) {
        comments.setUserId(SecurityUtils.getUserId());
        save(comments);

        // 增加回复数
        if (comments.getParentCommentId() != null && comments.getParentCommentId() != SystemConstants.COMMENT_ISROOT) {
            Comments parentComment = getById(comments.getParentCommentId());
            if (parentComment != null) {
                parentComment.setReplyCount(parentComment.getReplyCount() + 1);
                updateById(parentComment); // 更新父评论
            }
        }

        return ResponseResult.okResult();
    }


    private List<CommentVo> toCommentVoList(List<Comments> comments) {
        List<CommentVo> commentVoList = BeanCopyUtils.copyBeanList(comments, CommentVo.class);
        for (CommentVo commmentVo : commentVoList) {
            commmentVo.setUsername(userService.getById(commmentVo.getUserId()).getFullName());
            if(commmentVo.getParentCommentId() != SystemConstants.COMMENT_ISROOT){
                commmentVo.setRepliedUsername(userService.getById(commmentVo.getRepliedUserId()).getFullName());
            }
        }
                return commentVoList;
    }
}
