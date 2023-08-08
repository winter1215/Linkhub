package com.linkhub.security.config;

import com.linkhub.security.Handler.RestAccessDeniedHandler;
import com.linkhub.security.Handler.RestAuthenticationEntryPoint;
import com.linkhub.security.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * @author CYY
 * @date 2022年11月24日 下午8:12
 * @description SpringSecurity通用配置
 */
@Configuration
public class CommonSecurityConfig {

    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private RestAccessDeniedHandler restAccessDeniedHandler;

    /**
     * 密码加密解密
     */
    @Bean
    public PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}
    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter(){return  new JwtAuthenticationTokenFilter();}
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        //放行白名单
        for(String url : ignoreUrlsConfig.getUrls()){
            httpSecurity.authorizeRequests().antMatchers(url).permitAll();
        }
        httpSecurity
                .cors()
                .and()
                // CSRF禁用
                .csrf().disable()
                // 基于token，所以不需要session机制来存储登录的对象
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                //(所有白名单外的请求)都需要认证
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                //添加jwt过滤器
                .addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                //添加异常处理
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint) // token 无效异常返回
                .accessDeniedHandler(restAccessDeniedHandler); // 用户无权限异常返回
        return httpSecurity.build();
    }

    /**
     * 使用该跨域同源策略
     * @author: winter
     * @date: 2023/5/9 上午9:07
     * @description:
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // 允许所有来源
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 允许的方法
        configuration.setAllowedHeaders(Arrays.asList("*")); // 允许所有请求头
        configuration.setMaxAge(3600L); // 预检请求的有效期，单位秒

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
