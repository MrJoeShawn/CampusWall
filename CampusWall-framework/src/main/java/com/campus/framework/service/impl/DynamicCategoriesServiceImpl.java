package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.framework.constants.SystemConstants;
import com.campus.framework.dao.entity.Dynamic;
import com.campus.framework.dao.entity.DynamicTags;
import com.campus.framework.dao.entity.Users;
import com.campus.framework.dao.mapper.DynamicMapper;
import com.campus.framework.dao.mapper.DynamicTagsMapper;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.dao.vo.DynamicCategoriesVo;
import com.campus.framework.dao.vo.DynamicListVO;
import com.campus.framework.dao.vo.DynamicVO;
import com.campus.framework.dao.vo.PageVo;
import com.campus.framework.service.DynamicCategoriesService;
import com.campus.framework.service.DynamicService;
import com.campus.framework.service.UsersService;
import com.campus.framework.untils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.campus.framework.dao.entity.DynamicCategories;
import com.campus.framework.dao.mapper.DynamicCategoriesMapper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 动态分类表(DynamicCategories)表服务实现类
 *
 * @author makejava
 * @since 2024-10-03 14:30:22
 */
@Service("dynamicCategoriesService")
public class DynamicCategoriesServiceImpl extends ServiceImpl<DynamicCategoriesMapper, DynamicCategories> implements DynamicCategoriesService {

    @Autowired
    private DynamicService dynamicService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private DynamicMapper dynamicMapper;

    @Autowired
    private DynamicCategoriesMapper dynamicCategoriesMapper;

    /**
     * 查询分类列表
     * @return
     */
    @Override
    public ResponseResult getCategoryList() {
        //查询动态列表 状态为以发布的动态
        LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dynamic::getIsDraft, SystemConstants.ARTICLE_STATUS_NOTDRAFT);
        queryWrapper.eq(Dynamic::getIsDeleted, SystemConstants.ARTICLE_STATUS_NOTDELETED);
        queryWrapper.eq(Dynamic::getIsPrivate,SystemConstants.DYNAMIC_STATUS_PUBLIC);
        List<Dynamic> DynamicList = dynamicService.list(queryWrapper);
        //获取动态的分类id,去重
        Set<Long> categoryid = DynamicList.stream()
                .map(dynamic -> Long.valueOf(dynamic.getCategoryId()))
                .collect(Collectors.toSet());
        //查询分类表
        List<DynamicCategories> dynamicCategories = listByIds(categoryid);
        dynamicCategories = dynamicCategories.stream().filter(category -> category.getStatus() == SystemConstants.CLASSIFICATION_NORMAL)
                .collect(Collectors.toList());
        //封装vo
        List<DynamicCategoriesVo> dynamicCategoriesVos = BeanCopyUtils.copyBeanList(dynamicCategories, DynamicCategoriesVo.class);

