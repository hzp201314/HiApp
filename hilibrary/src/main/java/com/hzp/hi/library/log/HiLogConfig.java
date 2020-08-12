package com.hzp.hi.library.log;

/**
 * HiLog配置类
 */
public class HiLogConfig {
    //定义日志每行显示最大长度
    static int MAX_LEN = 512;
    //格式化器单例(多次使用)
    static HiThreadFormatter HI_THREAD_FORMATTER = new HiThreadFormatter();
    static HiStackTraceFormatter HI_STACK_TRACE_FORMATTER = new HiStackTraceFormatter();

    //注入Json序列化器
    public JsonParser injectJsonParser() {
        return null;
    }

    public String getGlobalTag() {
        return "HiLog";
    }

    public boolean enable() {
        return true;
    }

    //日志是否包含线程信息，默认不包含false
    public boolean includeThread() {
        return false;
    }

    //堆栈日志深度，默认5
    public int stackTraceDepth() {
        return 5;
    }

    //日志打印器
    public HiLogPrinter[] printers() {
        return null;
    }

    //Json序列化器
    public interface JsonParser {
        String toJson(Object src);
    }
}
