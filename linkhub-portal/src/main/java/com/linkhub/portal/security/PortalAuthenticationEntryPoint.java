package com.linkhub.portal.security;

import cn.hutool.json.JSONUtil;
import com.linkhub.common.enums.ErrorCode;
import com.linkhub.common.utils.R;
import com.linkhub.security.Handler.RestAuthenticationEntryPoint;
import org.springframework.stereotype.Component;


/**
 * @author CYY
 * @date 2022年11月26日 下午6:00
 * @description 前台认证失败的处理器
 */
@Component
public class PortalAuthenticationEntryPoint extends RestAuthenticationEntryPoint {
    @Override
    public Object errorMsg() {
        return JSONUtil.parse(
                R.error()
                        .code(ErrorCode.AUTHENTICATION_ERROR.getCode())
                        .message(ErrorCode.AUTHENTICATION_ERROR.getMessage())
        );
    }
}
