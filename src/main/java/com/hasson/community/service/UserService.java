package com.hasson.community.service;

import com.hasson.community.dao.LoginTicketMapper;
import com.hasson.community.dao.UserMapper;
import com.hasson.community.entity.LoginTicket;
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
    private LoginTicketMapper loginTicketMapper;
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
        String headerUrl = "http://images.nowcoder.com/head/" + new Random().nextInt(1000) + "t.png";
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

    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        //验证账号密码是否符合要求和正确
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "用户名不存在");
            return map;
        }
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活，请查收邮箱进行激活");
            return map;
        }
        String s = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(s)) {
            map.put("passwordMsg", "密码错误，请重新输入");
            return map;
        }

        //生成一个登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);  //有效
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }

    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeader(int userId, String headerUrl) {

        return userMapper.updateHeader(userId, headerUrl);
    }

    public Map<String, Object> updatePassword(int id, String password, String newPassword, String confirmPassword) {
        Map<String, Object> map = new HashMap<>();
        User user = userMapper.selectById(id);
        String testPassword = CommunityUtil.md5(password + user.getSalt());
        if (!newPassword.equals(confirmPassword)) {
            map.put("newMsg", "两次密码需保持一致");
            return map;
        }
        if (!testPassword.equals(user.getPassword())) {
            map.put("oldMsg", "原密码错误");
            return map;
        }
        if (StringUtils.isBlank(newPassword)) {
            map.put("newMsg", "新密码不能为空");
            return map;
        }
        String newSalt = CommunityUtil.generateUUID().substring(0, 5);
        String newPass = CommunityUtil.md5(newPassword + newSalt);
        if (newPass.equals(testPassword)) {
            map.put("newMsg", "新密码不能与旧密码相同");
            return map;
        }
        userMapper.updateSalt(id, newSalt);

        userMapper.updatePassword(id, newPass);
        return map;
    }
}
