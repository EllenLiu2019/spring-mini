package com.minis.boot;


public class ApplicationProperties {

    private boolean allowCircularReferences;
    private boolean lazyInitialization;
    private WebApplicationType webApplicationType;

    WebApplicationType getWebApplicationType() {
        return this.webApplicationType;
    }
    public void setWebApplicationType(WebApplicationType webApplicationType) {
        this.webApplicationType = webApplicationType;
    }

    public boolean isAllowCircularReferences() {
        return this.allowCircularReferences;
    }

}
