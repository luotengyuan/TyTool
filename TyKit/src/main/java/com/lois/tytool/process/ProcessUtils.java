package com.lois.tytool.process;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

/**
 * @Description 执行命令行语句静态方法封装
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class ProcessUtils extends com.lois.tytool.base.process.ProcessUtils {

    /**
     * 换行符
     */
    private static final String BREAK_LINE = "\n";
    /**
     * 执行退出命令
     */
    private static final byte[] COMMAND_EXIT = "\nexit\n".getBytes();
    /**
     * 错误缓冲
     */
    private static byte[] BUFFER = new byte[32];

    /**
     * 执行命令
     *
     * @param params 命令参数
     *               <pre> eg: "/system/bin/ping", "-c", "4", "-s", "100","www.qiujuer.net"</pre>
     * @return 执行结果
     */
    public static String execute(String... params) {
        Process process = null;
        StringBuilder sbReader = null;

        BufferedReader bReader = null;
        InputStreamReader isReader = null;

        InputStream in = null;
        InputStream err = null;
        OutputStream out = null;

        try {
            //创建一个进程,新的进程将调用command()的命令和参数
            process = new ProcessBuilder()
                    .command(params)
                    .start();

            out = process.getOutputStream();
            in = process.getInputStream();
            err = process.getErrorStream();

            out.write(COMMAND_EXIT);
            out.flush();

            //实际上是开辟了一个新的线程（子线程）来处理任务，主线程处于阻塞状态，直到子线程执行完毕！
            //当子线程的输入或者输出流满的时候，是不能够自动清空的，需要人为的去清空，所以我们一般要增加一个线程，用来专门的清空输入流
            process.waitFor();

            isReader = new InputStreamReader(in);
            bReader = new BufferedReader(isReader);

            String s;
            if ((s = bReader.readLine()) != null) {
                sbReader = new StringBuilder();
                sbReader.append(s);
                sbReader.append(BREAK_LINE);
                while ((s = bReader.readLine()) != null) {
                    sbReader.append(s);
                    sbReader.append(BREAK_LINE);
                }
            }

            while ((err.read(BUFFER)) > 0) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllStream(out, err, in, isReader, bReader);
            if (process != null) {
                processDestroy(process);
                process = null;
            }
        }

        if (sbReader == null) {
            return null;
        } else {
            return sbReader.toString();
        }
    }

    /**
     * 关闭所有流
     *
     * @param out      输出流
     * @param err      错误流
     * @param in       输入流
     * @param isReader 输入流封装
     * @param bReader  输入流封装
     */
    private static void closeAllStream(OutputStream out, InputStream err, InputStream in, InputStreamReader isReader, BufferedReader bReader) {
        if (out != null) {
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (err != null) {
            try {
                err.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (in != null) {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (isReader != null) {
            try {
                isReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (bReader != null) {
            try {
                bReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过Android底层实现进程关闭
     *
     * @param process 进程
     */
    private static void killProcess(Process process) {
        int pid = getProcessId(process);
        if (pid != 0) {
            try {
                //android kill process
                android.os.Process.killProcess(pid);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    process.destroy();
                } catch (Exception ex) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 通过Android底层实现进程关闭
     *
     * @param pid 进程
     */
    public static boolean killProcess(int pid) {
        if (pid != 0) {
            try {
                //android kill process
                android.os.Process.killProcess(pid);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 销毁进程
     *
     * @param process 进程
     */
    private static void processDestroy(Process process) {
        if (process != null) {
            try {
                //判断是否正常退出
                if (process.exitValue() != 0) {
                    killProcess(process);
                }
            } catch (Exception e) {
                killProcess(process);
                e.printStackTrace();
            }
        }
    }

    /**
     * whether this process is named with processName
     *
     * @param context     上下文
     * @param processName 进程名
     * @return <ul>
     * return whether this process is named with processName
     * <li>if context is null, return false</li>
     * <li>if {@link ActivityManager#getRunningAppProcesses()} is null,
     * return false</li>
     * <li>if one process of
     * {@link ActivityManager#getRunningAppProcesses()} is equal to
     * processName, return true, otherwise return false</li>
     * </ul>
     */
    public static boolean isNamedProcess(Context context, String processName) {
        if (context == null || TextUtils.isEmpty(processName)) {
            return false;
        }

        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfoList = manager
                .getRunningAppProcesses();
        if (processInfoList == null) {
            return true;
        }

        for (ActivityManager.RunningAppProcessInfo processInfo : manager
                .getRunningAppProcesses()) {
            if (processInfo.pid == pid
                    && processName.equalsIgnoreCase(processInfo.processName)) {
                return true;
            }
        }
        return false;
    }
}
