package com.linkhub.portal;

import com.linkhub.common.mapper.MessageMapper;
import com.linkhub.security.util.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TowerPortalApplicationTests {
        @Autowired
        MessageMapper messageMapper;
        @Autowired
        JwtTokenUtil jwtTokenUtil;
        @Test
        void contextLoads() {
            String username = jwtTokenUtil.getUsernameFromToken("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMzIzMTEzNDQyQHFxLmNvbSIsImNyZWF0ZWQiOjE2OTE2ODU5MDk1NTIsImV4cCI6MTY5MjI5MDcwOX0.wd_Ki80ZTiYez0cj0xtcM2I5zykYRTd6AB-apIP_l1u_4grPo6-QDhFOhJmrBI9HIdaZVy3UxLFQXbydt0w3nw");
            System.out.println(username);
        }

    }
