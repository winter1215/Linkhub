package com.linkhub.portal.controller;


import com.linkhub.common.model.dto.group.CreateGroupDto;
import com.linkhub.common.model.dto.group.UpdateGroupConfigDto;
import com.linkhub.common.model.dto.group.UpdateGroupFieldDto;
import com.linkhub.common.model.pojo.Group;
import com.linkhub.common.model.vo.GroupVo;
import com.linkhub.common.utils.R;
import com.linkhub.portal.service.IGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


}
