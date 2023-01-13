package com.lois.tytool.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.lois.tytool.TyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 权限管理处理类
 * @Author Luo.T.Y
 * @Date 2022/2/16
 * @Time 20:22
 */
public class PermissionHandler implements PermissionFragment.Callback {
    private static final String TAG = PermissionHandler.class.getSimpleName();

    private Activity mActivity;
    private String[] mPermissions;
    private PermissionFragment mPermissionFragment;
    private PermissionCallback mPermissionCallback;
    private boolean mIsShowDialog = false;

    public PermissionHandler(Activity activity, PermissionFragment permissionFragment) {
        this.mActivity = activity;
        this.mPermissionFragment = permissionFragment;
    }

    /**
     * 设置当权限被拒绝的时候是否弹窗提示用户
     *
     * @param showDialog 为true则弹窗反之不弹窗 默认为true
     * @return 返回 PermissionHandler
     */
    public PermissionHandler setShowDialog(boolean showDialog) {
        this.mIsShowDialog = showDialog;
        return this;
    }

    /**
     * 设置所需要申请的权限
     *
     * @param permissions 需要申请的权限数组
     * @return 返回一个 PermissionHandler 实例用于进行后续操作
     */
    public PermissionHandler permissions(String... permissions) {
        if (permissions != null && permissions.length > 0) {
            this.mPermissions = permissions;
        }
        return this;
    }

    /**
     * 设置所需要申请的权限
     *
     * @param permissions 需要申请的权限列表
     * @return 返回一个 PermissionHandler 实例用于进行后续操作
     */
    public PermissionHandler permissions(List<String> permissions) {
        if (permissions != null && permissions.size() > 0) {
            String[] temp = new String[permissions.size()];
            for (int i = 0; i < permissions.size(); i++) {
                temp[i] = permissions.get(i);
            }
            this.mPermissions = temp;
        }
        return this;
    }

    /**
     * 设置回调
     *
     * @param callback 权限被允许或者拒绝的时候会回调此实例
     *                  实例必须实现 PermissionCallback 方法
     * @return 返回 PermissionHandler 实例
     */
    public PermissionHandler setCallback(PermissionCallback callback) {
        this.mPermissionCallback = callback;
        return this;
    }

    /**
     * 开始请求权限
     */
    public void request() {
        request(0);
    }

    /**
     * 开始请求权限
     *
     * @param requestCode 请求号
     */
    public void request(int requestCode) {
        if (mPermissions == null || mPermissions.length <= 0) {
            Log.e(TAG, "Please set permission first.");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!requestPermissions(mPermissions, requestCode)) {
                return;
            }
        }
        //通知权限监听回调
        publish(requestCode, true, null);
    }

    private boolean requestPermissions(String[] permissions, int requestCode) {
        String[] deniedPermissions = TyPermissions.getDeniedPermissions(permissions);
        if (deniedPermissions.length > 0) {
            if (mPermissionFragment != null) {
                //注册回调
                mPermissionFragment.registerCallback(requestCode, this);
                mPermissionFragment.postRequestPermissions(deniedPermissions, requestCode);
            } else {
                ActivityCompat.requestPermissions(mActivity, deniedPermissions, requestCode);
            }
            return false;
        }
        return true;
    }

    private void publish(int requestCode, boolean allGranted, List<String> deniedPermissions) {
        List<String> grantedList = new ArrayList<>();
        List<String> deniedList = new ArrayList<>();
        List<String> dontAskAgainList = new ArrayList<>();
        for (String permission : mPermissions) {
            boolean isDenied = false;
            for (int i = 0; deniedPermissions != null && i < deniedPermissions.size(); i++) {
                if (permission.equals(deniedPermissions.get(i))) {
                    isDenied = true;
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)) {
                        dontAskAgainList.add(permission);
                    }
                    break;
                }
            }
            if (isDenied) {
                deniedList.add(permission);
            } else {
                grantedList.add(permission);
            }
        }
        if (dontAskAgainList.size() > 0 && mIsShowDialog) {
            showDialogTips(dontAskAgainList, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }
        if (mPermissionCallback != null) {
            mPermissionCallback.onResult(requestCode, allGranted, grantedList, deniedList, dontAskAgainList);
        }
    }

    /**
     * 提供权限的回调接口 此方法为内部接口 用户不应该调用此方法
     *
     * @param requestCode  请求号
     * @param permissions  权限
     * @param grantResults 授权结果
     */
    @Override
    public void onRequestPermissionsCallback(final int requestCode, final String[] permissions, final int[] grantResults) {
        //保证运行在主线程
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        });
    }

    /**
     * 当不是 FragmentActivity 请求权限时，需要主动在调用的 Activity 重写 onRequestPermissionsResult 方法，并且调用该方法把结果发布
     *
     * @param requestCode  请求号
     * @param permissions  权限
     * @param grantResults 授权结果
     */
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        List<String> deniedList = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            int grant = grantResults[i];
            if (i >= permissions.length) {
                break;
            }
            String permission = permissions[i];
            if (grant == PackageManager.PERMISSION_DENIED) {
                deniedList.add(permission);
            }
        }
        if (deniedList.size() <= 0) {
            publish(requestCode, true, null);
        } else {
            publish(requestCode, false, deniedList);
        }
    }

    /**
     * 当用户拒绝权限时调用前往设置界面的提醒窗口
     *
     * @param permissions 被拒绝的权限
     * @param onDenied 取消点击事件
     */
    private void showDialogTips(List<String> permissions, DialogInterface.OnClickListener onDenied) {
        StringBuilder permissionName = new StringBuilder();
        for (String permission : permissions) {
            permissionName.append("\n[").append(permission).append("]");
        }
        AlertDialog alertDialog = new AlertDialog.Builder(mActivity).setTitle("权限被禁用").setMessage(
                String.format("您拒绝了相关权限，无法正常使用本功能。\n请前往设置中启用以下权限：%s", permissionName.toString()
                )).setCancelable(false).
                setNegativeButton("返回", onDenied).
                setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent manageAppIntent = new Intent();
                        manageAppIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        //第二个参数为包名
                        Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
                        manageAppIntent.setData(uri);
                        mActivity.startActivity(manageAppIntent);
                    }
                }).create();
        alertDialog.show();
    }
}
