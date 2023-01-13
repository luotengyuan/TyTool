package com.lois.tytool.permission;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * @Description 订阅回调接口
 * @Author Luo.T.Y
 * @Date 2022/2/16
 * @Time 19:26
 */
public interface PermissionCallback {
    void onResult(int requestCode, boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList, @NonNull List<String> dontAskAgainList);
}
