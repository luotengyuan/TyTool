package com.lois.tytool.base.constant;

import java.io.File;

/**
 * @Description FileConstants
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class FileConstants {

    /**********************************************分隔符常量************************************************/
    /**
     * 点字符
     */
    public static final String POINT_STR = ".";
    /**
     * 空字符
     */
    public static final String BLANK_STR = "";
    /**
     * 空格字符
     */
    public static final String SPACE_STR = " ";
    /**
     * linux下换行符
     */
    public static final String NEWLINE_STR_LINUX = "\n";
    /**
     * Windows下换行符
     */
    public static final String NEWLINE_STR_WINDOWS = "\n\r";
    /**
     * 文件路径分隔符
     */
    public static final String FILE_SEPARATOR = File.separator;
    /**
     * 正斜线切割符
     */
    public static final String SLASH_SEPARATOR = "/";
    /**
     * 反斜线切割符
     */
    public static final String BACK_SLASH_SEPARATOR = "\\";
    /**
     * 左中括号
     */
    public static final String BRACKET_LEFT = "[";
    /**
     * 右中括号
     */
    public static final String BRACKET_RIGHT = "]";
    /**
     * 下划线
     */
    public static final String UNDERLINE = "_";
    /**
     * 中横杆
     */
    public static final String MINUS_STR = "-";



    /**********************************************编码格式************************************************/
    /**
     * UTF-8编码
     */
    public static final String ENCODE_UTF_8 = "UTF-8";
    public static final String ENCODE_GBK = "GBK";


    /**********************************************文件后缀************************************************/
    /**
     * xls文件后缀
     */
    public static final String EXCEL_XLS = ".xls";
    /**
     * xlsx文件后缀
     */
    public static final String EXCEL_XLSX = ".xlsx";
    /**
     * png文件后缀
     */
    public static final String IMAGE_PNG = ".png";
    /**
     * jpg文件后缀
     */
    public static final String IMAGE_JPG = ".jpg";
    /**
     * gz文件后缀
     */
    public static final String GZ_FILE = ".gz";
    /**
     * zip文件后缀
     */
    public static final String ZIP_FILE = ".zip";
    /**
     * jar文件后缀
     */
    public static final String JAR_FILE = ".jar";
    /**
     * class文件后缀
     */
    public static final String CLASS_FILE = ".class";
    /**
     * 文件协议
     */
    public static final String FILE_PROTOCOL = "file";


    /**********************************************io流************************************************/
    /**
     * 缓冲区大小 1024
     */
    public static final int BUFFER_1024 = 1024;
    /**
     * 缓冲区大小 512
     */
    public static final int BUFFER_512 = 512;
    /**
     * user.dir
     */
    public static final String USER_DIR = "user.dir";

    /**********************************************tesseract for java语言字库************************************************/
    /**
     * eng
     */
    public static final String ENG = "eng";
    /**
     * chi_sim
     */
    public static final String CHI_SIM = "chi_sim";
}
