package com.minis.test.scheduling;

public class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("MyRunnable run...");
    }

    public static void main(String[] args) {
        MyRunnable myRunnable = new MyRunnable();
        Thread thread = new Thread(myRunnable);
        thread.start();
    }
}
