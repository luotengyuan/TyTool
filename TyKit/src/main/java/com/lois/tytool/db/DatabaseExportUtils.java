package com.lois.tytool.db;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.lois.tytool.config.Config;
import com.lois.tytool.io.FileUtils;

import java.io.IOException;

/**
 * @Description 应用数据库导出工具类
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public final class DatabaseExportUtils {
    private static final String TAG = DatabaseExportUtils.class.getSimpleName();

    /**
     * Don't let anyone instantiate this class.
     */
    private DatabaseExportUtils() {
        throw new Error("Do not need instantiate!");
    }

    /**
     * 开始导出数据 此操作比较耗时,建议在线程中进行
     *
     * @param context      上下文
     * @param targetFile   目标文件
     * @param databaseName 要拷贝的数据库文件名
     * @return 是否倒出成功
     */
    public boolean startExportDatabase(Context context, String targetFile, String databaseName) throws IOException {
        if (Config.IS_DEBUG) {
            Log.d(TAG, "start export ...");
        }
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (Config.IS_DEBUG) {
                Log.w(TAG, "cannot find SDCard");
            }
            return false;
        }
        String sourceFilePath = Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/databases/" + databaseName;
        String destFilePath = Environment.getExternalStorageDirectory() + (TextUtils.isEmpty(targetFile) ? (context.getPackageName() + ".db") : targetFile);
        boolean isCopySuccess = FileUtils.copyFile(sourceFilePath, destFilePath);
        if (Config.IS_DEBUG) {
            if (isCopySuccess) {
                Log.d(TAG, "copy database file success. target file : " + destFilePath);
            } else {
                Log.w(TAG, "copy database file failure");
            }
        }
        return isCopySuccess;
    }
}
