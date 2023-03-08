package com.lois.tytool.demo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lois.tytool.TyToast;
import com.lois.tytool.activity.BaseActivity;
import com.lois.tytool.base.stream.coder.IMessage;
import com.lois.tytool.base.stream.coder.StringMessage;
import com.lois.tytool.base.stream.coder.StringMessageReceiver;
import com.lois.tytool.demo.R;
import com.lois.tytool.serialport.TySerialPort;
import com.lois.tytool.socket.bean.IpPort;
import com.lois.tytool.socket.udp.client.UdpClient;
import com.lois.tytool.socket.udp.client.listener.UdpClientStateListener;

/**
 * @Description TODO
 * @Author Luo.T.Y
 * @Date 2023/3/1
 * @Time 20:08
 */
public class SerialPortTestActivity extends BaseActivity {
    private static final String TAG = SerialPortTestActivity.class.getSimpleName();

    TextView tv_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv_text = findViewById(R.id.tv_text);
    }

    @Override
    protected int initContentView() {
        return R.layout.activity_serial_port_test;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {

    }

    public void send(View view) {

    }
}
