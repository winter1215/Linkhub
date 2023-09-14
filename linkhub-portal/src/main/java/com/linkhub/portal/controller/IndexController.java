package com.linkhub.portal.controller;

import cn.hutool.log.Log;
import com.linkhub.common.model.dto.user.UserIdsDto;
import com.linkhub.common.utils.R;
import com.linkhub.portal.im.util.IMUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author winter
 * @create 2023-08-15 下午5:42
 */
@RestController
@RequestMapping("/gateway")
public class IndexController {
    @PostMapping("checkUserOnline")
    public R checkUserOnline(@RequestBody UserIdsDto userIdsDto) {
        System.out.println("aaa");
        return R.ok().setData(IMUtil.checkUserOnline(userIdsDto.getUserIds()));
    }
}
