package com.minis.app;

import com.minis.app.controller.HelloController;
import com.minis.beans.BeansException;
import com.minis.boot.SpringApplication;
import com.minis.boot.autoconfigure.SpringBootApplication;
import com.minis.context.ConfigurableApplicationContext;

@SpringBootApplication
public class App {

    public static void main(String[] args) throws ReflectiveOperationException, BeansException {
        ConfigurableApplicationContext context = SpringApplication.run(App.class, args);
        HelloController controller = (HelloController) context.getBean("helloController");
        controller.hello();
    }

}
