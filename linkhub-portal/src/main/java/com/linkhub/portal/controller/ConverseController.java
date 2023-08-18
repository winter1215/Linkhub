package com.linkhub.portal.controller;


import com.linkhub.common.model.dto.converse.AppendDMConverseMemberRequest;
import com.linkhub.common.model.dto.converse.CreateDMConverseRequest;
import com.linkhub.common.model.pojo.User;
import com.linkhub.common.model.vo.ConverseVo;
import com.linkhub.common.utils.R;
import com.linkhub.portal.security.SecurityUtils;
import com.linkhub.portal.service.IConverseService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 会话表 前端控制器
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-13
 */
@RestController
@RequestMapping("/converse")
public class ConverseController {

    @Autowired
    IConverseService converseService;

    @ApiOperation("创建DM会话")
    @PostMapping("createDMConverse")
    public R  createDMConverse(@RequestBody CreateDMConverseRequest createDMConverseRequest) {
        User user = SecurityUtils.getLoginObj();
        String[] memberIds = createDMConverseRequest.getMemberIds();
        ConverseVo converseVo = converseService.creatDMconverse(user, memberIds);
        return R.ok().setData(converseVo);
    }

    @ApiOperation("在多人会话中添加成员")
    @PostMapping("appendDMConverseMembers")
    public R appendDMConverseMembers(@RequestBody AppendDMConverseMemberRequest appendMemberRequest) {
        User user = SecurityUtils.getLoginObj();
        ConverseVo converseVo = converseService.appendDMConverseMembers(user, appendMemberRequest);
        return R.ok().setData(converseVo);
    }

    @ApiOperation("查找会话")
    @GetMapping("findConverseInfo")
    public R findConverseInfo(@RequestParam String converseId) {
        String userId = SecurityUtils.getLoginUserId();
        ConverseVo converseVo = converseService.findConverseInfo(userId, converseId);
        return R.ok().setData(converseVo);
    }

}
