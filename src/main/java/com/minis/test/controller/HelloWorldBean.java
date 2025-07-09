package com.minis.test.controller;

import com.minis.test.entity.User;
import com.minis.web.annotation.RequestMapping;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class HelloWorldBean {
    @RequestMapping("/get")
    public String doGet(Date date, String name) {
        String formattedDate = DateTimeFormatter
                .ofPattern("yyyy-MM-dd")
                .format(date.toInstant().atZone(ZoneId.systemDefault()));
        return "doGet " + name + " says Hello on " + formattedDate;
    }
    @RequestMapping("/post")
    public String doPost(String name) {
        return "doPost() says I'm " + name;
    }
    @RequestMapping("/postBean")
    public String doTestBean(User user) {
        String formattedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .format(user.getBirthday()
                        .toInstant()
                        .atZone(ZoneId.systemDefault()));
        return user.getName() + "'s[id=" + user.getId() + "] birthday: " + formattedDate;
    }

}
