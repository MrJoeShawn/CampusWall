package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.framework.constants.SystemConstants;
import com.campus.framework.dao.entity.*;
import com.campus.framework.dao.enums.AppHttpCodeEnum;
import com.campus.framework.dao.mapper.DynamicMapper;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.dao.vo.*;
import com.campus.framework.service.*;
import com.campus.framework.untils.BeanCopyUtils;
import com.campus.framework.untils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

    @Autowired
    @Lazy
    LikesService likesService;

    @Autowired
    @Lazy
    FavoritesService favoritesService;

    @Autowired
    FriendshipService friendshipService;

    /**
     * 首页获取动态列表
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return 包含动态列表及用户信息的响应结果
     */
    public ResponseResult getDynamicList(Integer pageNum, Integer pageSize) {
        // 创建查询条件，查询未删除且已发布的动态，按创建时间倒序排列
        LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dynamic::getIsDeleted, SystemConstants.ARTICLE_STATUS_NOTDELETED)
                .eq(Dynamic::getIsDraft, SystemConstants.ARTICLE_STATUS_NOTDRAFT)
                .eq(Dynamic::getIsPrivate, SystemConstants.DYNAMIC_STATUS_PUBLIC)
                .orderByDesc(Dynamic::getCreatedAt);

        // 分页查询
        Page<Dynamic> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);

        // 将查询结果转换为VO列表
        List<DynamicListVO> dynamicsListVo = page.getRecords().stream()
                .map(dynamic -> BeanCopyUtils.copyBean(dynamic, DynamicListVO.class))
                .collect(Collectors.toList());

        // 使用 HashSet 来避免用户ID重复
        Set<Integer> userIds = dynamicsListVo.stream()
                .map(DynamicListVO::getUserId)
                .collect(Collectors.toSet());

        // 如果 userIds 为空，直接返回结果
        if (userIds.isEmpty()) {
            PageVo pageVo = new PageVo(dynamicsListVo, page.getTotal());
            return ResponseResult.okResult(pageVo);
        }

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
        PageVo pageVo = new PageVo(dynamicsListVo, page.getTotal());
        return ResponseResult.okResult(pageVo);
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
        queryWrapper.eq(Dynamic::getIsDeleted, SystemConstants.ARTICLE_STATUS_NOTDELETED);
        queryWrapper.eq(Dynamic::getIsDraft, SystemConstants.ARTICLE_STATUS_NOTDRAFT);
        queryWrapper.eq(Dynamic::getIsPrivate, SystemConstants.DYNAMIC_STATUS_PUBLIC);
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
        });

        // 封装分页结果
        PageVo pageVo = new PageVo(dynamicsListVo, page.getTotal());

        return ResponseResult.okResult(pageVo);
    }

    /**
     * 根据动态 ID 获取动态详细信息
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

        // 查询该动态对应的标签信息
        // 查询与该动态相关的所有标签
        List<DynamicTags> tags = dynamicTagsService.list(queryWrapper);
        // 获取每个标签的详细信息并转换为 Tags 对象列表
        List<Tags> tagList = tags.stream()
                .map(dynamicTags -> tagsService.getById(dynamicTags.getTagId()))
                .collect(Collectors.toList());
        // 将标签列表设置到 DynamicVO 对象中
        dynamicVO.setTagName(tagList);

        Integer userId = SecurityUtils.getUserId();
        // 查询该用户是否喜欢该动态
        LambdaQueryWrapper<Likes> likesQueryWrapper = new LambdaQueryWrapper<>();
        likesQueryWrapper.eq(Likes::getUserId, userId);
        likesQueryWrapper.eq(Likes::getDynamicId, dynamic.getDynamicId());
        // 执行查询，判断用户是否已点赞该动态
        Likes like = likesService.getOne(likesQueryWrapper); // 使用 getOne 查询单条记录
        if (like != null) {
            dynamicVO.setIsLike(SystemConstants.LIKE_STATUS_YES); // 用户已点赞
        } else {
            dynamicVO.setIsLike(SystemConstants.LIKE_STATUS_NO); // 用户未点赞
        }

        //查询用户是否收藏了该动态
        LambdaQueryWrapper<Favorites> favoritesQueryWrapper = new LambdaQueryWrapper<>();
        favoritesQueryWrapper.eq(Favorites::getUserId, userId);
        favoritesQueryWrapper.eq(Favorites::getDynamicId, dynamic.getDynamicId());
        Favorites favorites = favoritesService.getOne(favoritesQueryWrapper);
        if (favorites != null) {
            dynamicVO.setIsFavorite(SystemConstants.COLLECT_STATUS_YES);
        } else {
            dynamicVO.setIsFavorite(SystemConstants.COLLECT_STATUS_NO);
        }
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
        // 获取动态信息
        Dynamic dynamic = getById(dynamicId);

        // 获取动态中的用户ID
        Integer userId = dynamic.getUserId();

        // 获取用户信息
        Users user = usersService.getById(userId);
        UserInfoVo userInfoVo = getUserInfoVo(user);

        // 查询当前用户与该用户是否为好友
        Integer currentUserId = SecurityUtils.getUserId();
        boolean isFriend = friendshipService.isFriend(currentUserId, userId);  // 使用你已经实现的 isFriend 方法

        // 设置好友状态
        userInfoVo.setFriend(isFriend);

        // 返回用户信息和好友状态
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
        // 判断是否有 dynamicId，如果有，执行更新操作；如果没有，执行创建操作
        if (dynamicVO.getDynamicId() != null) {
            // 如果有 dynamicId，调用更新方法
            return updateDynamic(dynamicVO);
        } else {
            // 如果没有 dynamicId，执行创建操作
            return createNewDynamic(dynamicVO);
        }
    }

    /**
     * 创建新动态
     * @param dynamicVO 动态对象
     * @return 创建结果响应
     */
    private ResponseResult createNewDynamic(DynamicVO dynamicVO) {
        Integer userId = SecurityUtils.getUserId();
        dynamicVO.setUserId(userId);

        // 创建动态对象并保存
        Dynamic dynamic = new Dynamic();
        dynamic.setDynamicSummary(dynamicVO.getDynamicSummary());
        dynamic.setDynamicCover(dynamicVO.getDynamicCover());
        dynamic.setDynamicContent(dynamicVO.getDynamicContent());
        dynamic.setCleanedPostContent(dynamicVO.getCleanedPostContent());
        dynamic.setCategoryId(dynamicVO.getCategoryId());
        dynamic.setIsAnonymous(dynamicVO.getIsAnonymous());
        dynamic.setAllowComments(dynamicVO.getAllowComments());
        dynamic.setIsDraft(dynamicVO.getIsDraft());
        dynamic.setCreatedBy(Long.valueOf(dynamicVO.getUserId()));
        dynamic.setUserId(userId);

        // 保存新动态
        save(dynamic);
        Integer dynamicId = dynamic.getDynamicId(); // 获取新创建的动态ID

        // 获取前端传来的标签名称列表
        List<String> tagNames = dynamicVO.getTagName().stream()
                .map(Tags::getTagName)
                .collect(Collectors.toList());

        // 批量查询现有标签
        List<Tags> existingTags = tagsService.findTagsByNames(tagNames);

        // 将现有标签名称与标签对象映射到一个 Map 以便快速查找
        Map<String, Tags> tagMap = existingTags.stream()
                .collect(Collectors.toMap(Tags::getTagName, tag -> tag));

        // 处理标签
        for (String tagName : tagNames) {
            Tags existingTag = tagMap.get(tagName); // 从 Map 中获取标签

            if (existingTag == null) {
                // 标签不存在，创建新标签
                existingTag = new Tags();
                existingTag.setTagName(tagName);
                tagsService.save(existingTag); // 保存新标签
            }

            // 添加动态与标签的关系
            DynamicTags dynamicTag = new DynamicTags();
            dynamicTag.setDynamicId(Long.valueOf(dynamicId));
            dynamicTag.setTagId(existingTag.getTagId());
            dynamicTagsService.save(dynamicTag); // 保存关系
        }

        return ResponseResult.okResult();
    }

    /**
     * 更新已有动态
     * @param dynamicVO 动态对象
     * @return 更新结果响应
     */
    private ResponseResult updateDynamic(DynamicVO dynamicVO) {
        Integer userId = SecurityUtils.getUserId();
        dynamicVO.setUserId(userId);

        // 查找现有动态
        Dynamic existingDynamic = getById(dynamicVO.getDynamicId());
        if (existingDynamic == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DYNAMIC_NOT_FOUND, "动态不存在");
        }

        // 处理标签
        // 删除原有的标签关系
        dynamicTagsService.removeByDynamicId(dynamicVO.getDynamicId());

        // 更新动态信息
        existingDynamic.setDynamicSummary(dynamicVO.getDynamicSummary());
        existingDynamic.setDynamicCover(dynamicVO.getDynamicCover());
        existingDynamic.setDynamicContent(dynamicVO.getDynamicContent());
        existingDynamic.setCleanedPostContent(dynamicVO.getCleanedPostContent());
        existingDynamic.setCategoryId(dynamicVO.getCategoryId());
        existingDynamic.setIsAnonymous(dynamicVO.getIsAnonymous());
        existingDynamic.setAllowComments(dynamicVO.getAllowComments());
        existingDynamic.setIsDraft(dynamicVO.getIsDraft());
        existingDynamic.setUpdatedBy(Long.valueOf(dynamicVO.getUserId()));

        // 更新数据库中的动态信息
        updateById(existingDynamic);

        // 获取前端传来的标签名称列表
        List<String> tagNames = dynamicVO.getTagName().stream()
                .map(Tags::getTagName)
                .collect(Collectors.toList());

        // 批量查询现有标签
        List<Tags> existingTags = tagsService.findTagsByNames(tagNames);

        // 将现有标签名称与标签对象映射到一个 Map 以便快速查找
        Map<String, Tags> tagMap = existingTags.stream()
                .collect(Collectors.toMap(Tags::getTagName, tag -> tag));

        // 重新添加标签关系
        for (String tagName : tagNames) {
            Tags existingTag = tagMap.get(tagName); // 从 Map 中获取标签

            if (existingTag == null) {
                // 标签不存在，创建新标签
                existingTag = new Tags();
                existingTag.setTagName(tagName);
                tagsService.save(existingTag); // 保存新标签
            }

            // 添加动态与标签的关系
            DynamicTags dynamicTag = new DynamicTags();
            dynamicTag.setDynamicId(Long.valueOf(dynamicVO.getDynamicId()));
            dynamicTag.setTagId(existingTag.getTagId());
            dynamicTagsService.save(dynamicTag); // 保存关系
        }

        return ResponseResult.okResult();
    }


    /**
     * 根据摘要搜索动态
     * @param dynamicSummary
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ResponseResult selectByDynamicSummary(String dynamicSummary, Integer pageNum, Integer pageSize) {
        // 输入验证
        if (dynamicSummary == null || dynamicSummary.trim().isEmpty()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.CONTENT_NOT_EMPTY);
        }

        try {
            // 查询动态
            LambdaQueryWrapper<Dynamic> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(Dynamic::getDynamicSummary, dynamicSummary)
                    .eq(Dynamic::getIsDeleted,SystemConstants.ARTICLE_STATUS_NOTDELETED)
                    .eq(Dynamic::getIsDraft, SystemConstants.ARTICLE_STATUS_NOTDRAFT)
                    .eq(Dynamic::getIsPrivate, SystemConstants.ARTICLE_STATUS_NORMAL)
                    .orderByDesc(Dynamic::getCreatedAt);

            // 分页查询
            Page<Dynamic> page = new Page<>(pageNum, pageSize);
            page(page, wrapper);

            // 获取查询结果
            List<Dynamic> dynamicList = page.getRecords();
            if (dynamicList.isEmpty()) {
                return ResponseResult.okResult("暂无无相关动态");  // 提示前端无数据
            }

            // 将查询结果转换为VO列表
            List<DynamicListVO> dynamicsListVo = dynamicList.stream()
                    .map(dynamic -> BeanCopyUtils.copyBean(dynamic, DynamicListVO.class))
                    .collect(Collectors.toList());

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

            // 封装分页结果
            PageVo pageVo = new PageVo(dynamicsListVo, page.getTotal());
            return ResponseResult.okResult(pageVo);

        } catch (Exception e) {
            return ResponseResult.errorResult(AppHttpCodeEnum.QUERY_DYNAMIC_ERROR);
        }
    }

    /**
     * 获取要修改动态的详情
     * @return
     */
    @Override
    public ResponseResult updateGetDynamic(Integer dynamicId) {
        // 根据动态 ID 获取动态对象
        Dynamic dynamic = getById(dynamicId);

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
        return ResponseResult.okResult(dynamicVO);
    }

    /**
     * 删除动态
     * @param dynamicId
     * @return
     */
    @Override
    public ResponseResult deleteDynamic(Integer dynamicId) {
        // 先检查 dynamicId 是否存在
        Dynamic dynamic = getById(dynamicId);
        if (dynamic == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DYNAMIC_NOT_FOUND);
        }

        if (dynamic.getIsDeleted() == 1) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DYNAMIC_DELETED);
        }

        // 设置为已删除
        dynamic.setIsDeleted(1);

        // 更新操作
        boolean updated = updateById(dynamic);

        // 删除成功，返回成功的响应
        return ResponseResult.okResult();
    }

    /**
     * 更新动态为仅自己可见
     * @param dynamicId
     * @return
     */
    @Override
    public ResponseResult updatePrivate(Integer dynamicId) {
        // 检查动态是否存在
        Dynamic dynamic = getById(dynamicId);
        if (dynamic == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DYNAMIC_NOT_FOUND);
        }

        // 判断动态状态是否为仅自己可见
        if (dynamic.getIsPrivate() == SystemConstants.DYNAMIC_STATUS_PRIVATE) {
            // 当前状态为仅自己可见，则设置为公开可见
            dynamic.setIsPrivate(SystemConstants.DYNAMIC_STATUS_PUBLIC);
            boolean updated = updateById(dynamic);  // 更新数据库
            if (updated) {
                return ResponseResult.okResult("动态已设置为公开可见");
            } else {
                return ResponseResult.errorResult(AppHttpCodeEnum.DYNAMIC_UPDATE_FAILED, "更新动态状态失败");
            }
        } else {
            // 当前状态为公开可见，则设置为仅自己可见
            dynamic.setIsPrivate(SystemConstants.DYNAMIC_STATUS_PRIVATE);
            boolean updated = updateById(dynamic);  // 更新数据库
            if (updated) {
                return ResponseResult.okResult("动态已设置为仅自己可见");
            } else {
                return ResponseResult.errorResult(AppHttpCodeEnum.DYNAMIC_UPDATE_FAILED, "更新动态状态失败");
            }
        }
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
        // 创建一个Lambda查询条件构建器
        LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件：动态属于指定用户
        queryWrapper.eq(Dynamic::getUserId, userId);
        // 添加查询条件：动态未被删除
        queryWrapper.eq(Dynamic::getIsDeleted, SystemConstants.ARTICLE_STATUS_NOTDELETED);
        // 添加查询条件：动态未被标记为草稿
        queryWrapper.eq(Dynamic::getIsDraft, SystemConstants.ARTICLE_STATUS_NOTDRAFT);
        // 添加查询条件：动态应为公开状态
        queryWrapper.eq(Dynamic::getIsPrivate, SystemConstants.DYNAMIC_STATUS_PUBLIC);
        // 添加排序条件：按创建时间降序排列
        queryWrapper.orderByDesc(Dynamic::getCreatedAt);

        // 创建一个分页对象，用于封装分页查询参数
        Page<Dynamic> page = new Page<>(pageNum, pageSize);

        // 执行分页查询
        page(page, queryWrapper);

        // 将查询结果转换为用户动态列表VO对象
        List<UserDynamicListVO> userDynamicListVOS = BeanCopyUtils.copyBeanList(page.getRecords(), UserDynamicListVO.class);

        // 创建并返回一个分页对象，其中包含转换后的动态列表和总记录数
        PageVo pageVo = new PageVo(userDynamicListVOS, page.getTotal());

        return ResponseResult.okResult(pageVo);
    }


    /**
     * 根据用户id获取动态列表 个人主页动态 从token中获取用户id
     * @return
     */
    @Override
    public ResponseResult getDynamicListByUserIdToToken(Integer pageNum, Integer pageSize,Integer userId) {
        // 创建一个Lambda查询条件构建器
        LambdaQueryWrapper <Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件：动态属于指定用户
        queryWrapper.eq(Dynamic::getUserId, userId);
        // 添加查询条件：动态未被删除
        queryWrapper.eq(Dynamic::getIsDeleted, SystemConstants.ARTICLE_STATUS_NOTDELETED);
        // 添加排序条件：按创建时间降序排列
        queryWrapper.orderByDesc(Dynamic::getCreatedAt);
        // 创建一个分页对象，用于封装分页查询参数
        Page<Dynamic> page = new Page<>(pageNum, pageSize);
        // 执行分页查询
        page(page, queryWrapper);
        // 将查询结果转换为用户动态列表VO对象
        List<UserDynamicListVO> userDynamicListVOS = BeanCopyUtils.copyBeanList(page.getRecords(), UserDynamicListVO.class);
        // 创建并返回一个分页对象，其中包含转换后的动态列表和总记录数
        PageVo pageVo = new PageVo(userDynamicListVOS, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 增加浏览量
     * @param dynamicId
     * @return
     */
    @Override
    public ResponseResult addViewCount(Integer dynamicId) {
        Dynamic dynamic = getById(dynamicId);
        if (dynamic != null) {
            dynamic.setViewCount(dynamic.getViewCount() + 1);
            updateById(dynamic);
            return ResponseResult.okResult("浏览量+1");
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.DYNAMIC_NOT_FOUND);
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
     * 此方法用于根据用户ID获取该用户的动态列表，并根据指定的页码和页面大小进行分页
     * 它首先构建了一个查询条件，然后根据这些条件从数据库中获取数据，最后将结果封装成一个分页对象返回
     *
     * @param pageNum  页码，表示请求的页面编号
     * @param pageSize 页面大小，表示每页包含的动态数量
     * @param userId   用户ID，表示要查询的用户的标识
     * @return 返回一个分页对象，其中包含了动态列表和总记录数
     */
    private PageVo getPageVo(Integer pageNum, Integer pageSize, Integer userId) {
        // 创建一个Lambda查询条件构建器
        LambdaQueryWrapper <Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件：动态属于指定用户
        queryWrapper.eq(Dynamic::getUserId, userId);
        // 添加查询条件：动态未被删除
        queryWrapper.eq(Dynamic::getIsDeleted, SystemConstants.ARTICLE_STATUS_NOTDELETED);
        // 添加查询条件：动态未被标记为草稿
        queryWrapper.eq(Dynamic::getIsDraft, SystemConstants.ARTICLE_STATUS_NOTDRAFT);
        // 添加查询条件：动态未被标记为私有
        queryWrapper.eq(Dynamic::getIsPrivate, SystemConstants.DYNAMIC_STATUS_PRIVATE);
        // 添加排序条件：按创建时间降序排列
        queryWrapper.orderByDesc(Dynamic::getCreatedAt);
        // 创建一个分页对象，用于封装分页查询参数
        Page<Dynamic> page = new Page<>(pageNum, pageSize);
        // 执行分页查询
        page(page, queryWrapper);
        // 将查询结果转换为用户动态列表VO对象
        List<UserDynamicListVO> userDynamicListVOS = BeanCopyUtils.copyBeanList(page.getRecords(), UserDynamicListVO.class);
        // 创建并返回一个分页对象，其中包含转换后的动态列表和总记录数
        PageVo pageVo = new PageVo(userDynamicListVOS, page.getTotal());
        return pageVo;
    }
}
