package com.linkhub.portal;

import com.linkhub.common.mapper.MessageMapper;
import com.linkhub.portal.service.IGroupService;
import com.linkhub.security.util.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TowerPortalApplicationTests {
    @Autowired
    MessageMapper messageMapper;
    @Autowired
    IGroupService groupService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Test
    void contextLoads() {
        groupService.checkUserIsOwner();
    }

}
