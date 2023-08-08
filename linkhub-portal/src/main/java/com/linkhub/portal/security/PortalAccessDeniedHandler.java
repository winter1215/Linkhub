package com.linkhub.portal.security;

import cn.hutool.json.JSONUtil;
import com.linkhub.common.enums.ErrorCode;
import com.linkhub.common.utils.R;
import com.linkhub.security.Handler.RestAccessDeniedHandler;
import org.springframework.stereotype.Component;


/**
 * @author CYY
 * @date 2022年11月26日 下午10:30
 * @description 前台鉴权失败处理器
 */
@Component
public class PortalAccessDeniedHandler extends RestAccessDeniedHandler {

    @Override
    public Object errorMsg() {
        return JSONUtil.parse(
                R.error()
                        .code(ErrorCode.NO_AUTH_ERROR.getCode())
                        .message(ErrorCode.NO_AUTH_ERROR.getMessage())
        );
    }
}
