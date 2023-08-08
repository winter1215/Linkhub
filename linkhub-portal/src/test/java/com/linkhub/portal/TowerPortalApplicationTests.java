package com.linkhub.portal;

import com.linkhub.common.mapper.MessageMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TowerPortalApplicationTests {
        @Autowired
        MessageMapper messageMapper;

        @Test
        void contextLoads() {
            System.out.println(messageMapper.findOneById("11"));
        }

    }
