package com.hzp.hi.library.log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 日志管理类
 */
public class HiLogManager {
    /*日志配置类*/
    private HiLogConfig config;
    /*日志管理类单例*/
    private static HiLogManager instance;
    /*日志打印器列表*/
    private List<HiLogPrinter> printers = new ArrayList<>();

    public HiLogManager(HiLogConfig config, HiLogPrinter[] printers) {
        this.config = config;
        this.printers.addAll(Arrays.asList(printers));
    }

    public static HiLogManager getInstance() {
        return instance;
    }

    public static void init(@NonNull HiLogConfig config, HiLogPrinter... printers) {
        instance = new HiLogManager(config, printers);
    }

    public HiLogConfig getConfig() {
        return config;
    }

    public List<HiLogPrinter> getPrinters() {
        return printers;
    }

    public void addPrinter(HiLogPrinter printer) {
        printers.add(printer);
    }

    public void removePrinter(HiLogPrinter printer) {
        if (printers != null) {
            printers.remove(printer);
        }
    }
}
