package com.lois.tytool.demo.activity;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lois.tytool.TyLog;
import com.lois.tytool.TyToast;
import com.lois.tytool.demo.R;
import com.lois.tytool.permission.PermissionCallback;
import com.lois.tytool.permission.TyPermissions;

import java.util.List;

/**
 * @Description TODO
 * @Author Luo.T.Y
 * @Date 2022/3/31
 * @Time 20:28
 */
public class PermissionTestActivity2 extends Activity {
    private static final String TAG = PermissionTestActivity2.class.getSimpleName();

    private String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION
            , Manifest.permission.BLUETOOTH, Manifest.permission.CAMERA};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_test);
    }

    public void onClick(View view) {
        TyPermissions.getHandle(this).permissions(permissions).setCallback(new PermissionCallback() {
            @Override
            public void onResult(int requestCode, boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList, @NonNull List<String> dontAskAgainList) {
                TyToast.showShort(allGranted + "");
                TyLog.i(grantedList);
                TyLog.w(deniedList);
                TyLog.e(dontAskAgainList);
            }
        }).setShowDialog(true).request();
    }

    public void onClick1(View view) {
        TyToast.showShort("isGranted: " + TyPermissions.hasPermissions(permissions));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        TyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
