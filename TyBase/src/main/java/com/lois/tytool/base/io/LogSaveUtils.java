package com.lois.tytool.base.io;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.*;

/**
 * @Description Log日志保存工具类
 * @Author Luo.T.Y
 * @Date 2017-09-22
 * @Time 11:32
 */
public class LogSaveUtils {

    private static final HashMap<String, BlockingQueue<String>> LOG_MAP = new HashMap<>();
    public static StringBuffer sb = new StringBuffer();
    private static ScheduledExecutorService executorService = null;
    private static ScheduledFuture<?> scheduledFuture = null;
    private static SaveRunnable saveRunnable = new SaveRunnable();
    private static long logTime = 0;
    private static long saveTime = 0;

    public static void saveLog(String path, String msg){
        if (path.trim() != null && msg.trim() != null){
            if (executorService == null) {
                executorService = newSingleThreadScheduledExecutor();
                scheduledFuture = executorService.scheduleAtFixedRate(saveRunnable, 0, 10, TimeUnit.MILLISECONDS);
            } else if (scheduledFuture == null || scheduledFuture.isCancelled()) {
                scheduledFuture = executorService.scheduleAtFixedRate(saveRunnable, 0, 10, TimeUnit.MILLISECONDS);
            } else if (System.currentTimeMillis() - saveTime > 5000 && System.currentTimeMillis() - logTime > 1000 && !isLogMapEmpty()) {
                if (scheduledFuture != null) {
                    scheduledFuture.cancel(true);
                }
                scheduledFuture = executorService.scheduleAtFixedRate(saveRunnable, 0, 10, TimeUnit.MILLISECONDS);
                System.err.println("触发日志保存线程异常重启机制");
            }
            try {
                if (!LOG_MAP.containsKey(path)){
                    BlockingQueue<String> queue = new LinkedBlockingQueue<>();
                    LOG_MAP.put(path, queue);
                }
                LOG_MAP.get(path).put(msg);
                logTime = System.currentTimeMillis();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isLogMapEmpty() {
        try {
            for (Map.Entry<String, BlockingQueue<String>> entry : LOG_MAP.entrySet()) {
                BlockingQueue<String> queue = entry.getValue();
                if (queue != null && !queue.isEmpty()) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private static class SaveRunnable implements Runnable {
        private int cancelCount = 0;
        @Override
        public void run() {
            try {
                int tempCount = 0;
                if (LOG_MAP.size() > 0){
                    for (Map.Entry<String, BlockingQueue<String>> entry : LOG_MAP.entrySet()) {
                        String path = entry.getKey();
                        BlockingQueue<String> queue = entry.getValue();
                        if (!queue.isEmpty()){
                            int size = 50;
                            int cacheSize = queue.size();
                            if (cacheSize <= 0){
                                continue;
                            } else if (cacheSize < size){
                                size = cacheSize;
                            }
                            tempCount += cacheSize;
                            int idx1 = 0;
                            while (idx1 < size){
                                try {
                                    String msg = queue.take();
                                    if (!msg.endsWith("\n")){
                                        sb.append(msg);
                                        sb.append("\n");
                                    } else {
                                        sb.append(msg);
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                idx1++;
                            }
                            if (sb != null && sb.length() > 0){
                                if (!FileUtils.isFileExist(path)){
                                    FileUtils.createNewFile(path);
                                }
                                FileUtils.appendWriteFile(path, sb.toString(), "UTF-8");
                                saveTime = System.currentTimeMillis();
                            }
                            sb.delete(0, sb.length());
                        }
                    }
                }
                if (tempCount <= 0 && scheduledFuture != null && !scheduledFuture.isCancelled()) {
                    cancelCount++;
                    if (cancelCount >= 1000) {
                        scheduledFuture.cancel(true);
                        cancelCount = 0;
                    }
                } else {
                    cancelCount = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}