package com.ydlclass.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ydlclass.entity.YdlLoginUser;
import com.ydlclass.entity.YdlUser;
import com.ydlclass.service.YdlUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class IndexController {

    @Resource
    private YdlUserService userService;

    @PostMapping("login")
    public ResponseEntity<YdlLoginUser> login(
            @RequestBody @Validated YdlUser ydlUser, BindingResult bindingResult) {
        // 处理不合法的数据
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        allErrors.forEach(e -> log.error("登录时用户校验失败: {}", e.toString()));
        if (allErrors.size() > 0) {
            return ResponseEntity.status(500).build();
        }
        // 执行登录逻辑
        YdlLoginUser ydlLoginUser = null;
        try {
            ydlLoginUser = userService.login(ydlUser.getUserName(), ydlUser.getPassword());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok().body(ydlLoginUser);
    }

    @GetMapping("logout")
    public ResponseEntity<String> logout() {

        // 执行登录逻辑
        YdlLoginUser ydlLoginUser = null;
        try {
            userService.logout();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok().body("登出成功");
    }
}
