package com.linkhub.portal.controller;


import com.linkhub.common.model.dto.ack.UpdateAckDto;
import com.linkhub.common.model.pojo.Ack;
import com.linkhub.common.utils.R;
import com.linkhub.portal.service.IAckService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayDeque;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-27
 */
@RestController
@RequestMapping("chat/ack")
public class AckController {
    @Resource
    private IAckService ackService;

    @PostMapping("update")
    public R updateAck(@Valid @RequestBody UpdateAckDto updateAckDto) {
        boolean flag = ackService.updateAck(updateAckDto);
        return R.ok().setData(flag);
    }

    @GetMapping("all")
    public R allAck() {
        List<Ack> acks = ackService.listAllAck();
        return R.ok().setData(acks);
    }
}
