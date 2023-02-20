package com.lois.tytool.socket.udp.client.listener;

import com.lois.tytool.socket.udp.client.Udp;
import com.lois.tytool.socket.udp.client.bean.UdpMsg;

/**
 */
public interface UdpClientListener {
    void onStarted(Udp XUdp);

    void onStoped(Udp XUdp);

    void onSended(Udp XUdp, UdpMsg udpMsg);

    void onReceive(Udp client, UdpMsg udpMsg);

    void onError(Udp client, String msg, Exception e);

    class SimpleUdpClientListener implements UdpClientListener {

        @Override
        public void onStarted(Udp XUdp) {

        }

        @Override
        public void onStoped(Udp XUdp) {

        }

        @Override
        public void onSended(Udp XUdp, UdpMsg udpMsg) {

        }

        @Override
        public void onReceive(Udp client, UdpMsg msg) {

        }

        @Override
        public void onError(Udp client, String msg, Exception e) {

        }
    }

}
