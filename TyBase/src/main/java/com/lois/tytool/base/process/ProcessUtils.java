package com.lois.tytool.base.process;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 执行命令行语句静态方法封装
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class ProcessUtils {

    /**
     * 换行符
     */
    private static final String BREAK_LINE = "\n";

    /**
     * 获取进程命令执行打印出来的信息
     * @return
     */
    public static List<String> exec(String command){
        Runtime r = Runtime.getRuntime();
        BufferedReader in = null;
        Process pro = null;
        List<String> lists = new ArrayList<>();
        try {
            pro = r.exec(command);
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line;
            while((line = in.readLine()) != null){
                lists.add(line);
            }
        } catch (IOException e) {
            lists = null;
            e.printStackTrace();
        } finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(pro != null){
                pro.destroy();
            }
        }

        return lists;
    }

    /**
     * 执行exec指令
     * @param cmds
     * @return
     */
    public static String exec(String... cmds) {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("su");
            DataOutputStream os = new DataOutputStream(proc.getOutputStream());
            proc.getInputStream();
            for (String c : cmds){
                if (!c.endsWith("\n")){
                    c += "\n";
                }
                os.writeBytes(c);
            }
            os.writeBytes("exit\n");
            os.flush();
            InputStream is = proc.getInputStream();
            InputStreamReader isReader = new InputStreamReader(is);
            BufferedReader bReader = new BufferedReader(isReader);
            StringBuilder sbReader = new StringBuilder();
            String s;
            if ((s = bReader.readLine()) != null) {
                sbReader.append(s);
                sbReader.append(BREAK_LINE);
                while ((s = bReader.readLine()) != null) {
                    sbReader.append(s);
                    sbReader.append(BREAK_LINE);
                }
            }
            return sbReader.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取进程的ID
     *
     * @param process 进程
     * @return
     */
    public static int getProcessId(Process process) {
        String str = process.toString();
        try {
            int i = str.indexOf("=") + 1;
            int j = str.indexOf("]");
            str = str.substring(i, j);
            return Integer.parseInt(str);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
