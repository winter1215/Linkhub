package com.linkhub.portal.controller;


import com.linkhub.common.model.common.OptFriendRequest;
import com.linkhub.common.model.pojo.FriendRequest;
import com.linkhub.common.model.pojo.User;
import com.linkhub.common.utils.MapUtils;
import com.linkhub.common.utils.R;
import com.linkhub.portal.security.LinkhubUserDetails;
import com.linkhub.portal.service.IFriendRequestService;
import com.linkhub.security.util.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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
    public R addFriend(@RequestBody FriendRequest friendRequest) {
        LinkhubUserDetails userDetails = SecurityUtils.getLoginObj();
        User user = userDetails.getUser();
        friendRequest.setFrom(user.getId());

        FriendRequest addFriend = friendRequestService.addFriend(friendRequest);
        Map<String, Object> res = MapUtils.convertToMap(addFriend);
        return R.ok()
                .data(res);
    }

    @ApiOperation("所有与自己相关的好友请求")
    @PostMapping("/allRelated")
    public R allRelated() {
        LinkhubUserDetails userDetails = SecurityUtils.getLoginObj();
        User user = userDetails.getUser();
        List<FriendRequest> friendRequestList = friendRequestService.allRelated(user);
        return R.ok()
                .data("data", friendRequestList);
    }


    @ApiOperation("接收好友请求")
    @PostMapping("/accept")
    public R accept(@RequestBody OptFriendRequest optFriendRequest) {
        LinkhubUserDetails userDetails = SecurityUtils.getLoginObj();
        User user = userDetails.getUser();
        int flag = friendRequestService.accept(user, optFriendRequest);
        return flag > 0 ? R.ok().message("同意成功") : R.error().message("同意失败");
    }

    @ApiOperation("拒绝好友请求")
    @PostMapping("/deny")
    public R deny(@RequestBody OptFriendRequest optFriendRequest) {
        LinkhubUserDetails userDetails = SecurityUtils.getLoginObj();
        User user = userDetails.getUser();
        int flag = friendRequestService.deny(user, optFriendRequest);
        return flag > 0 ? R.ok().message("拒绝成功") : R.error().message("拒绝失败");
    }

    @ApiOperation("取消好友请求")
    @PostMapping("/cancel")
    public R cancel(@RequestBody OptFriendRequest optFriendRequest) {
        LinkhubUserDetails userDetails = SecurityUtils.getLoginObj();
        User user = userDetails.getUser();
        int flag = friendRequestService.cancel(user, optFriendRequest);
        return flag > 0 ? R.ok().message("取消成功") : R.error().message("取消失败");
    }
}
