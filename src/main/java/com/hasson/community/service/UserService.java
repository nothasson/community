package com.hasson.community.service;

import com.hasson.community.dao.UserMapper;
import com.hasson.community.entity.User;
import com.hasson.community.util.CommunityConstant;
import com.hasson.community.util.CommunityUtil;
import com.hasson.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        //验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该用户名已存在");
            return map;
        }
        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已存在");
            return map;
        }

        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        String md5 = CommunityUtil.md5(user.getPassword() + user.getSalt());
        user.setPassword(md5);
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        String headerUrl = "images.nowcoder.com/header/" + new Random().nextInt(1000) + "t.png";
        user.setHeaderUrl(headerUrl);
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //返回activation.html，激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        //http://locahost:8080/activation/id/code
        String url = String.format("http://localhost:8080/activation/%s/%s", user.getId(), user.getActivationCode());
        context.setVariable("url", url);
        String content = templateEngine.process("mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);
        return map;
    }

    public int activate(int userid, String activationCode) {
        User user = userMapper.selectById(userid);
        if (user.getActivationCode().equals(activationCode)) {
            if (user.getStatus() == 0) {
                userMapper.updateStatus(userid, 1);
                return ACTIVATION_SUCCESS;
            } else if (user.getStatus() == 1) {
                return ACTIVATION_REPEAT;
            }
        }
        return ACTIVATION_FAILURE;
    }
}
