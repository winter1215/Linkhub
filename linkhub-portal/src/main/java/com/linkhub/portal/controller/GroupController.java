package com.linkhub.portal.controller;


import com.linkhub.common.model.dto.group.*;
import com.linkhub.common.model.pojo.Group;
import com.linkhub.common.model.vo.GroupVo;
import com.linkhub.common.utils.R;
import com.linkhub.common.model.dto.group.GroupInviteRequest;
import com.linkhub.common.model.vo.GroupVo;
import com.linkhub.common.utils.R;
import com.linkhub.portal.security.SecurityUtils;
import com.linkhub.portal.service.IGroupService;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

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

    @PostMapping("createGroup")
    public R createGroup(@RequestBody CreateGroupDto createGroupDto) {
        GroupVo groupVo = groupService.createGroup(createGroupDto);
        return R.ok().setData(groupVo);
    }
    @PostMapping("updateGroupField")
    public R updateGroupField(@RequestBody UpdateGroupFieldDto updateGroupFieldDto) {
        groupService.updateGroupField(updateGroupFieldDto);
        return R.ok();
    }

    @PostMapping("updateGroupConfig")
    public R updateGroupConfig(@RequestBody UpdateGroupConfigDto updateGroupConfigDto) {
        groupService.updateGroupConfig(updateGroupConfigDto);
        return R.ok();
    }

    @GetMapping("getGroupBasicInfo")
    public R getGroupBasicInfo(@RequestParam String groupId) {
        Group group = groupService.getGroupBasicInfo(groupId);
        return R.ok().setData(group);
    }

    @PostMapping("quitGroup")
    public R quitGroup(@RequestBody CommonGroupIdDto groupIdDto) {
        groupService.quitGroup(groupIdDto.getGroupId());
        return R.ok();
    }

    @PostMapping("appendGroupMemberRoles")
    public R appendGroupMemberRoles(@RequestBody HandleMemberRolesDto appendMemberRolesDto) {
        groupService.handleGroupMemberRoles(appendMemberRolesDto, true);
        return R.ok();
    }

    @PostMapping("removeGroupMemberRoles")
    public R removeGroupMemberRoles(@RequestBody HandleMemberRolesDto handleMemberRolesDto) {
        groupService.handleGroupMemberRoles(handleMemberRolesDto, false);
        return R.ok();
    }

    @PostMapping("createGroupPanel")
    public R createGroupPanel(@Valid @RequestBody CreateGroupPanelDto createGroupPanelDto) {
        return R.ok().setData(groupService.createGroupPanel(createGroupPanelDto));
    }

    @PostMapping("modifyGroupPanel")
    public R modifyGroupPanel(@Valid @RequestBody ModGroupPanelDto modGroupPanelDto) {
        return R.ok().setData(groupService.modifyGroupPanel(modGroupPanelDto));
    }

    @PostMapping("deleteGroupPanel")
    public R deleteGroupPanel(@Valid @RequestBody DeleteGroupPanelDto deleteGroupPanelDto) {
        return R.ok().setData(groupService.deleteGroupPanel(deleteGroupPanelDto));
    }





    @ApiOperation("创建群组邀请")
    @PostMapping("/invite/createGroupInvite")
    public R createGroupInvite(@RequestBody GroupInviteRequest groupInviteRequest) {
        String userId = SecurityUtils.getLoginUserId();
        GroupVo groupVo = groupService.createGroupInvite(userId, groupInviteRequest);
    }

}
