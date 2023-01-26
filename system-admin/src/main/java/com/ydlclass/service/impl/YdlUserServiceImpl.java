package com.ydlclass.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ydlclass.configuration.CustomObjectMapper;
import com.ydlclass.constant.Constants;
import com.ydlclass.core.RedisTemplate;
import com.ydlclass.entity.YdlLoginUser;
import com.ydlclass.entity.YdlMenu;
import com.ydlclass.entity.YdlRole;
import com.ydlclass.entity.YdlUser;
import com.ydlclass.dao.YdlUserDao;
import com.ydlclass.exception.PasswordIncorrectException;
import com.ydlclass.exception.UserNotFoundException;
import com.ydlclass.service.YdlUserService;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户信息表(YdlUser)表服务实现类
 *
 * @author makejava
 * @since 2023-01-15 18:10:54
 */
@Service("ydlUserService")
@Slf4j
public class YdlUserServiceImpl implements YdlUserService {
    @Resource
    private YdlUserDao ydlUserDao;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private CustomObjectMapper customObjectMapper;
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private HttpServletRequest request;

    //HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    //HttpServletRequest request = null;



    /**
     * 通过ID查询单条数据
     *
     * @param userId 主键
     * @return 实例对象
     */
    @Override
    public YdlUser queryById(Long userId) {
        return this.ydlUserDao.queryById(userId);
    }

    /**
     * 分页查询
     *
     * @param ydlUser     筛选条件
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    @Override
    public Page<YdlUser> queryByPage(YdlUser ydlUser, PageRequest pageRequest) {
        long total = this.ydlUserDao.count(ydlUser);
        return new PageImpl<>(this.ydlUserDao.queryAllByLimit(ydlUser, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param ydlUser 实例对象
     * @return 实例对象
     */
    @Override
    public YdlUser insert(YdlUser ydlUser) {
        this.ydlUserDao.insert(ydlUser);
        return ydlUser;
    }

    /**
     * 修改数据
     *
     * @param ydlUser 实例对象
     * @return 实例对象
     */
    @Override
    public YdlUser update(YdlUser ydlUser) {
        this.ydlUserDao.update(ydlUser);
        return this.queryById(ydlUser.getUserId());
    }

    /**
     * 通过主键删除数据
     *
     * @param userId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long userId) {
        return this.ydlUserDao.deleteById(userId) > 0;
    }

    @Override
    public YdlLoginUser login(String userName, String password) throws JsonProcessingException {

        // 使用用户名查询用户
        YdlUser ydlUser = ydlUserDao.queryByUserName(userName);
        if (ydlUser == null) throw new UserNotFoundException("执行登录操作时 [" + userName + "] 该用户不存在");
        // 有用户 比较密码
        if (!password.equals(ydlUser.getPassword())) {
            log.info("执行登录操作时 [" + userName + "] 该用户密码不正确");
            throw new PasswordIncorrectException("执行登录操作时 [" + userName + "] 该用户密码不正确");
        }
        // 如果验证成功 ,
        // 生成token 并封装一个YdlLoginUser 保存在redis
        String token = UUID.randomUUID().toString();

        // 通过 ip 获取其地理位置
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        UserAgent userAgent = new UserAgent(request.getHeader("User-Agent"));
        ResponseEntity<String> forEntity = restTemplate.getForEntity("https://whois.pconline.com.cn/ipJson.jsp?ip=122.255.144.12&json=true", String.class);
        String body = forEntity.getBody();
        Map<String, String> map = customObjectMapper.readValue(body, new TypeReference<>() {
        });

        String location = map.get("addr") + map.get("pro") + map.get("city") + map.get("region");


        YdlLoginUser ydlLoginUser = YdlLoginUser.builder()
                .userId(ydlUser.getUserId())
                .token(token)
                .ipaddr(request.getRemoteAddr())
                .loginTime(new Date())
                .os(userAgent.getOperatingSystem().name())
                .browser(userAgent.getBrowser().name())
                .loginLocation(location)
                .ydlUser(ydlUser)
                .build();
        // 对 key 进行处理 token:username:uuid

        /**
         * 实现唯一的 token
         */
        // 1.根据用户名生成 key 前缀
        String keyPrefix = Constants.TOKEN_PREFIX + userName + ":";
        // 2.查询以 token:usename: 前缀的数据
        Set<String> keys = redisTemplate.keys(keyPrefix + "*");
        // 3.删除原来的数据
        keys.forEach(key -> redisTemplate.remove(key));
        // 4.把新的数据加入redis
        redisTemplate.setObject(keyPrefix + token, ydlLoginUser, Constants.TOKEN_TIME);

        return ydlLoginUser;
    }

    @Override
    public void logout() {
        // 删除 redis 中的 token user 这些数据
        // 获取首部信息 token
        String token = request.getHeader(Constants.HEAD_AUTHORIZATION);
        redisTemplate.remove(Constants.TOKEN_PREFIX + request.getHeader("username") + ":" + token);
    }

    @Override
    public HashMap<String, List<String>> getInfo() {

        YdlLoginUser loginUser = getLoginUser();
        Long userId = loginUser.getUserId();

        // 2.查询当前用户的角色和权限
        YdlUser info = ydlUserDao.getInfo(userId);

        // 处理权限和角色的相关信息
        // roles:token
        List<String> roleTags = info.getYdlRoles().stream().map(YdlRole::getRoleTag).collect(Collectors.toList());


        redisTemplate.setObject(Constants.ROLE_PREFIX + loginUser.getToken(), roleTags, Constants.TOKEN_TIME);

        // 所有的权限
        List<String> perms = new ArrayList<>();
        info.getYdlRoles()
                .stream()
                .map(YdlRole::getYdlMenus)
                .forEach(menus -> perms.addAll(menus.stream().map(YdlMenu::getPerms).collect(Collectors.toList())));

        redisTemplate.setObject(Constants.PERM_PRIFIX + loginUser.getToken(), perms, Constants.TOKEN_TIME);

        // 整合数据
        HashMap<String, List<String>> data = new HashMap<>();
        data.put("roles", roleTags);
        data.put("perms", perms);
        return data;
    }

    private YdlLoginUser getLoginUser() {
        String tokenKey = Constants.TOKEN_PREFIX + request.getHeader("username") + ":" + request.getHeader(Constants.HEAD_AUTHORIZATION);
        return redisTemplate.getObject(tokenKey, new TypeReference<>() {
        });
    }

}
