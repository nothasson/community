# 社区项目学习
已完成清单
- 注册
- 生成验证码
- 分页
- 发放邮箱
- 登录及登录平镇

待整理
- [ ] 注册的流程
- [ ] 集成数据库的流程，书写mapper的两种方式
- [ ] 生成验证码的流程
- [ ] 分页的流程
- [ ] 发送邮件的流程
- [ ] 注册的流程
- [ ] 拦截器 Handlerinterceptorw
- [ ] threadlocal



## 出现BUG

### 1. 拦截器配置问题

```
Error starting ApplicationContext. To display the conditions report re-run your application with 'debug' enabled.
2021-01-29 19:13:33,401 ERROR [restartedMain] o.s.b.d.LoggingFailureAnalysisReporter [LoggingFailureAnalysisReporter.java:40] 

***************************
APPLICATION FAILED TO START
***************************

Description:

Invalid mapping pattern detected: /**/*.css
^
No more pattern data allowed after {*...} or ** pattern element

Action:

Fix this pattern in your application or switch to the legacy parser implementation with `spring.mvc.pathpattern.matching-strategy=ant_path_matcher`.

Disconnected from the target VM, address: '127.0.0.1:14668', transport: 'socket'
```
其实就是这里错了 
```java
registry.addInterceptor(alphaInterceptor)
  .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg")
  .addPathPatterns("/register", "/login");
```

现在Spring MCV 5.3更新后 api已经变了

```{*...} or **```后面不能再接更多的东西

改为

```java
registry.addInterceptor(alphaInterceptor)
  .excludePathPatterns("/*/*.css", "/*/*.js", "/*/*.png", "/*/*.jpg", "/*/*.jpeg")
  .addPathPatterns("/register", "/login");
```

### 2. Cookie为空时会报错

![image-20210130121516118](https://gitee.com/20162180090/piccgo/raw/master/pic/image-20210130121516118.png)

在程序开始的时候默认给他一个cookie？ 现在能想到的方法。。

后面发现是多写了一句话![image-20210130145757731](https://gitee.com/20162180090/piccgo/raw/master/pic/image-20210130145757731.png)

多检查检查controller

## 笔记

### 拦截器的作用

1. 在请求开始的时候查询登录用户
2. 在本次请求中持有用户数据（存到内存中）
3. 在模板视图中显示用户数据w
4. 在请求结束时清理用户数据（从内存中删除）

![image-20210130105353860](https://gitee.com/20162180090/piccgo/raw/master/pic/image-20210130105353860.png)

