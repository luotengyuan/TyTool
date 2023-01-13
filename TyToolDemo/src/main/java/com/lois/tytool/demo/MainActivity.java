package com.lois.tytool.demo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.lois.tytool.TyLog;
import com.lois.tytool.TyToast;
import com.lois.tytool.TyTool;
import com.lois.tytool.activity.BaseSlidingAppComatActivity;
import com.lois.tytool.base.string.StringUtils;
import com.lois.tytool.permission.PermissionCallback;
import com.lois.tytool.permission.TyPermissions;
import com.lois.tytool.serialport.TySerialPort;
import com.lois.tytool.tts.TyTts;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentMap;

import com.lois.tytool.dialog.builder.EditTextDialogBuilder;
import com.lois.tytool.dialog.builder.ListDialogBuilder;
import com.lois.tytool.dialog.builder.ProgressDialogBuilder;
import com.lois.tytool.dialog.core.DialogBuilder;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Administrator
 */
public class MainActivity extends BaseSlidingAppComatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.btn_test1)
    Button btn_test1;
    @BindView(R.id.btn_test2)
    Button btn_test2;
    @BindView(R.id.btn_test3)
    Button btn_test3;
    @BindView(R.id.btn_test4)
    Button btn_test4;
    @BindView(R.id.btn_test5)
    Button btn_test5;
    @BindView(R.id.btn_test6)
    Button btn_test6;
    @BindView(R.id.btn_test7)
    Button btn_test7;
    @BindView(R.id.btn_test8)
    Button btn_test8;
    @BindView(R.id.btn_test9)
    Button btn_test9;
    @BindView(R.id.btn_test10)
    Button btn_test10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        btn_test1 = findViewById(R.id.btn_test1);
//        btn_test1.setOnClickListener(this);
//        btn_test2 = findViewById(R.id.btn_test2);
//        btn_test2.setOnClickListener(this);
//        btn_test3 = findViewById(R.id.btn_test3);
//        btn_test3.setOnClickListener(this);
//        btn_test4 = findViewById(R.id.btn_test4);
//        btn_test4.setOnClickListener(this);
//        btn_test5 = findViewById(R.id.btn_test5);
//        btn_test5.setOnClickListener(this);
//        btn_test6 = findViewById(R.id.btn_test6);
//        btn_test6.setOnClickListener(this);
//        btn_test7 = findViewById(R.id.btn_test7);
//        btn_test7.setOnClickListener(this);
//        btn_test8 = findViewById(R.id.btn_test8);
//        btn_test8.setOnClickListener(this);
//        btn_test9 = findViewById(R.id.btn_test9);
//        btn_test9.setOnClickListener(this);
//        btn_test10 = findViewById(R.id.btn_test10);
//        btn_test10.setOnClickListener(this);
    }

    @Override
    protected int initContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {
//        initBackToolbar();
//        initSearchToolbar();
//        invalidateOptionsMenu();
    }

    @OnClick({R.id.btn_test1, R.id.btn_test2, R.id.btn_test3, R.id.btn_test4, R.id.btn_test5
            , R.id.btn_test6, R.id.btn_test7, R.id.btn_test8, R.id.btn_test9, R.id.btn_test10})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_test1:
                break;
            case R.id.btn_test2:
                break;
            case R.id.btn_test3:
                break;
            case R.id.btn_test4:
                break;
            case R.id.btn_test5:
                break;
            case R.id.btn_test6:
                break;
            case R.id.btn_test7:
                break;
            case R.id.btn_test8:
                break;
            case R.id.btn_test9:
                break;
            case R.id.btn_test10:
                break;
            default:
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}