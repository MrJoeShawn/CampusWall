package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.framework.constants.SystemConstants;
import com.campus.framework.dao.entity.Dynamic;
import com.campus.framework.dao.entity.DynamicTags;
import com.campus.framework.dao.entity.Tags;
import com.campus.framework.dao.entity.Users;
import com.campus.framework.dao.mapper.DynamicMapper;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.dao.vo.*;
import com.campus.framework.service.DynamicService;
import com.campus.framework.service.DynamicTagsService;
import com.campus.framework.service.TagsService;
import com.campus.framework.service.UsersService;
import com.campus.framework.untils.BeanCopyUtils;
import com.campus.framework.untils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DynamicServiceImpl extends ServiceImpl<DynamicMapper, Dynamic> implements DynamicService {

    @Autowired
    UsersService usersService;

    @Autowired
    TagsService tagsService;

    @Autowired
    DynamicTagsService dynamicTagsService;

    /**
     * 首页获取动态列表
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return 包含动态列表及用户信息的响应结果
     */
    @Override
    public ResponseResult getDynamicList(Integer pageNum, Integer pageSize) {
        // 创建查询条件，查询未删除且已发布的动态，按创建时间倒序排列
        LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dynamic::getIsDeleted, SystemConstants.ARTICLE_STATUS_DRAFT)
                .eq(Dynamic::getIsDraft, SystemConstants.ARTICLE_STATUS_NORMAL)
                .orderByDesc(Dynamic::getCreatedAt);

        // 分页查询
        Page<Dynamic> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);

        // 将查询结果转换为VO列表
        List<DynamicListVO> dynamicsListVo = page.getRecords().stream()
                .map(dynamic -> BeanCopyUtils.copyBean(dynamic, DynamicListVO.class))  //map对流中的元素进行计算或转换
                .collect(Collectors.toList()); //当前流转换成一个集合

        // 使用 HashSet 来避免用户ID重复
        Set<Integer> userIds = dynamicsListVo.stream()
                .map(DynamicListVO::getUserId)
                .collect(Collectors.toSet());

        // 批量获取用户信息
        List<Users> users = usersService.listByIds(userIds);
        Map<Long, String> userMap = users.stream()
                .collect(Collectors.toMap(user -> Long.valueOf(user.getId()), Users::getFullName));

        // 设置用户全名，若不存在则设置为 "未知用户"
        dynamicsListVo.forEach(dynamicListVO -> {
            Integer userId = dynamicListVO.getUserId();
            dynamicListVO.setFullName(userMap.getOrDefault(Long.valueOf(userId), "未知用户"));
        });

        // 返回封装结果
        return ResponseResult.okResult(dynamicsListVo);
    }



    /**
     * 分类获取动态列表
     * @param pageNum
     * @param pageSize
     * @param categoryId
     * @return
     */
    @Override
    public ResponseResult getDynamicListByCategoryId(Integer pageNum, Integer pageSize, Long categoryId) {
        LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dynamic::getIsDeleted, SystemConstants.ARTICLE_STATUS_DRAFT);
        queryWrapper.eq(Dynamic::getIsDraft, SystemConstants.ARTICLE_STATUS_NORMAL);
        if (categoryId != null) {
            queryWrapper.eq(Dynamic::getCategoryId, categoryId);
        }
        queryWrapper.orderByDesc(Dynamic::getCreatedAt);
        Page<Dynamic> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        List<DynamicListVO> dynamicsListVo = BeanCopyUtils.copyBeanList(page.getRecords(), DynamicListVO.class);

        PageVo pageVo = new PageVo(dynamicsListVo, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 根据id获取动态详情
     * @param id
     * @return
     */
    @Override
    public ResponseResult getDynamicById(Long id) {
        Dynamic dynamic = getById(id);
        Users user = usersService.getById(dynamic.getUserId());
        DynamicVO dynamicVO = BeanCopyUtils.copyBean(dynamic, DynamicVO.class);
        dynamicVO.setFullName(user.getFullName());
        LambdaQueryWrapper<DynamicTags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DynamicTags::getDynamicId, dynamic.getDynamicId());
        List<DynamicTags> tags = dynamicTagsService.list(queryWrapper);
        List<Tags> tagList = tags.stream()
                .map(dynamicTags -> tagsService.getById(dynamicTags.getTagId()))
                .collect(Collectors.toList());
        dynamicVO.setTagName(tagList);
        return ResponseResult.okResult(dynamicVO);
    }

    /**
     * 根据动态获取对应动态的用户信息
     * @param dynamicId
     * @return
     */
    @Override
    public ResponseResult getUserByDynamicId(Integer dynamicId) {
        Dynamic dynamic = getById(dynamicId);
        Users user = usersService.getById(dynamic.getUserId());
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dynamic::getUserId, user.getId());
        queryWrapper.orderByDesc(Dynamic::getLikeCount);
        queryWrapper.last("LIMIT 3");
        List<Dynamic> dynamics = list(queryWrapper);
        List<DynamicListVO> dynamicListVOS = BeanCopyUtils.copyBeanList(dynamics, DynamicListVO.class);
        userInfoVo.setHotDynamic(dynamicListVOS);
        return ResponseResult.okResult(userInfoVo);
    }


    /**
     * 根据用户id获取动态列表  用户主页动态
     * @return
     */
    @Override
    public ResponseResult getDynamicListByUserId(Integer pageNum, Integer pageSize, Integer userId) {
        PageVo pageVo = getPageVo(pageNum, pageSize, userId);
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 封装分页数据
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    private PageVo getPageVo(Integer pageNum, Integer pageSize, Integer userId) {
        LambdaQueryWrapper <Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dynamic::getUserId, userId);
        queryWrapper.eq(Dynamic::getIsDeleted, SystemConstants.ARTICLE_STATUS_DRAFT);
        queryWrapper.orderByDesc(Dynamic::getCreatedAt);
        Page<Dynamic> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        List<UserDynamicListVO> userDynamicListVOS = BeanCopyUtils.copyBeanList(page.getRecords(), UserDynamicListVO.class);
        PageVo pageVo = new PageVo(userDynamicListVOS, page.getTotal());
        return pageVo;
    }


}
