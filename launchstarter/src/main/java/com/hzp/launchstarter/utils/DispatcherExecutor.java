package com.hzp.launchstarter.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DispatcherExecutor {
    /**
     * CPU 密集型任务的线程池
     */
    private static ThreadPoolExecutor sCPUThreadPoolExecutor;

    /**
     * IO 密集型任务的线程池
     */
    private static ExecutorService sIOThreadPoolExecutor;

    /**
     * CPU 核数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * 线程池线程数
     */
    public static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 5));

    /**
     * 线程池线程数的最大值
     */
    private static final int MAXIMUM_POOL_SIZE = CORE_POOL_SIZE;

    private static final int KEEP_ALIVE_SECONDS = 5;

    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingDeque<>();

    /**
     * 线程工厂
     */
    private static final DefaultThreadFactory sThredFactory = new DefaultThreadFactory();

    // 一般不会到这里
    private static final RejectedExecutionHandler sHandler = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            Executors.newCachedThreadPool().execute(r);
        }
    };

    /**
     * 获取CPU线程池
     *
     * @return
     */
    public static ThreadPoolExecutor getCPUExecutor() {
        return sCPUThreadPoolExecutor;
    }

    /**
     * 获取IO线程池
     *
     * @return
     */
    public static ExecutorService getIOExecutor() {
        return sIOThreadPoolExecutor;
    }

    /**
     * The default thread factory.
     */
    private static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "TaskDispatcherPool-" +
                    poolNumber.getAndIncrement() +
                    "-Thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    static {
        sCPUThreadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                sPoolWorkQueue, sThredFactory, sHandler);
        sCPUThreadPoolExecutor.allowCoreThreadTimeOut(true);
        sIOThreadPoolExecutor = Executors.newCachedThreadPool(sThredFactory);
    }

}
