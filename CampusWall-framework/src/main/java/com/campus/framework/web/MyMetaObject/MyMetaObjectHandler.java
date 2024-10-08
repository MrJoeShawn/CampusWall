package com.campus.framework.web.MyMetaObject;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.campus.framework.untils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        Long userId = null;
        try {
            userId = Long.valueOf(SecurityUtils.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
            userId = -1L;//表示是自己创建
        }
        // 创建时间
        this.setFieldValByName("createdAt", new Date(), metaObject);
        // 创建人
        this.setFieldValByName("createdBy", userId, metaObject);
        // 修改时间
        this.setFieldValByName("updatedAt", new Date(), metaObject);
        // 修改人
        this.setFieldValByName("updatedBy", userId, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updatedAt", new Date(), metaObject);
        this.setFieldValByName(" ", SecurityUtils.getUserId(), metaObject);
    }
}