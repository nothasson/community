package com.hasson.community.dao;

import com.hasson.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    //offset 开始的id,limit 显示多少个（每个多少个）
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    //一定要@Param（用于给参数取别名）注解，
    //如果只有一个参数，并且在<if>里使用，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);
}
