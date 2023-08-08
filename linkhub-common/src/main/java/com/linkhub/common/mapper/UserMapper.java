package com.linkhub.common.mapper;

import com.linkhub.common.model.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author CYY&winter
 * @since 2022-11-20
 */
public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户，未找到或者被删除返回null
     */
    User selectUserByUsername(String username);

    /**
     * 通过邮箱获取用户
     * @param email 电子邮箱
     * @return 未找到或者被删除返回null，反之返回user对象
     */
    User selectUserByMail(String email);
}
