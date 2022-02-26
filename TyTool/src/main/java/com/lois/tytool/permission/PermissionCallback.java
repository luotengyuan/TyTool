package com.lois.tytool.permission;

import androidx.annotation.NonNull;

/**
 * @Description 订阅回调接口
 * @Author Luo.T.Y
 * @Date 2022/2/16
 * @Time 19:26
 */
public interface PermissionCallback {
    void onResult(int requestCode, boolean allGranted, @NonNull String[] grantedList, @NonNull String[] deniedList);
}
