package com.lois.tytool.basej.secert;

import java.io.File;
import java.io.FileInputStream;
import java.util.zip.CRC32;

/**
 * @Description CRC校验工具
 * @Author Luo.T.Y
 * @Date 2017-09-22
 * @Time 19:52
 */
public class CrcUtils {

    public static long getCrc32(File file)throws Exception{
        FileInputStream inputStream = new FileInputStream(file);
        CRC32 crc32 = new CRC32();
        int bufSize = 8192;
        int read=0;
        byte[] buffer = new byte[bufSize];
        while ((read=inputStream.read(buffer))>0){
            crc32.update(buffer,0,read);
        }
        inputStream.close();
        return crc32.getValue();
    }

    public static long getCrc32(byte[] data) {
        if (data == null) {
            throw new NullPointerException();
        }
        return getCrc32(data, 0, data.length);
    }

    public static long getCrc32(byte[] data, int offset, int length) {
        CRC32 crc32 = new CRC32();
        crc32.update(data, offset, length);
        return crc32.getValue();
    }
}
