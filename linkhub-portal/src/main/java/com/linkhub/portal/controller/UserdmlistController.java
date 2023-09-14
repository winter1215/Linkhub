package com.linkhub.portal.controller;


import com.linkhub.common.model.dto.converse.ConverseIdRequest;
import com.linkhub.common.model.dto.userdmlist.UserdmlistDto;
import com.linkhub.common.model.pojo.User;
import com.linkhub.common.utils.MapUtils;
import com.linkhub.common.utils.R;
import com.linkhub.portal.security.LinkhubUserDetails;
import com.linkhub.portal.security.SecurityUtils;
import com.linkhub.portal.service.IUserdmlistService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 用户的会话列表 前端控制器
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-08
 */
@RestController
@RequestMapping("user/dmlist")
public class UserdmlistController {

    @Autowired
    IUserdmlistService userdmlistService;

    @ApiOperation("将会话添加到用户私信列表")
    @PostMapping("/addConverse")
    public R addConverse(@RequestBody ConverseIdRequest converseIdRequest) {
        String userId = SecurityUtils.getLoginUserId();
        UserdmlistDto userdmlistDto = userdmlistService.addConverse(userId, converseIdRequest.getConverseId());
        return R.ok()
                .setData(userdmlistDto);
    }
}
