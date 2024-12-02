package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.framework.dao.entity.Tags;
import com.campus.framework.dao.mapper.DynamicTagsMapper;
import com.campus.framework.dao.mapper.TagsMapper;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.service.DynamicTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.campus.framework.dao.entity.DynamicTags;

import java.util.List;

/**
 * 动态标签关联表(DynamicTags)表服务实现类
 *
 * @author makejava
 * @since 2024-10-13 18:03:36
 */
@Service("dynamicTagsService")
public class DynamicTagsServiceImpl extends ServiceImpl<DynamicTagsMapper, DynamicTags> implements DynamicTagsService {

    @Autowired
    private TagsMapper TagsMapper;

    @Autowired
    private DynamicTagsMapper dynamicTagsMapper;

    /**
     * 获取标签列表
     * @return
     */
    @Override
    public ResponseResult getTagsList() {
        List<Tags> tags = TagsMapper.selectList(null);
        return ResponseResult.okResult(tags);
    }

       /**
    * 根据动态id删除标签
    * @param dynamicId 动态id
    * @throws IllegalArgumentException 如果 dynamicId 为 null 或者不是正整数
    */
   @Override
   public void removeByDynamicId(Integer dynamicId) {
       if (dynamicId == null) {
           throw new IllegalArgumentException("DynamicId不存在");
       }
       try {
           int rowsDeleted = dynamicTagsMapper.deleteByDynamicId(dynamicId);
           if (rowsDeleted > 0) {
               System.out.println("带 id 的动态标签：" + dynamicId + " 删除成功");
           } else {
               System.out.println("找不到带有 id 的动态标签：" + dynamicId);
           }
       } catch (Exception e) {
           System.out.println("删除 id 的动态标签时出错：" + dynamicId);
           throw new RuntimeException("删除动态标签失败", e);
       }
   }


}
