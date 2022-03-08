package com.lois.tytool.permission;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Description 用于权限管理的隐藏Fragment
 * @Author Luo.T.Y
 * @Date 2022/2/16
 * @Time 19:23
 */
public class PermissionFragment extends Fragment {
    private HashMap<Integer, Callback> callbacks = new HashMap<>();
    private ArrayList<Runnable> runnables = new ArrayList<>();

    public void registerCallback(int requestCode, Callback callback) {
        callbacks.put(requestCode, callback);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("PermissionFragment", "延迟执行");
        while (!runnables.isEmpty()) {
            runnables.remove(0).run();
        }
    }

    public void postRequestPermissions(final String[] permissions, final int requestCode) {
        if (isAdded()) {
            requestPermissions(permissions, requestCode);
        } else {
            runnables.add(new Runnable() {
                @Override
                public void run() {
                    requestPermissions(permissions, requestCode);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        if (callbacks.containsKey(requestCode)) {
            Callback callback = callbacks.get(requestCode);
            if (callback != null) {
                callback.onRequestPermissionsCallback(requestCode, permissions, grantResults);
            }
        }
    }

    /**
     * @Description 内部回调使用，用户不应该调用或者实现此接口
     * @Author Luo.T.Y
     * @Date 2022/2/16
     * @Time 19:26
     */
    public interface Callback {
        /**
         * 权限请求结果回调接口
         * @param requestCode
         * @param permissions
         * @param grantResults
         */
        void onRequestPermissionsCallback(final int requestCode, String[] permissions, int[] grantResults);
    }
}
