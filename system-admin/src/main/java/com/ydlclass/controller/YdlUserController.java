package com.ydlclass.controller;

import com.ydlclass.annotation.HasPerms;
import com.ydlclass.annotation.HasRole;
import com.ydlclass.annotation.Log;
import com.ydlclass.annotation.Repeat;
import com.ydlclass.core.RedisTemplate;
import com.ydlclass.entity.YdlUser;
import com.ydlclass.service.YdlUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 用户信息表(YdlUser)表控制层
 *
 * @author makejava
 * @since 2023-01-15 18:10:54
 */
@RestController
@RequestMapping(value = "ydlUser")
public class YdlUserController extends BaseController {
    /**
     * 服务对象
     */
    @Resource
    private YdlUserService ydlUserService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 分页查询
     *
     * @param ydlUser 筛选条件
     * @param
     * @return 查询结果
     */
    @GetMapping
    @Log(title = "查询用户", businessType = "用户操作")
    @Repeat
    public ResponseEntity<Page<YdlUser>> queryByPage(YdlUser ydlUser) {
        return ResponseEntity.ok(this.ydlUserService.queryByPage(ydlUser, PageRequest.of(ydlUser.getPage(), ydlUser.getSize())));
    }

    //@Log(title = "查询用户", businessType = "用户操作")
    @GetMapping(value = "getInfo", produces = "application/json;charset=utf-8")
    public ResponseEntity<HashMap<String, List<String>>> getInfo() {
        return ResponseEntity.ok(this.ydlUserService.getInfo());
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    @HasPerms("system:user:query")
    public ResponseEntity<YdlUser> queryById(@PathVariable("id") Long id) {

        // 获取用户, 根据用户的查询他的角色或者权限
        // 判断 该用户是否有 接口所需要的权限

        return ResponseEntity.ok(this.ydlUserService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param ydlUser 实体
     * @return 新增结果
     */
    @PostMapping
    @HasRole({"admin", "hr"})
    @Log(title = "创建用户", businessType = "用户操作")
    public ResponseEntity<YdlUser> add(@RequestBody YdlUser ydlUser, HttpServletRequest request) {
        ydlUser.setLoginIp(request.getLocalAddr());
        ydlUser.setCreateTime(new Date());
        ydlUser.setCreateBy(getLoginUser().getYdlUser().getUserName());
        ydlUser.setDelFlag("0");

        return ResponseEntity.ok(this.ydlUserService.insert(ydlUser));
    }

    /**
     * 编辑数据
     *
     * @param ydlUser 实体
     * @return 编辑结果
     */
    @PutMapping
    public ResponseEntity<YdlUser> edit(YdlUser ydlUser) {
        return ResponseEntity.ok(this.ydlUserService.update(ydlUser));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public ResponseEntity<Boolean> deleteById(Long id) {
        return ResponseEntity.ok(this.ydlUserService.deleteById(id));
    }

}

