package com.hasson.community;

import com.hasson.community.dao.DiscussPostMapper;
import com.hasson.community.dao.UserMapper;
import com.hasson.community.entity.DiscussPost;
import com.hasson.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectUser() {
//        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 5, 10);
//        for (DiscussPost d :
//                discussPosts) {
//            System.out.println(discussPosts);
//        }
        System.out.println(discussPostMapper.selectDiscussPostRows(0));
    }
}
