package com.ydlclass.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ydlclass.constant.Constants;
import com.ydlclass.core.RedisTemplate;
import com.ydlclass.entity.YdlLoginUser;
import com.ydlclass.entity.YdlUser;
import com.ydlclass.service.YdlUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
