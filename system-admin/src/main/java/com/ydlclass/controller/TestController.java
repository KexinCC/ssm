package com.ydlclass.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ydlclass.core.RedisTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class TestController {
    @Resource
    private RedisTemplate redisTemplate;

    @GetMapping("test")
    public String test() {
        redisTemplate.setObject("map", List.of("张三", "李四", "王五"), -1);
        List<String> list = redisTemplate.getObject("map", new TypeReference<List>() {
        });
        log.info(list.toString());
        return "hello ssm-pro";
    }
}
