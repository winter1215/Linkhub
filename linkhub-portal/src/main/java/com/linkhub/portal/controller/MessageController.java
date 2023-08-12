package com.linkhub.portal.controller;


import cn.hutool.log.Log;
import com.linkhub.common.model.dto.message.SendMsgDto;
import com.linkhub.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 聊天记录 前端控制器
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-08
 */
@RestController
@RequestMapping("/message")
@Slf4j
public class MessageController {

    @PostMapping("sendMessage")
    public R sendMessage(@RequestBody SendMsgDto sendMsgDto) {
        return R.ok();
    }

}
