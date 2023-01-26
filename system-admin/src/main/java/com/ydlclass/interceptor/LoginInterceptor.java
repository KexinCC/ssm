package com.ydlclass.interceptor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ydlclass.configuration.CustomObjectMapper;
import com.ydlclass.constant.Constants;
import com.ydlclass.core.RedisTemplate;
import com.ydlclass.entity.YdlLoginUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CustomObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断有没有首部信息的 Authorization
        // 拿到首部信息的 Authorization 的信息
        String token = request.getHeader(Constants.HEAD_AUTHORIZATION);

        ResponseEntity<String> responseEntity = ResponseEntity.status(401).body("Bad Credentials");

            // 没有携带 token 的请求直接驳回
        if (token == null) {
            response.setStatus(401);
            response.getWriter().write(objectMapper.writeValueAsString((responseEntity)));
            return false;
        }

        /**
         * 如果 request 不携带 username 参数, 那么就通过 token 从 redis 里查找出tokenKey
         */
//        Set<String> keys = redisTemplate.keys(Constants.TOKEN_PREFIX + "*" + token);
//
//        // token 对应的 tokenKey 不存在
//        if (keys == null || keys.size() == 0) {
//            response.setStatus(401);
//            response.getWriter().write(objectMapper.writeValueAsString(responseEntity));
//        }
//        String tokenKey = (String) keys.toArray()[0];


        // 3.使用 token 去redis里查看有没有对应的 loginUser
        YdlLoginUser ydlLoginUser = redisTemplate.getObject(Constants.TOKEN_PREFIX + request.getHeader("username") + ":" + token, new TypeReference<YdlLoginUser>() {
        });
        if (ydlLoginUser == null) {
            response.setStatus(401);
            response.getWriter().write(objectMapper.writeValueAsString((responseEntity)));
            return false;
        }

        redisTemplate.expire(Constants.TOKEN_PREFIX+ request.getHeader("username") + ":" + token, Constants.TOKEN_TIME);

        return true;
    }
}
