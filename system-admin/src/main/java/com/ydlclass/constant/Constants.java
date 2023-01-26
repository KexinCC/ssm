package com.ydlclass.constant;

public class Constants {

    // 统一token 前缀
    public static final String TOKEN_PREFIX = "token:";
    public static final String HEAD_AUTHORIZATION = "Authorization";

    // token 续命时间 给 token 续命
    public static final int TOKEN_TIME = 30 * 60;

    // 角色的前缀
    public static final String ROLE_PREFIX = "roles:";
    // 权限的前缀
    public static final String PERM_PRIFIX = "perms:";
    public static final String REPEAT_SUBMIT_KEY = "repeat:";
}
