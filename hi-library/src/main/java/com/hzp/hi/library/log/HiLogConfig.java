package com.hzp.hi.library.log;

/**
 * 日志配置类
 */
public abstract class HiLogConfig {
    /*日志每行最大长度*/
    static int MAX_LEN = 512;
    /*线程格式化单例(懒汉模式单例)*/
    static HiThreadFormatter HI_THREAD_FORMATTER = new HiThreadFormatter();
    /*堆栈信息格式化单例(懒汉模式单例)*/
    static HiStackTraceFormatter HI_STACK_TRACE_FORMATTER = new HiStackTraceFormatter();

    /*JSON序列化器注入(默认:未注入)*/
    public JsonParser injectJsonParser() {
        return null;
    }

    /*全局Tag(默认:HiLog)*/
    public String getGlobalTag() {
        return "HiLog";
    }

    /*是否启用(默认:启用)*/
    public boolean enable() {
        return true;
    }

    /*是否包含线程信息(默认:不包含)*/
    public boolean includeThread() {
        return false;
    }

    /*堆栈深度(默认:5)*/
    public int stackTraceDepth() {
        return 5;
    }

    /*注册打印器(默认:未注入)*/
    public HiLogPrinter[] printers() {
        return null;
    }

    /*日志对象序列化接口*/
    public interface JsonParser {
        String toJson(Object src);
    }
}
