package com.minis.app;

import com.minis.beans.BeansException;
import com.minis.boot.SpringApplication;
import com.minis.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

    public static void main(String[] args) throws ReflectiveOperationException, BeansException {
        SpringApplication.run(App.class, args);
    }

}
