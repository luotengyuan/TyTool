package com.lois.tytool.basej.network.http;

import com.lois.tytool.basej.constant.FileConstants;

import java.io.InputStream;
import java.net.URL;

/**
 * @Description 资源文件加载工具类
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class ResourceUtils {

    /**
     * 获取资源文件的输入流
     * @param fileName
     * @return
     */
    public static InputStream getResFile(String fileName){
        InputStream in = null;
        if(fileName != null){
            if(fileName.startsWith(FileConstants.FILE_SEPARATOR)){
                in = ResourceUtils.class.getResourceAsStream(fileName);
            }else{
                in = ClassLoader.getSystemClassLoader().getResourceAsStream(fileName);
            }
        }
        return in;
    }

    /**
     * 获取资源文件的url
     * @param fileName
     * @return
     */
    public static URL getUrlFile(String fileName){
        URL url = null;
        if(fileName != null){
            if(fileName.startsWith(FileConstants.FILE_SEPARATOR)){
                url = ResourceUtils.class.getResource(fileName);
            }else{
                url = ClassLoader.getSystemClassLoader().getResource(fileName);
            }
        }
        return url;
    }


    /**
     * 获取class类的编译路径
     * @return
     */
    public static String getClassPath(){
        URL url = Thread.currentThread().getContextClassLoader().getResource(FileConstants.BLANK_STR);
        return url != null ? url.getPath() : "";
    }

    /**
     * 获取项目的路径
     * @return
     */
    public static String getProjectPath(){
        return System.getProperty(FileConstants.USER_DIR);
    }
}
