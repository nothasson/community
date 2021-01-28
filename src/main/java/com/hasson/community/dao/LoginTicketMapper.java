package com.hasson.community.dao;

import com.hasson.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {

    @Insert({"insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"})
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({"select id user_id,ticket,status,expired from login_ticket ",
            "where ticket = #{ticket}"})
    LoginTicket selectByTicket(String ticket);

    @Update({"update login_ticket ",
            "set status = #{status}",
            "where ticket = #{ticket}"})
    int updateStatus(String ticket, int status);


//    如果要在不写xml的写if判断，就需要在前后加上script标签，这实际上和在xml里写是一样的
//    @Update({"<script>",
//            "update login_ticket ",
//            "set status = #{status}",
//            "where ticket = #{ticket}",
//            "<if test = \"ticket!=null\">",
//            "and 1=1",
//            "</if>",
//            "</script>"})
//    int updateStatusIf(String ticket, int status);
}
