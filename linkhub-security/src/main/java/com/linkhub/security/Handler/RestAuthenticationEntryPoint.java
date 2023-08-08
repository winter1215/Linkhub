package com.linkhub.security.Handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 模板方法: 实际实现的是 portal/ admin 中的子类,注入 ioc
 * @author CYY
 * @date 2022年11月26日 下午4:51
 * @description 定义验证失败的处理器，子类实现
 */
public abstract class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control","no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println(errorMsg());
        response.getWriter().flush();
    }
    public abstract Object errorMsg();
}
