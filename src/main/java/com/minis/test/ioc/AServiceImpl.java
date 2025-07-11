package com.minis.test.ioc;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Data
@NoArgsConstructor
public class AServiceImpl implements AService {
    private static final Logger LOGGER = LogManager.getLogger(AServiceImpl.class.getName());

    private String name;
    private int level;
    private String property1;
    private String property2;
    private BaseService ref1;

    public AServiceImpl(String name, int level) {
        this.name = name;
        this.level = level;
        LOGGER.info(this.name + "," + this.level);
    }

    @Override
    public void sayHello() {
        LOGGER.info("a service 1 say hello");
    }


}
