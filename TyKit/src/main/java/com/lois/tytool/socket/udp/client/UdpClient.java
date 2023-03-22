package com.lois.tytool.socket.udp.client;

import com.lois.tytool.TyLog;
import com.lois.tytool.base.stream.coder.IMessage;
import com.lois.tytool.socket.BaseSocket;
import com.lois.tytool.socket.bean.IpPort;
import com.lois.tytool.socket.udp.client.listener.UdpClientStateListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * udp 客户端
 *
 * @author Administrator
 */
public class UdpClient extends BaseSocket {
    private static final String TAG = UdpClient.class.getSimpleName();
    protected UdpClientConfig mUdpClientConfig;
    protected UdpClientStateListener mUdpClientStateListener;
    private DatagramSocket datagramSocket;
    private SendThread sendThread;
    private IpPort mIpPort;

    private UdpClient(IpPort ipPort) {
        super();
        this.mIpPort = ipPort;
    }

    public static UdpClient getUdpClient(IpPort ipPort) {
        UdpClient client = new UdpClient(ipPort);
        client.init();
        return client;
    }

    private void init() {
        mUdpClientConfig = new UdpClientConfig();
    }

    public void closeSocket() {
        if (datagramSocket != null && datagramSocket.isConnected()) {
            datagramSocket.disconnect();
            datagramSocket = null;
        }
    }

    public void sendMsg(IMessage msg) {
        //开启发送线程
        if (!getSendThread().isAlive()) {
            getSendThread().start();
        }
        getSendThread().enqueueUdpMsg(msg);
    }

    private SendThread getSendThread() {
        if (sendThread == null || !sendThread.isAlive()) {
            sendThread = new SendThread();
        }
        return sendThread;
    }

    private DatagramSocket getDatagramSocket() {
        if (datagramSocket != null) {
            return datagramSocket;
        }
        synchronized (lock) {
            if (datagramSocket != null) {
                return datagramSocket;
            }
            try {
                datagramSocket = new DatagramSocket();
                datagramSocket.setSoTimeout((int) mUdpClientConfig.getReceiveTimeout());
            } catch (SocketException e) {
                e.printStackTrace();
                notifyErrorListener("udp create socket error", e);
                datagramSocket = null;
            }
            return datagramSocket;
        }
    }

    private class SendThread extends Thread {
        private LinkedBlockingQueue<IMessage> msgQueue;
        private IMessage sendingMsg;

        protected LinkedBlockingQueue<IMessage> getMsgQueue() {
            if (msgQueue == null) {
                msgQueue = new LinkedBlockingQueue<>();
            }
            return msgQueue;
        }

        protected SendThread setSendingMsg(IMessage sendingMsg) {
            this.sendingMsg = sendingMsg;
            return this;
        }

        public IMessage getSendingMsg() {
            return this.sendingMsg;
        }

        public boolean enqueueUdpMsg(final IMessage tcpMsg) {
            if (tcpMsg == null || getSendingMsg() == tcpMsg
                    || getMsgQueue().contains(tcpMsg)) {
                return false;
            }
            try {
                getMsgQueue().put(tcpMsg);
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }

        public boolean cancel(IMessage packet) {
            return getMsgQueue().remove(packet);
        }

        @Override
        public void run() {
            IMessage msg;
            if (getDatagramSocket() == null) {
                return;
            }
            try {
                while (!Thread.interrupted()
                        && (msg = getMsgQueue().take()) != null) {
                    setSendingMsg(msg);//设置正在发送的
                    TyLog.d(TAG, "udp send msg=" + msg);
                    byte[] encode = msg.encode();
                    if (encode != null && encode.length > 0) {
                        try {
                            DatagramPacket packet = new DatagramPacket(encode, encode.length,
                                    new InetSocketAddress(mIpPort.getIp(), mIpPort.getPort()));
                            try {
                                datagramSocket.send(packet);
                                notifySendedListener(msg);
                            } catch (IOException e) {
                                e.printStackTrace();
                                notifyErrorListener("发送消息失败", e);
                            }
                        } catch (Exception e) {
                            notifyErrorListener("发送消息失败", e);
                            e.printStackTrace();
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void config(UdpClientConfig udpClientConfig) {
        mUdpClientConfig = udpClientConfig;
    }

    public void setUdpClientStateListener(UdpClientStateListener listener) {
        this.mUdpClientStateListener = listener;
    }

    private void notifySendedListener(final IMessage msg) {
        if (mUdpClientStateListener != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mUdpClientStateListener.onSended(UdpClient.this, msg);
                }
            });
        }
    }

    private void notifyErrorListener(final String msg, final Exception e) {
        if (mUdpClientStateListener != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mUdpClientStateListener.onError(UdpClient.this, msg, e);
                }
            });
        }
    }

    public IpPort getIpPort() {
        return mIpPort;
    }

    @Override
    public String toString() {
        return "XUdp{" +
                "datagramSocket=" + datagramSocket +
                '}';
    }
}
