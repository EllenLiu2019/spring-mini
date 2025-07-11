package com.minis.test.ioc;

import com.minis.beans.factory.annotation.Autowired;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Data
public class BaseService {
    private static  final Logger LOGGER = LogManager.getLogger(BaseService.class.getName());
    @Autowired
    private BaseBaseService baseBaseService;
    public void sayHello() {
        LOGGER.info("Base Service says hello");
        baseBaseService.sayHello();
    }
}
