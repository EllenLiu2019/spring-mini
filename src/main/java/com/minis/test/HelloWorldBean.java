package com.minis.test;

import com.minis.web.RequestMapping;

public class HelloWorldBean {
    @RequestMapping("/test")
    public String doGet() {
        return "hello world!";
    }
    public String doPost() {
        return "hello world!";
    }

}