        return ResponseResult.okResult(dynamicCategoriesVos);
    }

    /**
     * 查询所有分类列表
     * @return
     */
    @Override
    public ResponseResult getAllCategoryList() {
        List<DynamicCategories> dynamicCategories = dynamicCategoriesMapper.selectList(null);
        return ResponseResult.okResult(dynamicCategories);
    }

    /**
     * 首页展示校园公告
     * @return
     */
    @Override
    public ResponseResult getCampusAnnouncementsList() {
        // 创建查询条件对象
        LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        // 生成查询条件：未删除的文章
        queryWrapper.eq(Dynamic::getIsDeleted, SystemConstants.ARTICLE_STATUS_NOTDELETED);
        // 生成查询条件：非草稿状态的文章
        queryWrapper.eq(Dynamic::getIsDraft, SystemConstants.ARTICLE_STATUS_NOTDRAFT);
        // 生成查询条件：公开的文章
        queryWrapper.eq(Dynamic::getIsPrivate, SystemConstants.DYNAMIC_STATUS_PUBLIC);
        // 生成查询条件：特定分类下的文章（假设分类ID为21代表校园公告）
        queryWrapper.eq(Dynamic::getCategoryId,21);
        // 限制查询结果数量为6条
        queryWrapper.last("LIMIT 6");
        // 而且为置顶动态
        queryWrapper.eq(Dynamic::getIsTop, SystemConstants.ARTICLE_STATUS_TOP);
        // 执行查询，获取动态列表
        List<Dynamic> dynamics = dynamicService.list(queryWrapper);
        // 使用 BeanCopyUtils 进行对象转换
        List<DynamicListVO> dynamicsListVo = BeanCopyUtils.copyBeanList(dynamics, DynamicListVO.class);
        // 返回查询结果
        return ResponseResult.okResult(dynamicsListVo);
    }

    /**
     * 管理员查询校园公告
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ResponseResult getAdminCampusAnnouncementsList(Integer pageNum, Integer pageSize) {
        // 创建分页对象
        Page<Dynamic> page = new Page<>(pageNum, pageSize);

        // 创建查询条件对象
        LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dynamic::getIsDeleted, SystemConstants.ARTICLE_STATUS_NOTDELETED);
        queryWrapper.eq(Dynamic::getIsDraft, SystemConstants.ARTICLE_STATUS_NOTDRAFT);
        queryWrapper.eq(Dynamic::getIsPrivate, SystemConstants.DYNAMIC_STATUS_PUBLIC);
        queryWrapper.eq(Dynamic::getCategoryId, 21);

        // 执行分页查询
        Page<Dynamic> dynamicPage = dynamicService.page(page, queryWrapper);
        List<Dynamic> dynamics = dynamicPage.getRecords();

        // 获取所有 userId
        Set<Integer> userIds = dynamics.stream()
                .map(Dynamic::getUserId)
                .collect(Collectors.toSet());

        // 查询所有用户信息
        Map<Integer, Users> usersMap = usersService.listByIds(userIds).stream()
                .collect(Collectors.toMap(Users::getId, user -> user));

        // 组装数据
        List<DynamicListVO> dynamicsListVo = dynamics.stream().map(dynamic -> {
            DynamicListVO vo = BeanCopyUtils.copyBean(dynamic, DynamicListVO.class);
            Users user = usersMap.get(dynamic.getUserId());
            if (user != null) {
                vo.setFullName(user.getFullName());
                vo.setProfilePictureUrl(user.getProfilePictureUrl());
            }
            return vo;
        }).collect(Collectors.toList());

        // 创建 PageVo 对象
        PageVo pageVo = new PageVo(dynamicsListVo, dynamicPage.getTotal());

        // 返回分页数据
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 更新公告置顶状态
     * @param dynamicId
     * @param isTop
     * @return
     */
    @Override
    public ResponseResult updateTopDynamics(Integer dynamicId, Integer isTop) {
        // 检查公告是否存在
        Dynamic dynamic = dynamicMapper.selectById(dynamicId);
        if (dynamic == null) {
            return ResponseResult.errorResult(404, "公告不存在");
        }

        // 如果是设置置顶操作（isTop == 1），需要检查当前置顶公告的数量
        if (isTop == 1) {
            // 查询当前已置顶的公告数量
            int topCount = dynamicMapper.selectCount(new LambdaQueryWrapper<Dynamic>()
                    .eq(Dynamic::getIsTop, 1));

            if (topCount >= 6) {
                return ResponseResult.errorResult(400, "最多只能置顶 6 条公告");
            }
        }

        // 更新置顶状态
        dynamic.setIsTop(isTop);
        int updatedRows = dynamicMapper.updateById(dynamic);

        // 判断更新是否成功
        if (updatedRows > 0) {
            return ResponseResult.okResult("公告状态更新成功");
        } else {
            return ResponseResult.errorResult(500, "公告状态更新失败");
        }
    }
}
