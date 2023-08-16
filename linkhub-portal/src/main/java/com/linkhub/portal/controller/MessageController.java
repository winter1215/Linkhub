package com.linkhub.portal.controller;


import cn.hutool.log.Log;
import com.linkhub.common.model.dto.message.FetchConverseMessageDto;
import com.linkhub.common.model.dto.message.FetchNearbyMessageDto;
import com.linkhub.common.model.dto.message.SendMsgDto;
import com.linkhub.common.model.pojo.Message;
import com.linkhub.common.model.vo.MessageVo;
import com.linkhub.common.utils.R;
import com.linkhub.portal.service.IInboxService;
import com.linkhub.portal.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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
    @Resource
    private IMessageService messageService;

    @PostMapping("sendMessage")
    public R sendMessage(@RequestBody SendMsgDto sendMsgDto) {
        messageService.sendMessage(sendMsgDto);
        return R.ok();
    }

    @PostMapping("fetchConverseMessage")
    public R fetchConverseMessage(@RequestBody FetchConverseMessageDto fetchConverseMessageDto) {
        List<MessageVo> messages = messageService.fetchConverseMessage(fetchConverseMessageDto);
        return R.ok().setData(messages);
    }

    @PostMapping("fetchNearbyMessage")
    public R fetchNearbyMessage(@RequestBody FetchNearbyMessageDto fetchNearbyMessageDto) {
        List<MessageVo> messages = messageService.fetchNearbyMessage(fetchNearbyMessageDto);
        return R.ok().setData(messages);
    }


}
