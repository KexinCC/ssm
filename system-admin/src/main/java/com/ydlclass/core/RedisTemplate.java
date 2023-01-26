package com.ydlclass.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.ydlclass.configuration.CustomObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import javax.annotation.Resource;
import java.util.Set;

@Component
@Slf4j
public class RedisTemplate {
    @Resource
    private JedisPool jedisPool;

    @Resource
    private CustomObjectMapper customObjectMapper;

    //保存字符串类型的数据 (token)
    public String set(String key, String value, int expire) {
        Jedis jedis = jedisPool.getResource();
        String returnValue = null;

        try {
            returnValue = jedis.setex(key, Math.toIntExact(expire), value);
        } catch (JedisException jedisException) {
            log.error("redis execution error", jedisException);
            jedisPool.returnBrokenResource(jedis);
        } finally {
            jedisPool.returnResource(jedis);
        }
        return returnValue;
    }

    public String get(String key) {
        Jedis jedis = jedisPool.getResource();
        String returnValue = null;

        try {
            returnValue = jedis.get(key);
        } catch (JedisException jedisException) {
            log.error("redis execution error", jedisException);
            jedisPool.returnBrokenResource(jedis);
        } finally {
            jedisPool.returnResource(jedis);
        }
        return returnValue;
    }

    public long remove(String ...key) {
        Jedis jedis = jedisPool.getResource();
        long returnValue = 0L;

        try {
            returnValue = jedis.del(key);
        } catch (JedisException jedisException) {
            log.error("redis execution error", jedisException);
            jedisPool.returnBrokenResource(jedis);
        } finally {
            jedisPool.returnResource(jedis);
        }
        return returnValue;
    }

    // 将对象以序列化的方式存在redis中 (json)
    public String setObject(String key, Object value, int expire) {
        Jedis jedis = jedisPool.getResource();
        String returnValue = null;

        try {
            // 先序列化成json
            String jsonValue = customObjectMapper.writeValueAsString(value);
            if (expire <= 0) {
                returnValue = jedis.set(key, jsonValue);
            } else {
                returnValue = jedis.setex(key, expire, jsonValue);
            }
        } catch (JedisException jedisException) {
            log.error("redis execution error", jedisException);
            jedisPool.returnBrokenResource(jedis);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            jedisPool.returnResource(jedis);
        }
        return returnValue;
    }

    public <T> T getObject(String key, TypeReference<T> typeReference) {
        Jedis jedis = jedisPool.getResource();
        T object = null;

        try {
            // 从redis中获取
            String returnValue = jedis.get(key);
            object = customObjectMapper.readValue(returnValue, typeReference);
        } catch (JedisException jedisException) {
            log.error("redis execution error", jedisException);
            jedisPool.returnBrokenResource(jedis);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            jedisPool.returnResource(jedis);
        }
        return object;
    }

    // 增加 key 的时间
    public long expire(String key,int expire) {
        Jedis jedis = jedisPool.getResource();
        long exp = -1L;

        try {
            // 从redis中获取
            exp = jedis.expire(key, expire);
        } catch (JedisException jedisException) {
            log.error("redis execution error", jedisException);
            jedisPool.returnBrokenResource(jedis);
        } finally {
            jedisPool.returnResource(jedis);
        }
        return exp;
    }

    public Set<String> keys(String prefix) {
        Jedis jedis = jedisPool.getResource();
        Set<String> keys = null;

        try {
            // 从redis中获取
            keys = jedis.keys(prefix);
        } catch (JedisException jedisException) {
            log.error("redis execution error", jedisException);
            jedisPool.returnBrokenResource(jedis);
        } finally {
            jedisPool.returnResource(jedis);
        }
        return keys;
    }
}
