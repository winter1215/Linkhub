package com.linkhub.portal.controller;

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
    public R checkUserOnline(@RequestBody List<String> userIds) {
        return R.ok().data("data", IMUtil.checkUserOnline(userIds));
    }
}
