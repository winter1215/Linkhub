package com.linkhub.portal.controller;


import com.linkhub.common.model.dto.group.GroupInviteRequest;
import com.linkhub.common.model.vo.GroupVo;
import com.linkhub.common.utils.R;
import com.linkhub.portal.security.SecurityUtils;
import com.linkhub.portal.service.IGroupService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 群组 info 前端控制器
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-12
 */
@RestController
@RequestMapping("/group")
public class GroupController {
    @Resource
    private IGroupService groupService;



    @ApiOperation("创建群组邀请")
    @PostMapping("/invite/createGroupInvite")
    public R createGroupInvite(@RequestBody GroupInviteRequest groupInviteRequest) {
        String userId = SecurityUtils.getLoginUserId();
        GroupVo groupVo = groupService.createGroupInvite(userId, groupInviteRequest);
    }

}
