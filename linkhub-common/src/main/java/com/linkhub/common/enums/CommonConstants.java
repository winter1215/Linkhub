package com.linkhub.common.enums;

/**
 * @author winter
 * @create 2022-11-27 下午10:03
 */
public interface CommonConstants {
    /**
    * 注册时验证码的模板
    */
    String REGISTER_CODE_TEMPLATE = "<p>忘记密码了？ 请使用以下 OTP 作为重置密码凭证:</p>\n" +
            "    <h3>OTP: <strong> %s </strong></h3>\n" +
            "    <p>该 OTP 将会在 "+ RedisPrefix.CODE_AVAILABLE_TIME + "分钟 后过期</p>\n" +
            "    <p style=\"color: grey;\">如果并不是您触发的忘记密码操作，请忽略此电子邮件。</p>";

    /**
    * 升序
    */
    String SORT_ORDER_ASC = "ascend";
    /**
    * 降序
    */
    String SORT_ORDER_DESC = "descend";

}
