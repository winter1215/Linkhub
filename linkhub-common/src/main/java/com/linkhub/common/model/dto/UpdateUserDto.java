package com.linkhub.common.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

/**
 * @author winter
 * @create 2023-05-08 下午3:23
 */
@Data
public class UpdateUserDto {
    String nickname;
    @URL
    String avatar;
    @Length(max = 100, message = "长度有误")
    String introduction;
}
