package com.linkhub.portal.security;

import com.linkhub.portal.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author CYY
 * @date 2022年11月25日 下午8:17
 * @description 前台的springSecurity配置类
 */
@Configuration
public class PortalSecurityConfig {
    @Autowired
    private IUserService userService;

    /**
     * 将实现UserDetailsService的子类对象注入容器 用于security过滤链加载UserDetails
     */
    @Bean
    public UserDetailsService userDetailsService(){
        return username -> userService.loadUserByUsername(username);
    }

}
