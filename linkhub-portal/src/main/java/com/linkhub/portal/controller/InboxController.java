package com.linkhub.portal.controller;


import com.linkhub.portal.service.IInboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 收件箱 前端控制器
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-14
 */
@RestController
@RequestMapping("/inbox")
public class InboxController {
    @Autowired
    private IInboxService inboxService;


}
