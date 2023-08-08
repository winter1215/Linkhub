package com.linkhub.common.config.validator;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * @author winter
 * @create 2022-11-22 上午10:32
 */
@Configuration
public class ValidationConfig {

    /**
 * Validator 快速失败配置（默认会校验完所有参数）
 * @author: winter
 * @date: 2022/11/22 上午10:33
 * @description:
 */
    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                // 快速失败模式
                .failFast(true)
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }
}
