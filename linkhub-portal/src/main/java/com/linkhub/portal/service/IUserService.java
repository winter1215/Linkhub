package com.linkhub.portal.service;

import com.linkhub.common.model.common.*;
import com.linkhub.common.model.dto.user.*;
import com.linkhub.common.model.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.linkhub.common.model.pojo.UserSetting;
import com.linkhub.common.utils.R;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author CYY&winter
 * @since 2022-11-20
 */
public interface IUserService extends IService<User> {
    int register(RegisterUser registerUser);

    /**
     * 用户登录
     * @return 登录成功返回token失败抛出异常交于GlobalExceptionHandler统一发送error消息
     */
    String login(String email, String password);

    /**
     * 通过用户名获取user对象(实现UserDetails的对象)
     * @param username 用户名
     * @return user对象,不存在返回null
     */
    UserDetails loadUserByUsername(String username);

    /**
     * 通过用户名从redis或者数据库获取用户，redis不存在会从数据库查询并将结果存入redis如果数据库中不存在则返回null
     * @param username 用户名
     * @return 用户对象或者null
     */
    User getUserByUsername(String username);

    /**
     * 更新用户，通过用户名更新redis的user和通过id更新数据库的user
     * @param user user
     */
    int updateUser(User user);

    /**
     * 忘记密码，验证验证码是否正确，再通过邮箱获取对应用户更改其密码
     *
     * @param otp 验证码
     * @param email 邮箱
     * @param password 新密码
     * @return 是否成功
     */
    R forgetPassword(String otp, String email, String password);

    /**
     * 更新用户密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    R updatePassword(String oldPassword, String newPassword);

    /**
     * 更新用户信息
     * @param updateUserDto:
     * @return: void
     * @author: winter
     * @date: 2023/5/8 下午3:33
     * @description:
     */
    int updateUserDetail(UpdateUserDto updateUserDto);


    String createTemporaryUser(UserNameRequest userNameRequest);

    String claimTemporaryUser(ClaimUserDto claimUserDto);

    UserInfoDto resolveToken(TokenRequest tokenRequest);

    UserInfoDto searchUserWithUniqueName(UniqueNameRequest uniqueNameRequest);

    UserSetting getUserSettings(User user);

    List<UserInfoDto> getUserInfoList(UserIdsRequest userIdsRequest);
}
