package com.linkhub.portal.controller;


import com.linkhub.common.model.dto.friend.AddFriendDto;
import com.linkhub.common.model.dto.friend.OptFriendRequest;
import com.linkhub.common.model.pojo.FriendRequest;
import com.linkhub.common.model.pojo.User;

import com.linkhub.common.utils.R;
import com.linkhub.portal.security.SecurityUtils;
import com.linkhub.portal.service.IFriendRequestService;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * <p>
 * 好友申请表 前端控制器
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-13
 */
@RestController
@RequestMapping("/friend/request")
public class FriendRequestController {

    @Autowired
    IFriendRequestService friendRequestService;
    @ApiOperation("添加好友")
    @PostMapping("/add")
    public R addFriend(@RequestBody AddFriendDto addFriendDto) {
        FriendRequest addFriend = friendRequestService.addFriend(addFriendDto);
        return R.ok()
                .setData(addFriend);
    }

    @ApiOperation("所有与自己相关的好友请求")
    @PostMapping("/allRelated")
    public R allRelated() {
        String userId = SecurityUtils.getLoginUserId();
        List<FriendRequest> friendRequestList = friendRequestService.allRelated(userId);
        return R.ok()
                .setData(friendRequestList);
    }


    @ApiOperation("接收好友请求")
    @PostMapping("/accept")
    public R accept(@RequestBody OptFriendRequest optFriendRequest) {
        String userId = SecurityUtils.getLoginUserId();
        int flag = friendRequestService.accept(userId, optFriendRequest);
        return flag > 0 ? R.ok().message("同意成功") : R.error().message("同意失败");
    }

    @ApiOperation("拒绝好友请求")
    @PostMapping("/deny")
    public R deny(@RequestBody OptFriendRequest optFriendRequest) {
        String userId = SecurityUtils.getLoginUserId();
        int flag = friendRequestService.deny(userId, optFriendRequest);
        return flag > 0 ? R.ok().message("拒绝成功") : R.error().message("拒绝失败");
    }

    @ApiOperation("取消好友请求")
    @PostMapping("/cancel")
    public R cancel(@RequestBody OptFriendRequest optFriendRequest) {
        String userId = SecurityUtils.getLoginUserId();
        int flag = friendRequestService.cancel(userId, optFriendRequest);
        return flag > 0 ? R.ok().message("取消成功") : R.error().message("取消失败");
    }
}
