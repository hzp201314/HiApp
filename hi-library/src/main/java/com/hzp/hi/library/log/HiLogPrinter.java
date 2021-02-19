package com.hzp.hi.library.log;

import androidx.annotation.NonNull;

/**
 * 日志打印接口
 */
public interface HiLogPrinter {
    void print(@NonNull HiLogConfig config, int level, String tag, @NonNull String printString);
}
