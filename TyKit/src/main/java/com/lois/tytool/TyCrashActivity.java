package com.lois.tytool;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import com.lois.tytool.config.Config;

/**
 * @Description 默认的异常捕获显示类
 * @Author Luo.T.Y
 * @Date 2017-09-22
 * @Time 11:32
 */
public class TyCrashActivity extends Activity implements View.OnClickListener {

    protected String stackTrace = "";
    protected String deviceInfo = "";

    private TextView tv_ty_crash_text;
    private Button btn_ty_crash_send_to_developer, btn_ty_crash_restart, btn_ty_crash_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stackTrace = TyCrashHelper.getStackTraceFromIntent(getIntent());
        deviceInfo = TyCrashHelper.getDeviceInfo();
        setContentView(R.layout.activity_ty_crash);
        tv_ty_crash_text = findViewById(R.id.tv_ty_crash_text);
        btn_ty_crash_send_to_developer = findViewById(R.id.btn_ty_crash_send_to_developer);
        btn_ty_crash_send_to_developer.setOnClickListener(this);
        btn_ty_crash_restart = findViewById(R.id.btn_ty_crash_restart);
        btn_ty_crash_restart.setOnClickListener(this);
        btn_ty_crash_exit = findViewById(R.id.btn_ty_crash_exit);
        btn_ty_crash_exit.setOnClickListener(this);
        if (Config.IS_DEBUG) {
            btn_ty_crash_send_to_developer.setVisibility(View.GONE);
            tv_ty_crash_text.setText(deviceInfo + stackTrace);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_ty_crash_send_to_developer) {
            sendToQQ();
        } else if (view.getId() == R.id.btn_ty_crash_restart) {
            restartApp();
        } else if (view.getId() == R.id.btn_ty_crash_exit) {
            exitApp();
        }
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    private void restartApp() {
        //必须调用getApplicationContext()才能获得正确的包名
        String packageName = getApplicationContext().getPackageName();
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void exitApp() {
        finish();
        //不退出的话，如果已进入主界面就奔溃，可能会一直循环弹出奔溃提示
        //ActivityManager.getInstance().exitApp();
    }

    private void sendToQQ() {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("crash_log", stackTrace);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "错误日志已复制，请粘贴发送！", Toast.LENGTH_LONG).show();
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=1524883878&version=1&src_type=web&web_src=http:://wpa.b.qq.com");
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //延迟杀掉软件，以便Toast显示完毕
        CountDownTimer timer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                exitApp();
            }
        };
        timer.start();
    }
}
