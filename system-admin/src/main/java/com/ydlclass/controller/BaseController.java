package com.ydlclass.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ydlclass.constant.Constants;
import com.ydlclass.core.RedisTemplate;
import com.ydlclass.entity.YdlLoginUser;
import com.ydlclass.service.YdlUserService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
public class BaseController {

    @Resource
    private YdlUserService userService;

    @Resource
    private HttpServletRequest request;

    @Resource
    private RedisTemplate redisTemplate;

    protected YdlLoginUser getLoginUser() {
        String tokenKey = Constants.TOKEN_PREFIX + request.getHeader("username") + ":" + request.getHeader(Constants.HEAD_AUTHORIZATION);
        return redisTemplate.getObject(tokenKey, new TypeReference<>() {
        });
    }


}
