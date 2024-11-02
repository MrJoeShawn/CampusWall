package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.framework.constants.SystemConstants;
import com.campus.framework.dao.entity.Dynamic;
import com.campus.framework.dao.entity.DynamicTags;
import com.campus.framework.dao.entity.Tags;
import com.campus.framework.dao.entity.Users;
import com.campus.framework.dao.enums.AppHttpCodeEnum;
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
     * 根据分类id获取动态列表
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

        // 分页查询
        Page<Dynamic> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);

        // 将查询结果转换为VO列表
        List<DynamicListVO> dynamicsListVo = BeanCopyUtils.copyBeanList(page.getRecords(), DynamicListVO.class);

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

            // 调试输出
            System.out.println("User ID: " + userId + ", Full Name: " + dynamicListVO.getFullName());
        });

        // 封装分页结果
        PageVo pageVo = new PageVo(dynamicsListVo, page.getTotal());

        return ResponseResult.okResult(pageVo);
    }



    /**
     * 根据动态 ID 获取动态详细信息
     *
     * @param id 动态的唯一标识符
     * @return 返回包含动态信息的响应结果
     */
    @Override
    public ResponseResult getDynamicById(Long id) {
        // 根据动态 ID 获取动态对象
        Dynamic dynamic = getById(id);

        // 根据动态对象中的用户 ID 获取用户信息
        Users user = usersService.getById(dynamic.getUserId());

        // 将 Dynamic 对象转换为 DynamicVO 对象
        DynamicVO dynamicVO = BeanCopyUtils.copyBean(dynamic, DynamicVO.class);

        // 设置动态的完整名称
        dynamicVO.setFullName(user.getFullName());

        // 创建 LambdaQueryWrapper 用于查询动态标签
        LambdaQueryWrapper<DynamicTags> queryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件，匹配动态 ID
        queryWrapper.eq(DynamicTags::getDynamicId, dynamic.getDynamicId());

        // 查询与该动态相关的所有标签
        List<DynamicTags> tags = dynamicTagsService.list(queryWrapper);

        // 获取每个标签的详细信息并转换为 Tags 对象列表
        List<Tags> tagList = tags.stream()
                .map(dynamicTags -> tagsService.getById(dynamicTags.getTagId()))
                .collect(Collectors.toList());

        // 将标签列表设置到 DynamicVO 对象中
        dynamicVO.setTagName(tagList);

        // 返回包含动态详细信息的响应结果
        return ResponseResult.okResult(dynamicVO);
    }


    /**
     * 根据动态获取对应动态的用户信息
     * @param dynamicId 动态ID
     * @return 用户信息响应结果
     */
    @Override
    public ResponseResult getUserByDynamicId(Integer dynamicId) {
        // 根据动态ID获取动态信息
        Dynamic dynamic = getById(dynamicId);
        // 根据动态中的用户ID获取用户信息
        Users user = usersService.getById(dynamic.getUserId());
        UserInfoVo userInfoVo = getUserInfoVo(user);
        // 返回用户信息响应结果
        return ResponseResult.okResult(userInfoVo);
    }

    /**
     * 根据用户ID获取用户信息 用户主页展示用户信息
     * @param userId 用户ID
     * @return 用户信息响应结果
     */
    @Override
    public ResponseResult getUserInfoByUserId(Integer userId) {
        Users user = usersService.getById(userId);
        UserInfoVo userInfoVo = getUserInfoVo(user);
        // 返回用户信息响应结果
        return ResponseResult.okResult(userInfoVo);
    }

    /**
     * 创建动态
     * @param dynamicVO 动态对象
     * @return 创建结果响应
     */
   @Override
   public ResponseResult createDynamic(DynamicVO dynamicVO) {
       Integer userId = SecurityUtils.getUserId();
       dynamicVO.setUserId(userId);

       Dynamic dynamic = new Dynamic();
       dynamic.setDynamicSummary(dynamicVO.getDynamicSummary()); // 设置动态摘要
       dynamic.setDynamicCover(dynamicVO.getDynamicCover()); // 设置动态封面
       dynamic.setDynamicContent(dynamicVO.getDynamicContent()); // 设置动态内容
       dynamic.setCleanedPostContent(dynamicVO.getCleanedPostContent()); // 设置已清理的动态内容
       dynamic.setCategoryId(dynamicVO.getCategoryId()); // 设置动态分类ID
       dynamic.setIsAnonymous(dynamicVO.getIsAnonymous()); // 设置是否匿名
       dynamic.setAllowComments(dynamicVO.getAllowComments()); // 设置是否允许评论
       dynamic.setIsDraft(dynamicVO.getIsDraft());// 设置是否为草稿
       dynamic.setUserId(userId); // 确保设置用户ID

       save(dynamic);

       return ResponseResult.okResult();
   }


    /**
     * 获取用户信息视图对象
     * @param user 用户对象
     * @return 用户信息视图对象
     */
    private UserInfoVo getUserInfoVo(Users user) {
        // 将用户信息转换为用户信息视图对象
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        // 创建查询条件，查询同一用户的动态，并按点赞数降序排序，限制结果数量为3
        LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dynamic::getUserId, user.getId());
        queryWrapper.orderByDesc(Dynamic::getLikeCount);
        queryWrapper.last("LIMIT 3");
        // 执行查询，获取动态列表
        List<Dynamic> dynamics = list(queryWrapper);
        // 将动态列表转换为动态列表视图对象
        List<DynamicListVO> dynamicListVOS = BeanCopyUtils.copyBeanList(dynamics, DynamicListVO.class);
        // 将热门动态列表设置到用户信息视图对象中
        userInfoVo.setHotDynamic(dynamicListVOS);
        return userInfoVo;
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
     * 获取置顶动态 用户点击自己主页
     * @return
     */
    @Override
    public ResponseResult getDynamicTop(Integer userId) {
        LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dynamic::getUserId, userId);
        queryWrapper.eq(Dynamic::getIsTop, SystemConstants.ARTICLE_STATUS_TOP);

        Dynamic dynamic = getOne(queryWrapper);

        if (dynamic == null) {
            return ResponseResult.okResult("没有找到置顶动态", null);
        }

        DynamicVO dynamicVO = BeanCopyUtils.copyBean(dynamic, DynamicVO.class);
        return ResponseResult.okResult(dynamicVO);
    }

    /**
     * 更新置顶动态
     * @param dynamicId
     * @return
     */
    @Override
    public ResponseResult updateTopDynamics(Integer dynamicId) {
          // 从token中获取用户id
          Integer userId = SecurityUtils.getUserId();

          // 检查用户ID是否有效
          if (userId == null) {
              return ResponseResult.errorResult(AppHttpCodeEnum.INVALID_USER_ID);
          }

          // 查询当前要置顶的动态
          Dynamic newDynamic = getById(dynamicId);
          if (newDynamic == null) {
              return ResponseResult.errorResult(AppHttpCodeEnum.DYNAMIC_NOT_FOUND);
          }

          // 查询该用户是否有置顶动态
          QueryWrapper queryWrapper = new QueryWrapper<>();
          queryWrapper.eq("is_top", SystemConstants.ARTICLE_STATUS_TOP);
          queryWrapper.eq("user_id", userId);
          Dynamic dynamic = getOne(queryWrapper);

          // 如果不为空置顶动态则先将原本置顶动态修改为未置顶
          if (dynamic != null) {
              dynamic.setIsTop(SystemConstants.ARTICLE_STATUS_NORMAL);
              updateById(dynamic); // 更新数据库
          }

          // 将新的动态设置为置顶
          newDynamic.setIsTop(SystemConstants.ARTICLE_STATUS_TOP);
          updateById(newDynamic); // 更新数据库


          DynamicVO newDynamicVO = BeanCopyUtils.copyBean(newDynamic, DynamicVO.class);
          // 返回结果
          return ResponseResult.okResult(newDynamicVO);
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
