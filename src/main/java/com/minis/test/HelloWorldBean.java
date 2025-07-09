package com.minis.test;

import com.minis.web.annotation.RequestMapping;

public class HelloWorldBean {
    @RequestMapping("/get")
    public String doGet() {
        return "doGet() says hello world!";
    }
    @RequestMapping("/post")
    public String doPost() {
        return "doPost() says hello world!";
    }

}
