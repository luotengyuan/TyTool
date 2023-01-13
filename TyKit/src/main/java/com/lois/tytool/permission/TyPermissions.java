package com.lois.tytool.permission;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lois.tytool.TyTool;

import java.util.ArrayList;
import java.util.List;

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
     *
     * @param activity Activity对象
     * @return PermissionHandler
     */
    public static PermissionHandler getHandle(Activity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity must not be null!");
        }
        if (activity instanceof FragmentActivity) {
            PermissionFragment permissionFragment = new PermissionFragment();
            FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
            return createPermissionHandler(fragmentManager, permissionFragment, activity);
        } else {
            return createPermissionHandler(null, null, activity);
        }
    }

    /**
     * 通过Fragment创建PermissionHandler
     *
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
     *
     * @param fragmentManager    fragmentManager
     * @param permissionFragment permissionFragment
     * @param activity           activity
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
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionHandler != null) {
            mPermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 获取指定权限集合中未被授权的权限集合
     *
     * @param permissions 指定的权限集合
     * @return 未被授权的权限集合
     */
    public static @NonNull String[] getDeniedPermissions(String ...permissions) {
        List<String> deniedList = new ArrayList<>();
        if (permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(TyTool.getInstance().getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    deniedList.add(permission);
                }
            }
        }
        if (deniedList.size() <= 0) {
            return new String[]{};
        }
        String[] deniedArray = new String[deniedList.size()];
        for (int i = 0; i < deniedList.size(); i++) {
            deniedArray[i] = deniedList.get(i);
        }
        return deniedArray;
    }

    /**
     * 判断一个指定的权限集合是否全部被授权
     *
     * @param permissions 指定的权限集合
     * @return 是否全部被授权
     */
    public static boolean hasPermissions(String ...permissions) {
        if (permissions == null) {
            return false;
        }
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(TyTool.getInstance().getContext(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 打开权限设置界面
     */
    public static void gotoPermissionSetting() {
        Intent manageAppIntent = new Intent();
        manageAppIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        //第二个参数为包名
        Uri uri = Uri.fromParts("package", TyTool.getInstance().getContext().getPackageName(), null);
        manageAppIntent.setData(uri);
        TyTool.getInstance().getContext().startActivity(manageAppIntent);
    }
}
