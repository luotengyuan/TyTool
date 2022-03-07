package com.lois.tytool.permission;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lois.tytool.TyTool;

/**
 * @Description 权限工具类
 * @Author Luo.T.Y
 * @Date 2022/2/16
 * @Time 20:07
 */
public class TyPermissions {

    /**
     * 最后一个权限处理操作
     */
    private static PermissionHandler mPermissionHandler;

    /**
     * 通过Activity创建PermissionHandler
     * @param activity Activity对象
     * @return PermissionHandler
     */
    public static PermissionHandler getHandle(Activity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity must not be null!");
        }
        if (activity instanceof FragmentActivity) {
            PermissionFragment permissionFragment = new PermissionFragment();
            FragmentManager fragmentManager = ((FragmentActivity)activity).getSupportFragmentManager();
            return createPermissionHandler(fragmentManager, permissionFragment, activity);
        } else {
            return createPermissionHandler(null, null, activity);
        }
    }

    /**
     * 通过Fragment创建PermissionHandler
     * @param fragment Fragment
     * @return PermissionHandler
     */
    public static PermissionHandler getHandle(Fragment fragment) {
        if (fragment == null) {
            throw new IllegalArgumentException("Fragment must not be null!");
        }
        FragmentActivity activity = fragment.getActivity();
        if (activity == null) {
            throw new IllegalArgumentException("Activity must not be null!");
        }
        PermissionFragment permissionFragment = new PermissionFragment();
        FragmentManager fragmentManager = fragment.getChildFragmentManager();
        return createPermissionHandler(fragmentManager, permissionFragment, activity);
    }

    /**
     * 创建 PermissionHandler 实例
     * @param fragmentManager fragmentManager
     * @param permissionFragment permissionFragment
     * @param activity activity
     * @return PermissionHandler 实例
     */
    private static PermissionHandler createPermissionHandler(FragmentManager fragmentManager, PermissionFragment permissionFragment, Activity activity) {
        if (fragmentManager != null && permissionFragment != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(permissionFragment, PermissionFragment.class.getSimpleName());
            fragmentTransaction.commit();
            mPermissionHandler = new PermissionHandler(activity, permissionFragment);
            return mPermissionHandler;
        } else {
            mPermissionHandler = new PermissionHandler(activity, null);
            return mPermissionHandler;
        }
    }
    /**
     * 当不是 FragmentActivity 请求权限时，需要主动在调用的 Activity 重写 onRequestPermissionsResult 方法，并且调用该方法把结果发布
     *
     * @param requestCode  请求号
     * @param permissions  权限
     * @param grantResults 授权结果
     */
    public static void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionHandler != null) {
            mPermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 判断一个指定的权限是否被授权
     * @param permission  指定的权限
     * @return 是否被授权
     */
    public static boolean isGranted(String permission) {
        return ContextCompat.checkSelfPermission(TyTool.getInstance().getContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }
}
