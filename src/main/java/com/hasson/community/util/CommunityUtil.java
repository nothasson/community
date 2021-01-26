package com.hasson.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import sun.security.provider.MD5;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CommunityUtil {
    //生成随机字符串
    //把所有的横杠取消掉
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", " ");
    }

    //MD5加密(加盐)
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());

    }
}
