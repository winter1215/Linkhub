package com.linkhub.portal.controller;


import com.linkhub.common.model.dto.message.*;
import com.linkhub.common.model.vo.MessageVo;
import com.linkhub.common.utils.R;
import com.linkhub.portal.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("recallMessage")
    public R recallMessage(@RequestBody CommonMessageIdDto commonMessageIdDto) {
        MessageVo message = messageService.recallMessage(commonMessageIdDto.getMessageId());
        return R.ok().setData(message);
    }

    @PostMapping("deleteMessage")
    public R deleteMessage(@RequestBody CommonMessageIdDto commonMessageIdDto) {
        MessageVo message = messageService.deleteMessage(commonMessageIdDto.getMessageId());
        return R.ok().setData(message);
    }

    @PostMapping("fetchConverseLastMessages")
    public R fetchConverseLastMessages(@RequestBody List<String> converseIds) {
        List<String> lastMessageIds = messageService.fetchConverseLastMessages(converseIds);
        return R.ok().setData(lastMessageIds);
    }

    @PostMapping("addReaction")
    public R addReaction(@RequestBody ReactionDto reactionDto) {
        boolean flag = messageService.addReaction(reactionDto);
        return R.ok().setData(flag);
    }

    @PostMapping("removeReaction")
    public R removeReaction(@RequestBody ReactionDto reactionDto) {
        boolean flag = messageService.removeReaction(reactionDto);
        return R.ok().setData(flag);
    }



}
