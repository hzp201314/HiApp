package com.hzp.hi.library.log;

import android.util.Log;

import androidx.annotation.NonNull;

import static com.hzp.hi.library.log.HiLogConfig.MAX_LEN;

/**
 * 控制台打印器
 */
public class HiConsolePrinter implements HiLogPrinter {
    @Override
    public void print(@NonNull HiLogConfig config, int level, String tag, @NonNull String printString) {
        //长度
        int len = printString.length();
        //行数
        int countOfSub = len / MAX_LEN;
        //遍历循环打印日志
        if (countOfSub > 0) {
            int index = 0;
            for (int i = 0; i < countOfSub; i++) {
                Log.println(level, tag, printString.substring(index, index + MAX_LEN));
                index += MAX_LEN;
            }
            //打印最后一行(不整除的时候)
            if (index != len) {
                Log.println(level, tag, printString.substring(index, len));
            }
        } else {
            Log.println(level, tag, printString);
        }
    }
}
