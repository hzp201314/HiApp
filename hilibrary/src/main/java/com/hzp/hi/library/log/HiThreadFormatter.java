package com.hzp.hi.library.log;

/**
 * HiLog线程日志格式化
 */
public class HiThreadFormatter implements HiLogFormatter<Thread>{
    @Override
    public String format(Thread data) {
        return "Thread:"+data.getName();
    }
}
