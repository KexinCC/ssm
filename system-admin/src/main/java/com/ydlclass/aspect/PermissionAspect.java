package com.ydlclass.aspect;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ydlclass.annotation.HasPerms;
import com.ydlclass.annotation.HasRole;
import com.ydlclass.constant.Constants;
import com.ydlclass.core.RedisTemplate;
import com.ydlclass.exception.ShouldHasPermException;
import com.ydlclass.exception.ShouldHasRoleException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@Aspect
public class PermissionAspect {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private HttpServletRequest request;

    @Before("@annotation(hasRole)")
    public void roleBefore(JoinPoint joinPoint, HasRole hasRole) {
        // 获得当前方法所需要的 角色
        String[] needRoles = hasRole.value();
        // 获得拥有的角色
        String token = request.getHeader(Constants.HEAD_AUTHORIZATION);
        List<String> hasRoles = redisTemplate.getObject(Constants.ROLE_PREFIX + token, new TypeReference<>() {
        });

        // 拥有的角色里 包含所需的角色 就放行
        boolean flag = false;
        for (String needRole : needRoles) {
            if (hasRoles.contains(needRole)) {
                flag = true;
            }
        }
        if (!flag) throw new ShouldHasRoleException("您没有该接口所需要的角色");
    }

    @Before("@annotation(hasPerms)")
    public void permsBefore(JoinPoint joinPoint, HasPerms hasPerms) {
        // 获得当前方法所需要的 角色
        String[] needPerms = hasPerms.value();
        // 获得拥有的角色
        String token = request.getHeader(Constants.HEAD_AUTHORIZATION);
        List<String> hasPerm = redisTemplate.getObject(Constants.PERM_PRIFIX + token, new TypeReference<>() {
        });

        // 拥有的角色里 包含所需的角色 就放行
        boolean flag = false;
        for (String needPerm : needPerms) {
            if (hasPerm.contains(needPerm)) {
                flag = true;
            }
        }
        if (!flag) throw new ShouldHasPermException("您没有该接口所需要的角色");

    }

}
