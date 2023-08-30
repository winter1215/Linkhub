package com.linkhub.portal.controller;


import com.linkhub.common.model.dto.group.GroupEditRequest;
import com.linkhub.common.model.dto.group.GroupInviteApplyRequest;
import com.linkhub.common.model.dto.group.GroupInviteRequest;
import com.linkhub.common.model.pojo.GroupInvite;
import com.linkhub.common.model.pojo.User;
import com.linkhub.common.utils.R;
import com.linkhub.portal.security.SecurityUtils;
import com.linkhub.portal.service.IGroupInviteService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 群组邀请码 前端控制器
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-20
 */
@RestController
@RequestMapping("/group/invite")
public class GroupInviteController {

    @Autowired
    IGroupInviteService groupInviteService;

    @ApiOperation("创建群组邀请")
    @PostMapping("/createGroupInvite")
    public R createGroupInvite(@RequestBody GroupInviteRequest groupInviteRequest) {
        String userId = SecurityUtils.getLoginUserId();
        GroupInvite groupInvite = groupInviteService.createGroupInvite(userId, groupInviteRequest);
        return R.ok().setData(groupInvite);
    }

    @ApiOperation("编辑群组邀请码")
    @PostMapping("/editGroupInvite")
    public R editGroupInvite(@RequestBody GroupEditRequest groupEditRequest) {
        String userId = SecurityUtils.getLoginUserId();
        int flag = groupInviteService.editGroupInvite(userId, groupEditRequest);
        return flag > 0 ? R.ok() : R.error();
    }

    @ApiOperation("获取群组所有邀请码")
    @GetMapping("/getAllGroupInviteCode")
    public R getAllGroupInviteCode(@RequestParam String groupId) {
        String userId = SecurityUtils.getLoginUserId();
        List<GroupInvite> groupInvites = groupInviteService.getAllGroupInviteCode(userId, groupId);
        return R.ok().setData(groupInvites);
    }

    @ApiOperation("根据邀请码查找邀请信息")
    @GetMapping("/findInviteByCode")
    public R findGroupInviteByCode(@RequestParam String code) {
        GroupInvite groupInvite = groupInviteService.findGroupInviteByCode(code);
        return R.ok().setData(groupInvite);
    }

    @ApiOperation("应用群组邀请(通过群组邀请加入群组)")
    @PostMapping("/invite/applyInvite")
    public R applyInvite(@RequestBody GroupInviteApplyRequest groupInviteApplyRequest){
        User user = SecurityUtils.getLoginObj();
        groupInviteService.applyInvite(user, groupInviteApplyRequest);
        return R.ok();
    }
}
