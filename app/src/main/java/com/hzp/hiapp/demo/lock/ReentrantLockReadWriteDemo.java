package com.hzp.hiapp.demo.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 利用ReentrantLockReadWrite 实现多人在线查看与编辑文档
 * <p>
 * 读锁：共享锁
 * 写锁：排他锁
 */
public class ReentrantLockReadWriteDemo {
    public static void main(String[] args) {
        final ReentrantReadWriteLockTask task = new ReentrantReadWriteLockTask();

        for (int i = 0; i < 3; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    task.read();
                }
            }).start();
        }

        for (int i = 0; i < 3; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    task.write();
                }
            }).start();
        }
    }

    static class ReentrantReadWriteLockTask {
        private final ReentrantReadWriteLock.ReadLock readLock;
        private final ReentrantReadWriteLock.WriteLock writeLock;
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        ReentrantReadWriteLockTask() {
            readLock = lock.readLock();
            writeLock = lock.writeLock();
        }

        void read() {
            String name = Thread.currentThread().getName();
            try {
                readLock.lock();
                System.out.println("线程" + name + "正在读取数据...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                readLock.unlock();
                System.out.println("线程" + name + "释放读锁...");
            }
        }

        void write() {
            String name = Thread.currentThread().getName();
            try {
                writeLock.lock();
                System.out.println("线程" + name + "正在写入数据...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                writeLock.unlock();
                System.out.println("线程" + name + "释放写锁...");
            }
        }
    }
}
