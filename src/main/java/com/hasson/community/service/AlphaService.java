package com.hasson.community.service;

import com.hasson.community.dao.DiscussPostMapper;
import com.hasson.community.dao.UserMapper;
import com.hasson.community.entity.DiscussPost;
import com.hasson.community.entity.User;
import com.hasson.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

@Service
public class AlphaService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save() {
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("123@asd.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.com");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle("haha");
        discussPost.setContent("今天天气不错");
        discussPost.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(discussPost);

        Integer abc = Integer.valueOf("abc");

        return "OK";
    }

    public Object save2() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return transactionTemplate.execute(new TransactionCallback<Object>() {

            @Override
            public Object doInTransaction(TransactionStatus status) {
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("123@asd.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/99t.com");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                DiscussPost discussPost = new DiscussPost();
                discussPost.setUserId(user.getId());
                discussPost.setTitle("haha");
                discussPost.setContent("今天天气不错");
                discussPost.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(discussPost);

                Integer abc = Integer.valueOf("abc");
                return "OK";
            }
        });
    }
}
