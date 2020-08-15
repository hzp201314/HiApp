package com.hzp.hi.library.log;

import androidx.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * HiLog文件打引器
 */
public class HiFilePrinter implements HiLogPrinter {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private final String logPath;
    private final long retentionTime;
    private LogWriter writer;
    private volatile PrintWorker worker;
    private static HiFilePrinter instance;

    public static synchronized HiFilePrinter getInstance(String logPath, long retentionTime) {
        if (instance == null) {
            instance = new HiFilePrinter(logPath, retentionTime);
        }
        return instance;
    }

    private HiFilePrinter(String logPath, long retentionTime) {
        this.logPath = logPath;
        this.retentionTime = retentionTime;
        this.writer = new LogWriter();
        this.worker = new PrintWorker();
        cleanExpiredLog();
    }

    @Override
    public void print(@NonNull HiLogConfig config, int level, String tag, @NonNull String printString) {
        long timeMillis = System.currentTimeMillis();
        if (!worker.isRunning()) {
            worker.start();
        }
        worker.put(new HiLogMo(timeMillis,level,tag,printString));
    }

    private void doPrint(HiLogMo logMo){
        String lastFileName =writer.getPreFileName();
        if(lastFileName==null){
            String newFileName=genFileName();
            if (writer.isReady()){
                writer.close();
            }

            if(!writer.ready(newFileName)){
                return;
            }
        }
        writer.append(logMo.flattenedLog());
    }

    /**
     * 创建文件名称
     * @return 文件名称
     */
    private String genFileName(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    /**
     * 清除过期log
     */
    private void cleanExpiredLog(){
        if(retentionTime<=0){
            return;
        }

        long currentTimeMillis = System.currentTimeMillis();
        File logDir = new File(logPath);
        File[] files = logDir.listFiles();
        if(files==null){
            return;
        }

        for (File file : files) {
            if(currentTimeMillis-file.lastModified() > retentionTime){
                file.delete();
            }
        }
    }



    private class PrintWorker implements Runnable {
        private BlockingQueue<HiLogMo> logs = new LinkedBlockingQueue<>();
        private volatile boolean running;

        /**
         * 将log放入打印队列
         *
         * @param log 要被打印的log
         */
        void put(HiLogMo log) {
            try {
                logs.put(log);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * 判断工作线程是否还在运行中
         *
         * @return true 在运行
         */
        boolean isRunning() {
            synchronized (this) {
                return running;
            }
        }

        /**
         * 启动工作线程
         */
        void start() {
            synchronized (this) {
                EXECUTOR.execute(this);
                running = true;
            }
        }

        @Override
        public void run() {
            HiLogMo log;
            try {
                while (true) {
                    log = logs.take();
                    doPrint(log);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                synchronized (this) {
                    running = false;
                }
            }
        }
    }

    private class LogWriter {
        private String preFileName;
        private File logFile;
        private BufferedWriter bufferedWriter;

        boolean isReady() {
            return bufferedWriter != null;
        }

        String getPreFileName() {
            return preFileName;
        }

        boolean ready(String newFileName) {
            preFileName = newFileName;
            logFile = new File(logPath, newFileName);

            //当log文件不存在事创建log文件
            if (!logFile.exists()) {
                try {
                    File parent = logFile.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    preFileName = null;
                    logFile = null;
                    return false;
                }
            }

            try {
                bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
            } catch (IOException e) {
                e.printStackTrace();
                preFileName = null;
                logFile = null;
                return false;
            }
            return true;
        }

        /**
         * 关闭bufferedWriter
         */
        boolean close(){
            if(bufferedWriter!=null){
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }finally {
                    bufferedWriter=null;
                    preFileName=null;
                    logFile=null;
                }
            }
            return true;
        }

        /**
         * 将log写入文件
         *
         * @param flattenedLog 格式化后的log
         */
        void append(String flattenedLog){
            try {
                bufferedWriter.write(flattenedLog);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
