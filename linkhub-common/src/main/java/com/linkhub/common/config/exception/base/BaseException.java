package com.linkhub.common.config.exception.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author winter
 * @create 2022-08-25 上午10:36
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class BaseException extends RuntimeException {
    private String module;
    private String message;
    private Integer code;



    public String getMessage() {
        // todo: 考虑削短 异常信息，栈太长
        return message;
    }


}
