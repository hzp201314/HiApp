package com.hzp.hiapp.demo.lock;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 利用一个原子类锁，一个用volatile修饰，再多线程的情况下做自增，然后输出最后的值
 */
public class AtomicDemo {

    public static void main(String[] args) throws InterruptedException {
        final AtomicTask task = new AtomicTask();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    task.incrementVolatile();
                    task.incrementAtomic();
                }
            }
        };
        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);
        t1.start();
        t2.start();


        //线程插队，保证线程执行结束再往下执行
        t1.join();
        t2.join();

        System.out.println("原子类的结果：" + task.atomicInteger.get());
        System.out.println("volatile修饰的结果：" + task.volatileCount);
    }

    static class AtomicTask {
        AtomicInteger atomicInteger = new AtomicInteger();
        volatile int volatileCount = 0;

        void incrementAtomic() {
            atomicInteger.getAndIncrement();
        }

        void incrementVolatile() {
            volatileCount++;
        }
    }
}
