package com.lois.tytool.demo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lois.tytool.activity.BaseActivity;
import com.lois.tytool.base.stream.coder.StringMessageReceiver;
import com.lois.tytool.demo.R;
import com.lois.tytool.socket.udp.server.UdpServer;
import com.lois.tytool.socket.udp.server.listener.UdpServerStateListener;

/**
 * @Description TODO
 * @Author Luo.T.Y
 * @Date 2023/3/1
 * @Time 20:08
 */
public class UdpServerTestActivity extends BaseActivity {
    private static final String TAG = UdpServerTestActivity.class.getSimpleName();

    TextView tv_text;
    UdpServer server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv_text = findViewById(R.id.tv_text);
    }

    @Override
    protected int initContentView() {
        return R.layout.activity_udp_server_test;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {

    }

    public void connect(View view) {
        tv_text.append("-------------\n");
        server = UdpServer.getUdpClient(2233);
        server.setMessageReceiver(new StringMessageReceiver() {
            @Override
            public void onReceiveData(Object target, String bean) {
                tv_text.append(bean + "\n");
            }
        });
        server.setUdpServerStateListener(new UdpServerStateListener() {
            @Override
            public void onStarted(UdpServer XUdp) {
                tv_text.append("开启成功\n");
            }

            @Override
            public void onStoped(UdpServer XUdp) {
                tv_text.append("停止\n");
            }

            @Override
            public void onError(UdpServer client, String msg, Exception e) {
                tv_text.append("错误\n");
            }
        });
        server.startUdpServer();
    }
}
