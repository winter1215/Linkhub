package com.linkhub.portal.controller;


import com.linkhub.portal.service.IGroupService;
import org.springframework.beans.factory.annotation.Autowired;
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


}
