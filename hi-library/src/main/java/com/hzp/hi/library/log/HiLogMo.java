package com.hzp.hi.library.log;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 日志MO
 * 构造日志格式实体类
 */
public class HiLogMo {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.CHINA);
    public long timeMillis;
    public int level;
    public String tag;
    public String log;

    public HiLogMo(long timeMillis, int level, String tag, String log) {
        this.timeMillis = timeMillis;
        this.level = level;
        this.tag = tag;
        this.log = log;
    }

    /*格式化时间戳,日志等级,tag,log*/
    public String flattenedLog() {
        return getFlattened() + "\n" + log;
    }

    /*格式化时间戳,日志等级,tag*/
    public String getFlattened() {
        return format(timeMillis) + '|' + level + '|' + tag + "|:";
    }

    /*格式化时间戳*/
    private String format(long timeMillis) {
        return sdf.format(timeMillis);
    }
}
