package com.hzp.hi.library.log;

/**
 * HiLog格式化接口
 * @param <T> 任意类型
 */
public interface HiLogFormatter<T> {
    String format(T data);
}
