package com.hasson.community.util;

public interface CommunityConstant {

    //激活成功
    int ACTIVATION_SUCCESS = 0;
    //重复激活
    int ACTIVATION_REPEAT = 1;
    //激活失败
    int ACTIVATION_FAILURE = 2;

    //默认登录凭证时间 12小时
    int DEFAULT_EXPIRED_SECONDS = 60 * 60 * 12;

    //勾选记住我的，登录凭证时间 7天小时
    int IS_REMEMBER＿ME_EXPIRED_SECONDS = 60 * 60 * 24 * 7;

    //帖子的评论
    int ENTITY_TYPE_POST = 1;
    //评论的评论
    int ENTITY_TYPE_COMMENT = 2;
}
