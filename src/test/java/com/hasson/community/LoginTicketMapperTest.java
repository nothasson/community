package com.hasson.community;

import com.hasson.community.dao.LoginTicketMapper;
import com.hasson.community.entity.LoginTicket;
import com.hasson.community.util.CommunityUtil;
import com.hasson.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoginTicketMapperTest {
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void ticketTest() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(143);
        loginTicket.setExpired(new Date());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicketMapper.insertLoginTicket(loginTicket);

        LoginTicket loginTicket1 = loginTicketMapper.selectByTicket(loginTicket.getTicket());
        System.out.println(loginTicket1);
        loginTicketMapper.updateStatus(loginTicket.getTicket(), 1);

    }

    @Test
    public void testSensitiveFiler() {
        System.out.println(sensitiveFilter.filter("开票xxxx嫖娼"));
    }
}
