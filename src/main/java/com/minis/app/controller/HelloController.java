package com.minis.app.controller;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.stereotype.Component;
import com.minis.app.service.BaseService;
import com.minis.web.bind.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class HelloController {
    @Autowired
    BaseService baseService;

    @RequestMapping("/hello")
    public void hello() {
        baseService.getBaseBaseService().getAServiceImpl().sayHello();
    }

}
