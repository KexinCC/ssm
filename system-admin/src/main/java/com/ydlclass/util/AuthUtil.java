package com.ydlclass.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ydlclass.constant.Constants;
import com.ydlclass.core.RedisTemplate;
import com.ydlclass.entity.YdlLoginUser;

import javax.servlet.http.HttpServletRequest;

public class AuthUtil {


    /**
     * 获取当前的登录对象
     * @return
     */
    public static YdlLoginUser getLoginUser(RedisTemplate redisTemplate, HttpServletRequest request) {
        String tokenKey = Constants.TOKEN_PREFIX + request.getHeader("username") + ":" + request.getHeader(Constants.HEAD_AUTHORIZATION);
        return redisTemplate.getObject(tokenKey, new TypeReference<>() {
        });
    }

}
