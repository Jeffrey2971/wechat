package com.jeffrey.wechat.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.gson.Gson;
import com.jeffrey.wechat.entity.TransResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 对 redis 进行操作的类
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Slf4j
public class SaveAndReadImageDocument {
    private static RedisTemplate<String, String> redisTemplate;

    @Autowired
    public SaveAndReadImageDocument(RedisTemplate<String, String> redisTemplate) {
        SaveAndReadImageDocument.redisTemplate = redisTemplate;
    }

    /**
     * 将给定的的对象转为 json 并以 string 类型的方式存入 redis 中
     *
     * @param wrapperKey string key
     * @param wrapper    string value
     * @return 保存是否成功
     */
    public static boolean serialToJsonAndSave(String wrapperKey, TransResponseWrapper wrapper) {

        if (StringUtils.isBlank(wrapperKey) || wrapper == null) {
            log.error("传入的 wrapperKey 为 0 或 wrapper 为空");
            return false;
        }

        String jsonStr = new Gson().toJson(wrapper);

        if ("{}".equals(jsonStr)) {
            log.error("对象转 Json 失败，结果为空");
            return false;
        }

        Boolean result = redisTemplate.opsForValue().setIfAbsent(wrapperKey, jsonStr, 30, TimeUnit.DAYS);

        if (result == null || !result) {
            log.error("存储 json 字符串时返回的结果为 false或结果为空，可能是键已经存在");
            return false;
        }

        return true;
    }

    /**
     * 根据给定的 wrapperKey 获取 redis 中的 value 并转为 TransResponseWrapper 返回
     *
     * @param wrapperKey 查找的 key
     * @param wrapper    TransResponseWrapper.class
     * @return TransResponseWrapper
     */
    public static TransResponseWrapper deSerialJsonToClass(String wrapperKey, Class<TransResponseWrapper> wrapper) {
        if (StringUtils.isBlank(wrapperKey) || wrapper == null) {
            throw new RuntimeException("传入的 wrapperKey 或 clazz 无效");
        }

        String result = redisTemplate.opsForValue().get(wrapperKey);

        if (StringUtils.isBlank(result)) {
            throw new RuntimeException("获取的结果无效");
        }

        return new Gson().fromJson(result, wrapper);
    }

    /**
     * 判断一个 key 是否存在于 redis 中
     *
     * @param key 给定的 key
     * @return 不为 null 且为 true
     */
    public static boolean containsKey(String key) {
        Boolean result = redisTemplate.hasKey(key);
        return result != null && result;
    }

    /**
     * 移除 redis 中的 key
     *
     * @param key 要删除的 key
     */
    public static void removeKey(String key) {
        Boolean result = redisTemplate.delete(key);
        if (result != null && result) {
            log.info("移除 redis 无效数据：{}", key);
        } else {
            log.error("移除 redis 无效数据失败：{}", key);
        }
    }

    /**
     * 获取给定 key 的过期时间
     *
     * @param key 给定的 key
     * @return 获取过期时间，-2 表示已过期
     */
    public static long getKeyExpireTime(String key) {
        Long expire = redisTemplate.getExpire(key, TimeUnit.DAYS);
        return expire == null ? -2 : expire;
    }

    /**
     * 获取 redis 中的所有文档数据并封装为 List<TransResponseWrapper> 返回
     *
     * @return List<TransResponseWrapper>
     */
    public static Map<String, TransResponseWrapper> getEntities() {

        Set<String> keys = redisTemplate.keys("*");

        if (keys == null || keys.size() == 0) {
            return null;
        }

        HashMap<String, TransResponseWrapper> documentMap = new HashMap<>();

        keys.forEach(item -> documentMap.put(item, new Gson().fromJson(redisTemplate.opsForValue().get(item), TransResponseWrapper.class)));

        return documentMap;

    }
}
