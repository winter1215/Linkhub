package com.linkhub.portal.controller;


import com.linkhub.common.utils.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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

    @ApiOperation("创建DM会话")
    @PostMapping("createDMConverse")
    public R  createDMConverse
}
