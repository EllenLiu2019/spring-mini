package com.minis.test.ioc;

import com.minis.beans.factory.annotation.Autowired;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Data
public class BaseBaseService {
    private static final Logger LOGGER = LogManager.getLogger(BaseBaseService.class.getName());

    @Autowired
    private AServiceImpl aService;
    public void sayHello() {
        LOGGER.info("BaseBaseService says Hello");
    }
    public void init() {
        LOGGER.info("-------call init-method----------");
    }
}
