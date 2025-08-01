package com.minis.test.controller;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.app.service.IAction;
import com.minis.test.ioc.BaseService;
import com.minis.app.entity.User;
import com.minis.app.service.UserService;
import com.minis.app.service.IService;
import com.minis.web.bind.annotation.RequestMapping;
import com.minis.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
@Slf4j
public class HelloController {
    @Autowired
    BaseService baseService;

    @Autowired
    UserService userService;

    @Autowired
    IAction realAction;

    @Autowired
    IAction anotherRealAction;

    @Autowired
    IService iServiceImpl;

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

    @RequestMapping("/test4")
    @ResponseBody
    public User doTest4(User user) {
        user.setName(user.getName() + "---");
        user.setBirthday(new Date());
        return user;
    }

    @RequestMapping("/test5")
    public void doTest5() {
        baseService.getBaseBaseService().getAService().sayHello();
    }

    @RequestMapping("/test6")
    public User doTest6() {
        return userService.getUser(1);
    }

    @RequestMapping("/test7")
    public List<User> doTest7() {
        return userService.getUserByName("Alice");
    }

    @RequestMapping("/test8")
    public List<User> doTest8() {
        return userService.getAllUsersByName("Alice");
    }

    @RequestMapping("/testaop")
    public void doTestAop(HttpServletRequest request, HttpServletResponse response) throws IOException {
        realAction.doAction();
        response.getWriter().write("action do action");
    }

    @RequestMapping("/testaop2")
    public void doTestAop2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        realAction.doSomething();
        response.getWriter().write("action do something");
    }

    @RequestMapping("/testaop3")
    public void doTestAop3(HttpServletRequest request, HttpServletResponse response) throws IOException {
        anotherRealAction.doAction();
        response.getWriter().write("another action do action");
    }

    @RequestMapping("/testaop4")
    public void doTestAop4(HttpServletRequest request, HttpServletResponse response) throws IOException {
        anotherRealAction.doSomething();
        response.getWriter().write("another action do something");
    }

    @RequestMapping("/testaopthread")
    public String doTestAopThread() {
        iServiceImpl.greeting4Score()
                .addCallback(result -> log.info("callback: executed success, obtained 10 scores."),
                        result -> log.error("callback: got failure for error = {}, returned 2 scores.", result, result));
        return "greeting sending out";
    }

    @RequestMapping("/testaopthread2")
    public void doTestAopThread2() {
        iServiceImpl.greetingException().addCallback(result -> log.info("callback: executed success, obtained 10 scores."),
                result -> log.error("callback: got failure for error = {}, returned 2 scores.", result, result));
    }
}
