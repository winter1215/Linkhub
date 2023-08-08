package com.linkhub.portal.service;

import com.linkhub.common.model.pojo.User;

/**
 * user的缓存service
 */
public interface IUserCacheService {
    /**
     * 从redis中通过用户名称获取user,未找到返回null
     * @param username 用户名
     * @return null或者user
     */
    User getUserByUsername(String username);

    /**
     * 将user存入redis中，以用户名作为key
     * @param user 等待存入的用户
     */
    void setUser(User user);
}
