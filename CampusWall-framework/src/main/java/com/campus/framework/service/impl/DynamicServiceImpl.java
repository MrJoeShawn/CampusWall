package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.framework.constants.SystemConstants;
import com.campus.framework.dao.entity.Dynamic;
import com.campus.framework.dao.entity.Users;
import com.campus.framework.dao.mapper.DynamicMapper;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.dao.vo.DynamicListVO;
import com.campus.framework.dao.vo.DynamicVO;
import com.campus.framework.dao.vo.PageVo;
import com.campus.framework.service.DynamicService;
import com.campus.framework.service.UsersService;
import com.campus.framework.untils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DynamicServiceImpl extends ServiceImpl<DynamicMapper, Dynamic> implements DynamicService {

    @Autowired
    UsersService usersService;

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
        return ResponseResult.okResult(dynamicVO);
    }
}
