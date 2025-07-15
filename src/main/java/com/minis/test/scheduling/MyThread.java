package com.minis.test.scheduling;

public class MyThread extends Thread {

    @Override
    public void run() {
        System.out.println("MyThread run...");
        super.run();
    }

    public static void main(String[] args) {
        MyThread myThread = new MyThread();
        myThread.start();
    }
}
