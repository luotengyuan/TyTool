package com.lois.tytool.demo;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.lois.tts.TtsJni;
import com.lois.tts.TyTts;
import com.lois.tytool.TyLog;
import com.lois.tytool.TyTool;
import com.lois.tytool.activity.BaseSlidingAppComatActivity;
import com.lois.tytool.basej.io.FileUtils;

/**
 * @author Administrator
 */
public class MainActivity extends BaseSlidingAppComatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    Button btn_test1, btn_test2, btn_test3, btn_test4, btn_test5, btn_test6, btn_test7, btn_test8, btn_test9, btn_test10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TyTool.getInstance().init(this);
        btn_test1 = findViewById(R.id.btn_test1);
        btn_test1.setOnClickListener(this);
        btn_test2 = findViewById(R.id.btn_test2);
        btn_test2.setOnClickListener(this);
        btn_test3 = findViewById(R.id.btn_test3);
        btn_test3.setOnClickListener(this);
        btn_test4 = findViewById(R.id.btn_test4);
        btn_test4.setOnClickListener(this);
        btn_test5 = findViewById(R.id.btn_test5);
        btn_test5.setOnClickListener(this);
        btn_test6 = findViewById(R.id.btn_test6);
        btn_test6.setOnClickListener(this);
        btn_test7 = findViewById(R.id.btn_test7);
        btn_test7.setOnClickListener(this);
        btn_test8 = findViewById(R.id.btn_test8);
        btn_test8.setOnClickListener(this);
        btn_test9 = findViewById(R.id.btn_test9);
        btn_test9.setOnClickListener(this);
        btn_test10 = findViewById(R.id.btn_test10);
        btn_test10.setOnClickListener(this);
    }

    @Override
    protected void initActivity() {

    }

    @Override
    protected void initView() {
        initBackToolbar();
        initSearchToolbar();
        invalidateOptionsMenu();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_test1:
                TyTts.getInstance().init(this);
                break;
            case R.id.btn_test2:
                TyTts.getInstance().startReadThread("你好，我和我的祖国。");
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
}