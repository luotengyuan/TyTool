package com.lois.tytool.demo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lois.tytool.TyLog;
import com.lois.tytool.activity.BaseActivity;
import com.lois.tytool.base.stream.coder.StringMessage;
import com.lois.tytool.base.stream.coder.StringMessageReceiver;
import com.lois.tytool.base.stream.sticky.GeneralStickPackage;
import com.lois.tytool.demo.R;
import com.lois.tytool.socket.tcp.client.TcpClient;
import com.lois.tytool.socket.bean.IpPort;
import com.lois.tytool.socket.tcp.client.TcpConnConfig;
import com.lois.tytool.socket.tcp.client.listener.TcpClientStateListener;

/**
 * @Description TODO
 * @Author Luo.T.Y
 * @Date 2023/3/1
 * @Time 20:08
 */
public class TcpClientTestActivity extends BaseActivity {
    private static final String TAG = TcpClientTestActivity.class.getSimpleName();

    TextView tv_text;
    TcpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv_text = findViewById(R.id.tv_text);
    }

    @Override
    protected int initContentView() {
        return R.layout.activity_tcp_client_test;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {

    }

    public void connect(View view) {
        IpPort ipPort = new IpPort("192.168.139.135", 2222);
        client = TcpClient.getTcpClient(ipPort);
        client.setTcpClientStateListener(new TcpClientStateListener() {
            @Override
            public void onConnected(TcpClient client) {
                tv_text.append("连接成功\n");
            }

            @Override
            public void onDisconnected(TcpClient client, String msg, Exception e) {
                tv_text.append("连接断开\n");
            }
        });
        client.setMessageReceiver(new StringMessageReceiver() {
            @Override
            public void onReceiveData(Object target, String bean) {
                TyLog.e(bean);
                tv_text.append(bean + "\n");
            }
        });
//        client.config(new TcpConnConfig().setStickPackage(new GeneralStickPackage()));
        client.connect();
    }

    public void send(View view) {
        if (client != null) {
            client.sendMsg(new StringMessage("uhfahhusafa互杀发送给发过火好分行ahfhajfhafha"));
        }
    }
}
