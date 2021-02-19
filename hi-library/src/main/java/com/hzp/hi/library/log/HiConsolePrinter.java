package com.hzp.hi.library.log;

import android.util.Log;

import androidx.annotation.NonNull;

import static com.hzp.hi.library.log.HiLogConfig.MAX_LEN;

/**
 * 控制台打印器
 */
public class HiConsolePrinter implements HiLogPrinter{
    @Override
    public void print(@NonNull HiLogConfig config, int level, String tag, @NonNull String printString) {
        int len = printString.length();
        //打印行数
        int countOfSub = len / MAX_LEN;
        if (countOfSub > 0) {
            StringBuilder log = new StringBuilder();
            //索引
            int index = 0;
            for (int i = 0; i < countOfSub; i++) {
                log.append(printString.substring(index, index + MAX_LEN));
                index += MAX_LEN;
            }
            //最后一行
            if (index != len) {
                log.append(printString.substring(index, len));
            }
            Log.println(level, tag, log.toString());
        }else {
            Log.println(level, tag, printString);
        }
    }
}
