package com.campus.framework.dao.entity;
import java.util.Date;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 用户收藏表(Favorites)表实体类
 *
 * @author makejava
 * @since 2024-11-03 14:02:58
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("favorites")
public class Favorites  {
    //收藏ID
    @TableId
    private Long favoriteId;
    //用户ID    
    private Long userId;
    //动态ID    
    private Long dynamicId;
    //收藏时间    
    private Date createdAt;
}
