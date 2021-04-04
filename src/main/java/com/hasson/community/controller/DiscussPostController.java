package com.hasson.community.controller;

import com.hasson.community.entity.Comment;
import com.hasson.community.entity.DiscussPost;
import com.hasson.community.entity.Page;
import com.hasson.community.entity.User;
import com.hasson.community.service.CommentService;
import com.hasson.community.service.DiscussPostService;
import com.hasson.community.service.UserService;
import com.hasson.community.util.CommunityUtil;
import com.hasson.community.util.HostHolder;
import org.apache.catalina.Host;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.hasson.community.util.CommunityConstant.ENTITY_TYPE_COMMENT;
import static com.hasson.community.util.CommunityConstant.ENTITY_TYPE_POST;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        System.out.println(title);
        System.out.println(content);
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "你还没有登录");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);
        return CommunityUtil.getJSONString(0, "发布成功");
    }

    @RequestMapping(value = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("post", discussPost);
        model.addAttribute("user", user);

        //评论分页
        page.setLimit(5);
        page.setPath("/discuss//detail/" + discussPostId);
        page.setRows(discussPost.getCommentCount());
        List<Map<String, Object>> commentVoList = new LinkedList<>();
        //评论：给帖子的评论
        //回复：给评论的评论
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST,
                discussPost.getId(), page.getOffset(), page.getLimit());
        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                commentVoList.add(commentVo);
                //评论的评论（回复）
                List<Comment> replayList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getEntityId()
                        , 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new LinkedList<>();
                if (replayList != null) {
                    for (Comment replay : replayList) {
                        Map<String, Object> replayVo = new HashMap<>();
                        replayVo.put("replay", replay);
                        replayVo.put("user", userService.findUserById(replay.getUserId()));
                        //回复目标
                        User target = replay.getTargetId() == 0 ? null : userService.findUserById(replay.getTargetId());
                        replayVo.put("target", target);
                        replyVoList.add(replayVo);
                    }
                }
                commentVo.put("replays", replyVoList);
                int replayCount = commentService.findCommentsCount(ENTITY_TYPE_COMMENT, comment.getEntityId());
                commentVo.put("replayCount",replayCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }
}
