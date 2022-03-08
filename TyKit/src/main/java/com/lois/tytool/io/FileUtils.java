package com.lois.tytool.io;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.loader.content.CursorLoader;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description 文件操作工具类
 * @Author Lois
 * @Date 2022/1/21 16:09
 */
public class FileUtils extends com.lois.tytool.base.io.FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    /**
     * 将缓存数据写入到/data/data/<应用程序名>目录上的文件
     *
     * @param fileName 写入文件名
     * @param writeStr : 写入数据
     */
    public static void writeDataFile(Context context, String fileName, String writeStr) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            byte[] bytes = writeStr.getBytes();
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 将/data/data/<应用程序名>目录上的文件读取到缓存 仅使用在Activity中的文件读取操作
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String readDataFile(Context context, String fileName) {
        String readBuf = "";
        FileInputStream fis;
        try {
            fis = context.openFileInput(fileName);
            int length = fis.available();
            byte[] buffer = new byte[length];
            fis.read(buffer);
            readBuf = new String(buffer, "GBK");
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readBuf;
    }

    public static void shareFile(Context context, String title, String filePath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri uri = Uri.parse("file://" + filePath);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(intent, title));
    }

    /**
     * 根据Uri返回文件绝对路径
     * 兼容了file:///开头的 和 content://开头的情况
     */
    public static String getRealFilePathFromUri(final Context context, final Uri uri) {
        if (null == uri) {
            return null;
        }
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 使用系统程序打开文件
     *
     * @param activity Activity
     * @param file     File
     * @throws Exception
     */
    public static void openFile(Activity activity, File file) throws Exception {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), getMimeType(file, activity));
        activity.startActivity(intent);
    }

    /**
     * 获取以后缀名为ID的值
     *
     * @param file     File
     * @param activity Activity
     * @return MimeType字符串
     * @throws Exception
     */
    public static String getMimeType(File file, Activity activity)
            throws Exception {

        String name = file.getName()
                .substring(file.getName().lastIndexOf('.') + 1).toLowerCase();
        int id = activity.getResources().getIdentifier(
                activity.getPackageName() + ":string/" + name, null, null);

        // 特殊处理
        if ("class".equals(name)) {
            return "application/octet-stream";
        }
        if ("3gp".equals(name)) {
            return "video/3gpp";
        }
        if ("nokia-op-logo".equals(name)) {
            return "image/vnd.nok-oplogo-color";
        }
        if (id == 0) {
            throw new Exception("未找到分享该格式的应用");
        }
        return activity.getString(id);
    }

    /**
     * 资源管理器,查找该文件夹下的文件和目录
     *
     * @param folder 文件夹
     * @param filter 文件过滤器
     * @return 符合条件的List
     */
    public static List<HashMap<String, Object>> unrecursionFolder(File folder,
                                                                  FileFilter filter) {
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        // 如果是SD卡路径,不添加父路径
        if (!folder.getAbsolutePath().equals(
                Environment.getExternalStorageDirectory().getAbsolutePath())) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("file", folder.getParentFile());
            map.put("iconType", ICON_TYPE_ROOT);
            list.add(map);
        }
        // 获得文件夹下的所有目录和文件集合
        File[] files = folder.listFiles();
        /** 如果文件夹下没内容,会返回一个null **/
        // 判断适配器是否为空
        if (filter != null) {
            files = folder.listFiles(filter);
        }
        if (files != null && files.length > 0) {
            for (File p : files) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("file", p);
                // 设置图标种类
                if (p.isDirectory()) {
                    map.put("iconType", ICON_TYPE_FOLDER);
                } else {
                    if (p.getAbsolutePath().toLowerCase().matches(MP3_REG)) {
                        map.put("iconType", ICON_TYPE_MP3);
                    } else if (p.getAbsolutePath().toLowerCase()
                            .matches(MTV_REG)) {
                        map.put("iconType", ICON_TYPE_MTV);
                    } else if (p.getAbsolutePath().toLowerCase()
                            .matches(JPG_REG)) {
                        map.put("iconType", ICON_TYPE_JPG);
                    } else {
                        map.put("iconType", ICON_TYPE_FILE);
                    }
                }
                // 添加
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 把uri转为File对象
     *
     * @param activity Activity
     * @param uri      文件Uri
     * @return File对象
     */
    public static File uri2File(Activity activity, Uri uri) {
        if (Build.VERSION.SDK_INT < 11) {
            // 在API11以下可以使用：managedQuery
            String[] proj = {MediaStore.Images.Media.DATA};
            @SuppressWarnings("deprecation")
            Cursor actualimagecursor = activity.managedQuery(uri, proj, null, null,
                    null);
            int actual_image_column_index = actualimagecursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            String img_path = actualimagecursor
                    .getString(actual_image_column_index);
            return new File(img_path);
        } else {
            // 在API11以上：要转为使用CursorLoader,并使用loadInBackground来返回
            String[] projection = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(activity, uri, projection, null,
                    null, null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return new File(cursor.getString(column_index));
        }
    }
}
