package com.linkhub.portal.service.impl;

import com.linkhub.common.config.redis.RedisCache;
import com.linkhub.common.model.pojo.User;
import com.linkhub.portal.service.IUserCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author CYY
 * @date 2022年11月25日 下午3:32
 * @description user的缓存service
 */
@Service
public class UserCacheService implements IUserCacheService {
    @Autowired
    private RedisCache redisCache;
    @Value("${redis.key.user}")
    private String REDIS_KEY_USER;
    @Value("${redis.expire.common}")
    private Integer REDIS_EXPIRE;
    /**
     * 从redis中通过用户名称获取user,未找到返回null
     * @param username 用户名
     * @return null或者user
     */
    @Override
    public User getUserByUsername(String username) {
        String key = REDIS_KEY_USER+":"+username;
        return redisCache.getCacheObject(key);
    }

    @Override
    public void setUser(User user) {
        String key = REDIS_KEY_USER+":"+user.getUsername();
        redisCache.setCacheObject(key,user,REDIS_EXPIRE, TimeUnit.SECONDS);
    }
}
