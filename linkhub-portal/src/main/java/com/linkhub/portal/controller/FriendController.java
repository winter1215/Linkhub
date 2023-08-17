package com.linkhub.portal.controller;


import com.linkhub.common.model.dto.friend.DeleteFriendRequest;
import com.linkhub.common.model.dto.friend.SetFriendNicknameRequest;
import com.linkhub.common.model.pojo.User;
import com.linkhub.common.utils.R;
import com.linkhub.portal.security.SecurityUtils;
import com.linkhub.portal.service.IFriendService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 好友表 前端控制器
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-11
 */
@RestController
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    IFriendService friendService;

    @ApiOperation("移除单项好友关系")
    @PostMapping("/removeFriend")
    public R removeFriend(@RequestBody DeleteFriendRequest deleteFriendRequest) {
        User user = SecurityUtils.getLoginObj();

        int flag = friendService.removeFriend(user, deleteFriendRequest);
        return flag > 0 ? R.ok().message("移除成功") : R.error().message("移除失败");
    }

    @ApiOperation("设置好友备注")
    @PostMapping("/setFriendNickname")
    public R setFriendNickname(@RequestBody SetFriendNicknameRequest setFriendNicknameRequest) {
        String userId = SecurityUtils.getLoginUserId();
        boolean flag = friendService.setFriendNickname(userId, setFriendNicknameRequest);
        return flag ? R.ok().message("设置成功") : R.error().message("设置失败");
    }


}
