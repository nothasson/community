package com.hasson.community.util;

import com.hasson.community.entity.User;
import org.springframework.stereotype.Component;

@Component
public class HostHolder {
    //起到一个容器的作用,用于持有用户信息,用于代替session

    //Threadlocal 每个线程都有一个map
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
