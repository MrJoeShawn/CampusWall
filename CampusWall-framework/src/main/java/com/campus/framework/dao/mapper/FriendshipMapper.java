package com.campus.framework.dao.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.framework.dao.entity.Friendship;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface FriendshipMapper extends BaseMapper<Friendship> {
    @Select("SELECT * FROM friendship WHERE (user_id = #{userId} AND friend_id = #{friendId}) " +
            "OR (user_id = #{friendId} AND friend_id = #{userId}) LIMIT 1")
    Friendship selectFriendshipStatus(@Param("userId") Integer userId, @Param("friendId") Integer friendId);
}

