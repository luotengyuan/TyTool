package com.lois.tytool.demo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lois.tytool.TyToast;
import com.lois.tytool.activity.BaseActivity;
import com.lois.tytool.base.stream.coder.IMessage;
import com.lois.tytool.base.stream.coder.StringMessage;
import com.lois.tytool.demo.R;
import com.lois.tytool.socket.bean.IpPort;
import com.lois.tytool.socket.udp.client.UdpClient;
import com.lois.tytool.socket.udp.client.listener.UdpClientStateListener;

/**
 * @Description TODO
 * @Author Luo.T.Y
 * @Date 2023/3/1
 * @Time 20:08
 */
public class UdpClientTestActivity extends BaseActivity {
    private static final String TAG = UdpClientTestActivity.class.getSimpleName();

    TextView tv_text;
    UdpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv_text = findViewById(R.id.tv_text);
        IpPort ipPort = new IpPort("255.255.255.255", 2233);
        client = UdpClient.getUdpClient(ipPort);
        client.setUdpClientStateListener(new UdpClientStateListener() {
            @Override
            public void onSended(UdpClient client, IMessage msg) {
                StringMessage message = (StringMessage) msg;
                TyToast.showShort(client.hashCode() + " " + message.getDataObj());
            }

            @Override
            public void onError(UdpClient client, String msg, Exception e) {

            }
        });
    }

    @Override
    protected int initContentView() {
        return R.layout.activity_udp_client_test;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {

    }

    public void send(View view) {
        if (client != null) {
            client.sendMsg(new StringMessage("uhfahhusafa互杀发送给发过火好分行ahfhajfhafha"));
        }
    }
}
