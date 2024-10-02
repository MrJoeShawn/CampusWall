package com.campus.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.framework.dao.entity.Dynamic;
import com.campus.framework.dao.repository.ResponseResult;

public interface DynamicService extends IService<Dynamic> {
    ResponseResult getDynamicList();
}
