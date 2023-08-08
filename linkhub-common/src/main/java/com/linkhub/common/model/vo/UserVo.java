package com.linkhub.common.model.vo;

import lombok.Data;

/**
 * @author winter
 * @create 2023-03-06 下午8:29
 */
@Data
public class UserVo {
    Long id;
    String nickname;
    String username;
    String avatar;
    Integer gender;
    String introduction;
    Boolean status;
    // todo： 脱敏后需要多少属性
}
