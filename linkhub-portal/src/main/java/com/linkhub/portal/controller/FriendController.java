package com.linkhub.portal.controller;


import com.linkhub.common.model.common.DeleteFriendRequest;
import com.linkhub.common.model.pojo.FriendRequest;
import com.linkhub.common.model.pojo.User;
import com.linkhub.common.utils.MapUtils;
import com.linkhub.common.utils.R;
import com.linkhub.portal.security.LinkhubUserDetails;
import com.linkhub.portal.service.IFriendService;
import com.linkhub.security.util.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
        LinkhubUserDetails userDetails = SecurityUtils.getLoginObj();
        User user = userDetails.getUser();
        int flag = friendService.removeFriend(user, deleteFriendRequest);
        return flag > 0 ? R.ok().message("移除成功") : R.error().message("移除失败");
    }

}
