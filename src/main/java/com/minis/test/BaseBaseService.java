package com.minis.test;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Data
public class BaseBaseService {
    private static final Logger LOGGER = LogManager.getLogger(BaseBaseService.class.getName());
    private AServiceImpl as;
    public void sayHello() {
        LOGGER.info("BaseBaseService says Hello");
    }
    public void init() {
        LOGGER.info("-------call init-method----------");
    }
}
