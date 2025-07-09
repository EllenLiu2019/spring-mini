package com.minis.test;

import com.minis.beans.factory.annotation.Autowired;
import lombok.Data;

@Data
public class BaseService {
    @Autowired
    private BaseBaseService baseBaseService;
    public void sayHello() {
        System.out.println("Base Service says hello");
        baseBaseService.sayHello();
    }
}
