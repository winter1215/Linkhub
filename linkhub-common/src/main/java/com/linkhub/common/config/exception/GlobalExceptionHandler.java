package com.linkhub.common.config.exception;



import com.linkhub.common.utils.R;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理
 * @author winter
 * @create 2022-04-03 下午8:33
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    public R error(GlobalException e) {
        // code 默认 ResultCode.ERROR
        log.error(e.getMessage());
        e.printStackTrace();
        return R.error().code(e.getCode()).message(e.getMessage());
    }

    /**
     *  拦截参数校验异常
     * @param e:
     * @return: com.winter.utils.R
     * @author: winter
     * @date: 2022/4/8 下午2:45
     * @description:
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R error(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> collect = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        log.error(e.getMessage());
        e.printStackTrace();
        return R.error().message(collect.get(0));
    }

    /**
     * validation 异常拦截器，开启了快速失败，所以 message 应该只有一个
     * @return: com.tower.server.utils.R
     * @author: winter
     * @date: 2022/11/22 上午11:42
     * @description:
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public R error(ConstraintViolationException e) {
        List<String> list = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        log.error(e.getMessage());
        e.printStackTrace();
        return R.error().message(list.get(0));
    }

    /**
     * 拦截统一的 baseException 的异常
     * @param e:
     * @return: com.tower.server.utils.R
     * @author: winter
     * @date: 2022/8/25 上午11:07
     * @description:
     */
    @ExceptionHandler(RuntimeException.class)
    public R error(RuntimeException e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return R.error().message("服务器异常");
    }

    /**
     * 拦截所有未被捕捉到的异常
     * @return: com.tower.server.utils.R
     * @author: winter
     * @date: 2022/11/22 上午10:29
     * @description:
     */
    @ExceptionHandler(Exception.class)
    public R error(Exception e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return R.error().message("服务器异常！");
    }


}
