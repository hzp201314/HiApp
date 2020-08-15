package com.hzp.hi.library.log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * HiLog管理类
 */
public class HiLogManager {
    private HiLogConfig config;
    private static HiLogManager instance;
    //打印器集合
    private List<HiLogPrinter> printers = new ArrayList<>();

    //构造器
    private HiLogManager(HiLogConfig config, HiLogPrinter[] printers) {
        this.config = config;
        this.printers.addAll(Arrays.asList(printers));
    }

    //获取HiLogManager单例类
    public static HiLogManager getInstance() {
        return instance;
    }

    //初始化
    public static void init(@NonNull HiLogConfig config, HiLogPrinter... printers) {
        instance = new HiLogManager(config, printers);
    }

    //获取HiLog配置
    public HiLogConfig getConfig() {
        return config;
    }

    //获取HiLog打印器集合
    public List<HiLogPrinter> getPrinters() {
        return printers;
    }

    //添加打印器
    public void addPrinter(HiLogPrinter printer) {
        this.printers.add(printer);
    }

    //移除打印器
    public void removePrinter(HiLogPrinter printer) {
        if (printers != null) {
            printers.remove(printer);
        }
    }


}
