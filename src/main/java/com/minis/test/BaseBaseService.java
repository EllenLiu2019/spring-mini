package com.minis.test;

import lombok.Data;

@Data
public class BaseBaseService {
    private AServiceImpl as;
    public void sayHello() {
        System.out.println("BaseBaseService says Hello");
    }
}
