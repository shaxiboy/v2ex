package com.hjx.v2ex;

/**
 * Created by shaxiboy on 2017/3/8 0008.
 */

public class Test {

    @org.junit.Test
    public void test() {
        final ThreadLocal<String> mStringThreadLocal = new ThreadLocal<>();
        mStringThreadLocal.set("hjx.com");
        System.out.println(Thread.currentThread().getName() + ": " + mStringThreadLocal.get());
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                mStringThreadLocal.set("droidyue.com");
                System.out.println(Thread.currentThread().getName() + ": " + mStringThreadLocal.get());
            }
        };
        t.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + ": " + mStringThreadLocal.get());
    }

}
