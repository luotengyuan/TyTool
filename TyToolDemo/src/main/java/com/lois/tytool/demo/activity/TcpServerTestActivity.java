package com.lois.tytool.demo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lois.tytool.activity.BaseActivity;
import com.lois.tytool.base.stream.coder.StringMessage;
import com.lois.tytool.base.stream.coder.StringMessageReceiver;
import com.lois.tytool.base.stream.sticky.GeneralStickPackage;
import com.lois.tytool.demo.R;
import com.lois.tytool.socket.tcp.client.TcpClient;
import com.lois.tytool.socket.tcp.server.TcpServer;
import com.lois.tytool.socket.tcp.server.TcpServerConfig;
import com.lois.tytool.socket.tcp.server.listener.TcpServerStateListener;

import java.io.ObjectOutputStream;

/**
 * @Description TODO
 * @Author Luo.T.Y
 * @Date 2023/3/1
 * @Time 20:08
 */
public class TcpServerTestActivity extends BaseActivity {
    private static final String TAG = TcpServerTestActivity.class.getSimpleName();

    TextView tv_text;
    TcpServer server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv_text = findViewById(R.id.tv_text);
    }

    @Override
    protected int initContentView() {
        return R.layout.activity_tcp_server_test;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {

    }

    public void connect(View view) {
        tv_text.append("-------------\n");
        server = TcpServer.getTcpServer(2222);
        server.setMessageReceiver(new StringMessageReceiver() {
            @Override
            public void onReceiveData(Object target, String bean) {
                TcpClient client = (TcpClient) target;
                tv_text.append(bean + "\n");
            }
        });
        server.config((TcpServerConfig) new TcpServerConfig().setStickPackage(new GeneralStickPackage()));
        server.setTcpServerSatteListener(new TcpServerStateListener() {
            @Override
            public void onCreated(TcpServer server) {
                tv_text.append("创建成功\n");
            }

            @Override
            public void onListened(TcpServer server) {
                tv_text.append("已开启监听\n");
            }

            @Override
            public void onAccept(TcpServer server, TcpClient tcpClient) {
                tv_text.append("收到客户端连接\n");
            }

            @Override
            public void onClientClosed(TcpServer server, TcpClient tcpClient, String msg, Exception e) {
                tv_text.append("客户端连接断开\n");
            }

            @Override
            public void onServerClosed(TcpServer server, String msg, Exception e) {
                tv_text.append("服务器连接断开\n");
            }
        });
        server.startServer();
    }

    public void send(View view) {
        if (server != null) {
            server.sendMsgToAll(new StringMessage("123456789"));
            server.sendMsgToAll(new StringMessage("123456789"));
            server.sendMsgToAll(new StringMessage("123456789"));
        }
    }
}
