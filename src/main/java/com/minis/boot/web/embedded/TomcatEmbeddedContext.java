package com.minis.boot.web.embedded;

import org.apache.catalina.core.StandardContext;


class TomcatEmbeddedContext extends StandardContext {

    private TomcatStarter starter;


    void setStarter(TomcatStarter starter) {
        this.starter = starter;
    }


    TomcatStarter getStarter() {
        return this.starter;
    }

}
