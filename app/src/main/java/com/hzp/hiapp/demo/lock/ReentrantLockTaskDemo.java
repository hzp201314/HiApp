package com.hzp.hiapp.demo.lock;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTaskDemo {

    public static void main(String[] args) {

//        testDemo1();
//        testDemo2();
//        testDemo3();
        testDemo4();

    }

    static void testDemo1() {
        //多个线程竞争
        ReentrantLockTask1 task = new ReentrantLockTask1();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                task.doSomeThing();
            }
        };
        for (int i = 0; i < 10; i++) {
            new Thread(runnable).start();
        }
    }

    static void testDemo2() {
        // 可重入锁
        ReentrantLockTask2 task = new ReentrantLockTask2();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                task.doSomeThing();
            }
        };
        for (int i = 0; i < 10; i++) {
            new Thread(runnable).start();
        }
    }

    static void testDemo3() {
        //多个线程多次打印，公平锁与非公平锁
        ReentrantLockTask3 task = new ReentrantLockTask3();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                task.doSomeThing();
            }
        };
        for (int i = 0; i < 10; i++) {
            new Thread(runnable).start();
        }
    }

    static void testDemo4() {
        ReentrantLockTask4 lockTask = new ReentrantLockTask4();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    lockTask.work1();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    lockTask.work2();
                }
            }
        }).start();

        for (int i = 0; i < 10; i++) {
            lockTask.boss();
        }
    }

    /**
     * 演示 多个线程竞争锁的用法
     */
    static class ReentrantLockTask1 {
        ReentrantLock reentrantLock = new ReentrantLock();

        void doSomeThing() {
            String name = Thread.currentThread().getName();
            try {
                reentrantLock.lock();
                System.out.println(name + ":准备好了");
                Thread.sleep(100);
                System.out.println(name + ":买好了");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                reentrantLock.unlock();
            }
        }

    }

    /**
     * 演示 可重入锁
     */
    static class ReentrantLockTask2 {
        ReentrantLock reentrantLock = new ReentrantLock();

        void doSomeThing() {
            String name = Thread.currentThread().getName();
            try {
                reentrantLock.lock();
                System.out.println(name + ":准备好了");
                Thread.sleep(100);
                System.out.println(name + ":买好了");

                reentrantLock.lock();
                System.out.println(name + ":又准备好了");
                Thread.sleep(100);
                System.out.println(name + ":又买好了");

                reentrantLock.lock();
                System.out.println(name + ":又又准备好了");
                Thread.sleep(100);
                System.out.println(name + ":又又买好了");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                reentrantLock.unlock();
                reentrantLock.unlock();
                reentrantLock.unlock();
            }
        }

    }

    /**
     * 演示 多个线程 去打印，每个线程打印张
     * (ReentrantLock 公平锁与非公平锁)
     * 公平锁：交易
     * 非公平锁：synchronized 场景比比皆是
     */
    static class ReentrantLockTask3 {
        //公平锁
//        ReentrantLock lock = new ReentrantLock(true);
        //非公平锁 允许线程插队，可能存在线程饿死情况，导致线程一直等不到执行
        ReentrantLock lock = new ReentrantLock();

        void doSomeThing() {
            String name = Thread.currentThread().getName();
            try {
                //打印两次
                lock.lock();
                System.out.println(name + ":第一次打印");
                Thread.sleep(100);
                lock.unlock();

                lock.lock();
                System.out.println(name + ":第二次打印");

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {

                lock.unlock();

            }
        }

    }

    /**
     * 演示：生产者与消费者，利用ReentrantLock Condition条件对象，能够指定唤醒某个线程去工作
     * <p>
     * 生产者：一个boss 去生产砖，砖的序列号为偶数，那么工人2去搬，奇数号让工人去搬
     * 消费者：两个工人，有砖就搬，没砖就休息
     */
    static class ReentrantLockTask4 {
        private Condition worker1Condition, worker2Condition;
        //公平锁
        ReentrantLock lock = new ReentrantLock(true);
        //砖的序号
        volatile int flag = 0;

        public ReentrantLockTask4() {
            worker1Condition = lock.newCondition();
            worker2Condition = lock.newCondition();
        }

        //工人1搬砖
        void work1() {
            try {
                lock.lock();
                //无砖或偶数砖休息
                if (flag == 0 || flag % 2 == 0) {
                    System.out.println("worker1 无砖可搬，休息会");
                    worker1Condition.await();
                }
                System.out.println("worker1 搬到的砖是：" + flag);
                flag = 0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        //工人2搬砖
        void work2() {
            try {
                lock.lock();
                //无砖或奇数砖休息
                if (flag == 0 || flag % 2 != 0) {
                    System.out.println("worker2 无砖可搬，休息会");
                    worker2Condition.await();
                }
                System.out.println("worker2 搬到的砖是：" + flag);
                flag = 0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        void boss() {
            try {
                lock.lock();
                flag = new Random().nextInt(100);
                if (flag % 2 == 0) {
                    worker2Condition.signal();
                    System.out.println("生产出来了砖，唤醒工人2去搬：" + flag);
                } else {
                    worker1Condition.signal();
                    System.out.println("生产出来了砖，唤醒工人1去搬：" + flag);
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
