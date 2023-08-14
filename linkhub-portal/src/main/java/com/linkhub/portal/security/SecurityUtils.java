package com.linkhub.portal.security;


import com.linkhub.common.model.pojo.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author CYY
 * @date 2022年07月15日 下午1:17
 * @description 安全服务工具类
 */
public class SecurityUtils {
    /**
     * 从springSecurity全局上下文获取登录用户
     * @return 登录用户,前台登录或者后台登录用户
     */
    public static User getLoginObj(){
        return ((LinkhubUserDetails) getAuthentication().getPrincipal()).getUser();
    }
    public static String getLoginUserId(){
        return ((LinkhubUserDetails) getAuthentication().getPrincipal()).getUser().getId();
    }

    /**
     * 从springSecurity全局上下文获取授权后的对象
     * @return 授权后的对象
     */
    public static Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
}