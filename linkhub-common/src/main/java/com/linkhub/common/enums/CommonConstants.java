package com.linkhub.common.enums;

/**
 * @author winter
 * @create 2022-11-27 下午10:03
 */
public interface CommonConstants {
    /**
    * 注册时验证码的模板
    */
    String REGISTER_CODE_TEMPLATE = "你好，欢迎注册 TOWER， 本次验证码为： %s , 有效时间为： " + RedisPrefix.CODE_AVAILABLE_TIME + "分钟，请勿泄漏您的验证码~";

    /**
    * 升序
    */
    String SORT_ORDER_ASC = "ascend";
    /**
    * 降序
    */
    String SORT_ORDER_DESC = "descend";

}
