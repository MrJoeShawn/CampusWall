package com.campus.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.framework.dao.entity.Dynamic;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.dao.vo.DynamicVO;

public interface DynamicService extends IService<Dynamic> {
    ResponseResult getDynamicList(Integer pageNum,Integer pageSize);

    ResponseResult getDynamicListByCategoryId(Integer pageNum, Integer pageSize, Long categoryId);

    ResponseResult getDynamicById(Long id);

    ResponseResult getUserByDynamicId(Integer dynamicId);

    ResponseResult getDynamicListByUserId(Integer pageNum, Integer pageSize, Integer userId);

    ResponseResult getDynamicTop(Integer userId);

    ResponseResult updateTopDynamics(Integer dynamicId);

    ResponseResult getUserInfoByUserId(Integer userId);

    ResponseResult createDynamic(DynamicVO dynamicVO);

    ResponseResult selectByDynamicSummary(String dynamicSummary, Integer pageNum, Integer pageSize);

    ResponseResult updateGetDynamic(Integer dynamicId);

    ResponseResult deleteDynamic(Integer dynamicId);

    ResponseResult updatePrivate(Integer dynamicId);
}
