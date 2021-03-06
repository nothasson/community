package com.hasson.community.controller;

import com.google.code.kaptcha.Producer;
import com.hasson.community.config.KaptchaConfig;
import com.hasson.community.entity.User;
import com.hasson.community.service.UserService;
import com.hasson.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {

        return "/site/register";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage(HttpSession session) {
        System.out.println(session);
        return "/site/login";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> register = userService.register(user);
        if (register == null || register.size() == 0) {
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活！");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", register.get("usernameMsg"));
            model.addAttribute("passwordMsg", register.get("passwordMsg"));
            model.addAttribute("emailMsg", register.get("emailMsg"));
            return "/site/register";
        }
    }

    //    String url = String.format("http://locahost:8080/activation/%s/%s", user.getId(), user.getActivationCode());
    @RequestMapping(path = "/activation/{userId}/{activationCode}", method = RequestMethod.GET)
    public String activate(Model model, @PathVariable("userId") int userId, @PathVariable("activationCode") String activationCode) {
        int result = userService.activate(userId, activationCode);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功，已经可以正常使用了");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，该账号已经激活过了");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败，返回主页");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    //生成的是一张图片所有要声明为void类型，并在response里放东西
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpSession session, HttpServletResponse response) {
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //把验证码存入到session中，供验证使用
        session.setAttribute("kaptcha", text);
        response.setContentType("image/png");
        try {
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
//            e.printStackTrace();
            LOGGER.error("响应验证码报错", e.getMessage());
        }

    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(Model model, String username, String password, String code, boolean isRememberMe
            , HttpSession session, HttpServletResponse response) {
        String kaptcha = session.getAttribute("kaptcha").toString();
        if (StringUtils.isBlank(code)) {
            model.addAttribute("kaptchaMsg", "请输入验证码");
            return "/site/login";
        } else if (StringUtils.isBlank(kaptcha)) {
            model.addAttribute("kaptchaMsg", "验证码生成失败");
            return "/site/login";
        } else if (!kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("kaptchaMsg", "验证码错误，请重新输入");
            return "/site/login";
        }
        int expiredSecond = isRememberMe ? IS_REMEMBER＿ME_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;

        Map<String, Object> map = userService.login(username, password, expiredSecond);
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath("/");
            cookie.setMaxAge(expiredSecond);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue(name = "ticket") String ticket) {
        if (!ticket.equals(123))
            userService.logout(ticket);
        return "redirect:/index";
    }
}
