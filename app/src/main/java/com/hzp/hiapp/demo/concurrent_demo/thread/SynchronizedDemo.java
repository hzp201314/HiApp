package com.hzp.hiapp.demo.concurrent_demo.thread;

import java.util.ArrayList;
import java.util.List;

/**
 * 卖票的并发场景--不设置并发安全措施，会出现多人买到同一张票
 * <p>
 * 如果synchronized加载方法上，为获取到对象锁的线程，只能排队，不能访问
 * 如果 synchronized加在 代码块上面，为获取到对象锁的线程，可以访问同步代码块之外的代码
 * 加在static 方法上面，就相对是给Class对象加锁，由于在jvm中只会存在一份class对象。
 *      所以此时无论是不是同一个java对象，去访问同步访问，都只能排队
 */
public class SynchronizedDemo {
    static List<String> tickets = new ArrayList<>();

    public static void main(String[] args) {

        System.out.println((-1 << 29) & ~((1 << 29) - 1));
        for (int i = 0; i < 5; i++) {
            tickets.add("票_" + (i + 1));
        }

        sellTickets();
    }


    private static void sellTickets() {
        SynchronizedTestDemo testDemo = new SynchronizedTestDemo();
//        SynchronizedTestDemo2 testDemo = new SynchronizedTestDemo2();
//        SynchronizedTestDemo3 testDemo = new SynchronizedTestDemo3();
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //多线程持有同一个对象实例
                    testDemo.printThreadName();
                    //多线程持有不同的对象实例
//                    new SynchronizedTestDemo().printThreadName();
//                    new SynchronizedTestDemo2().printThreadName();
//                    new SynchronizedTestDemo3().printThreadName();
                }
            }).start();
        }
    }


    static class SynchronizedTestDemo {
        // 锁方法。加在方法上。未获取到对象锁的其他线程都不可以访问该方法
        // 如果多线程持有不同的对象实例则任然会产生异常卖票
        synchronized void printThreadName() {
            String name = Thread.currentThread().getName();
            System.out.println("买票人：" + name + "准备好了...");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("买票人：" + name + "买到的票是..." + tickets.remove(0));
        }
    }

    static  class SynchronizedTestDemo2 {
        // 锁Class对象。加在static方法上相当于给类Class对象加锁，哪怕是不同的java对象实例，也需要排队执行。
        // 如果多线程持有不同的对象实例也不会产生异常卖票
        static synchronized void printThreadName() {
            String name = Thread.currentThread().getName();
            System.out.println("买票人：" + name + "准备好了...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("买票人：" + name + "买到的票是..." + tickets.remove(0));
        }
    }

    static class SynchronizedTestDemo3 {
        void printThreadName() {
            String name = Thread.currentThread().getName();
            System.out.println("买票人：" + name + "准备好了...");
            //锁代码块。未获取到对象锁的其他线程可以执行同步代码块之外的代码
            synchronized (this) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("买票人：" + name + "正在买票...");
            }
            System.out.println("买票人：" + name + "买到的票是..." + tickets.remove(0));
        }
    }
}
