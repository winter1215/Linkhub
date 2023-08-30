package com.linkhub.portal;
import com.linkhub.common.model.dto.inbox.RemoveInboxDto;
import com.linkhub.common.model.pojo.Inbox.Payload;
import java.time.LocalDateTime;

import com.linkhub.common.mapper.MessageMapper;
import com.linkhub.common.model.pojo.Inbox;
import com.linkhub.portal.service.IGroupService;
import com.linkhub.portal.service.IInboxService;
import com.linkhub.security.util.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TowerPortalApplicationTests {
    @Autowired
    IInboxService iInboxService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Test
    void contextLoads() {
        RemoveInboxDto removeInboxDto = new RemoveInboxDto();
        removeInboxDto.setUserId("winter");
        removeInboxDto.setType("message");
        removeInboxDto.setConserveId("abcd");
        removeInboxDto.setMessageId("abcd");

        iInboxService.removeMsgInbox(removeInboxDto);
    }

}
