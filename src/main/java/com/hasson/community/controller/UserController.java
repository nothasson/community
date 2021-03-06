package com.hasson.community.controller;

import com.hasson.community.annotation.LoginRequired;
import com.hasson.community.entity.User;
import com.hasson.community.service.UserService;
import com.hasson.community.util.CommunityUtil;
import com.hasson.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${community.path.domain}")
    private String domain;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "uploadHeader", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有上传图片");
            return "/site/setting";
        }
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf('.'));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "图片的格式有误,请重新上传");
            return "/site/setting";
        }
        //生成随机文件名
        filename = CommunityUtil.generateUUID() + suffix;
        File dest = new File(uploadPath + "\\" + filename);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            LOGGER.error("上传失败" + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常");

        }
        //更新当前用户的头像(注意要是web访问路径)
        User user = hostHolder.getUser();
        String headerUrl = domain + "/user/head/" + filename;
        userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/index";
    }

    @RequestMapping(path = "/head/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            LOGGER.error("读取头像失败: " + e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(path = "/modifyPass", method = RequestMethod.POST)
    public String updatePassword(Model model, String oldPassword, String newPassword, String confirmPassword) {
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.updatePassword(user.getId(), oldPassword, newPassword, confirmPassword);
        if (map == null || map.size() == 0) {  //修改成功
            model.addAttribute("msg", "修改成功了,即将返回主页");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("oldMsg", map.get("oldMsg"));
            model.addAttribute("newMsg", map.get("newMsg"));
            return "/site/setting";
        }
    }
}
