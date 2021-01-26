package com.hasson.community;

import com.hasson.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void mailTest() {
        mailClient.sendMail("linhch36@mail2.sysu.edu.cn", "test", "小测试");

    }

    @Test
    public void testHtmlMail() {
        int i = 0;
        while (i < 100) {
            String headerUrl = "images.nowcoder.com/header/" + new Random().nextInt(1001) + "t.png";
            System.out.println(headerUrl);
            i++;
        }
    }

}
