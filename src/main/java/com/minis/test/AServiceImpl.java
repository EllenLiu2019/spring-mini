package com.minis.test;


public class AServiceImpl implements AService {

    private String name;
    private int level;
    private String property1;
    private String property2;

    public AServiceImpl(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public void setProperty1(String property1) {
        this.property1 = property1;
    }
    public void setProperty2(String property2) {
        this.property2 = property2;
    }
    @Override
    public void sayHello() {
        System.out.println("a service " + this + " 1 say hello");
    }

    @Override
    public String toString() {
        return "AServiceImpl{" +
                "name='" + name + '\'' +
                ", level=" + level +
                ", property1='" + property1 + '\'' +
                ", property2='" + property2 + '\'' +
                '}';
    }
}
