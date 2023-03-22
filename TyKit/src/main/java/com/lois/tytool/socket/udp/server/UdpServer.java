package com.lois.tytool.socket.udp.server;

import com.lois.tytool.TyLog;
import com.lois.tytool.base.stream.coder.IMessageReceiver;
import com.lois.tytool.socket.BaseSocket;
import com.lois.tytool.socket.udp.server.listener.UdpServerStateListener;
import com.lois.tytool.socket.udp.server.manager.UdpServerManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;

/**
 * udp 服务器
 * @author Administrator
 */
public class UdpServer extends BaseSocket {
    private static final String TAG = UdpServer.class.getSimpleName();
    protected UdpServerConfig mUdpClientConfig;
    protected UdpServerStateListener mUdpServerStateListener;
    private DatagramSocket datagramSocket;
    private ReceiveThread receiverThread;
    private int mPort;
    private IMessageReceiver mMessageReceiver = null;

    private UdpServer(int port) {
        super();
        mPort = port;
    }

    public static UdpServer getUdpClient(int port) {
        UdpServer client = new UdpServer(port);
        client.init();
        return client;
    }

    private void init() {
        mUdpClientConfig = new UdpServerConfig();
    }

    public IMessageReceiver getMessageReceiver() {
        return mMessageReceiver;
    }

    public void setMessageReceiver(IMessageReceiver messageReceiver) {
        this.mMessageReceiver = messageReceiver;
    }

    public void closeSocket() {
        if (datagramSocket != null && datagramSocket.isConnected()) {
            datagramSocket.disconnect();
            datagramSocket = null;
        }
    }

    public void startUdpServer() {
        if (!getReceiveThread().isAlive()) {
            getReceiveThread().start();
            TyLog.d(TAG, "udp server started");
        }
    }

    public void stopUdpServer() {
        getReceiveThread().interrupt();
        notifyStopListener();
    }

    public boolean isUdpServerRuning() {
        return getReceiveThread().isAlive();
    }

    private ReceiveThread getReceiveThread() {
        if (receiverThread == null || !receiverThread.isAlive()) {
            receiverThread = new ReceiveThread();
        }
        return receiverThread;
    }

    private DatagramSocket getDatagramSocket() {
        if (datagramSocket != null) {
            return datagramSocket;
        }
        synchronized (lock) {
            if (datagramSocket != null) {
                return datagramSocket;
            }
            int localPort = mPort;
            try {
                if (localPort > 0) {
                    datagramSocket = UdpServerManager.getUdpSocket(localPort);
                    if (datagramSocket == null) {
                        datagramSocket = new DatagramSocket(localPort);
                        UdpServerManager.putUdpSocket(datagramSocket);
                    }
                } else {
                    datagramSocket = new DatagramSocket();
                }
                datagramSocket.setSoTimeout((int) mUdpClientConfig.getReceiveTimeout());
            } catch (SocketException e) {
                e.printStackTrace();
                notifyErrorListener("udp create socket error", e);
                datagramSocket = null;
            }
            return datagramSocket;
        }
    }

    private class ReceiveThread extends Thread {
        @Override
        public void run() {
            if (getDatagramSocket() == null) {
                return;
            }
            byte[] buff = new byte[1024];
            DatagramPacket pack = new DatagramPacket(buff, buff.length);
            notifyStartListener();
            while (!Thread.interrupted()) {
                try {
                    getDatagramSocket().receive(pack);
                    byte[] res = Arrays.copyOf(buff, pack.getLength());
                    TyLog.d(TAG, "udp receive byte=" + Arrays.toString(res));
                    notifyReceiveListener(res);
                } catch (IOException e) {
                    if (!(e instanceof SocketTimeoutException)) {//不是超时报错
                        notifyErrorListener(e.getMessage(), e);
                        notifyStopListener();
                    }
                }
            }
        }
    }

    public void config(UdpServerConfig udpClientConfig) {
        mUdpClientConfig = udpClientConfig;
    }

    public void setUdpServerStateListener(UdpServerStateListener listener) {
        this.mUdpServerStateListener = listener;
    }

    private void notifyReceiveListener(final byte[] data) {
        if (mMessageReceiver != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMessageReceiver.onReceiveData(UdpServer.this, mMessageReceiver.decode(data));
                }
            });
        }
    }

    private void notifyStartListener() {
        if (mUdpServerStateListener != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mUdpServerStateListener.onStarted(UdpServer.this);
                }
            });
        }
    }

    private void notifyStopListener() {
        if (mUdpServerStateListener != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mUdpServerStateListener.onStoped(UdpServer.this);
                }
            });
        }
    }

    private void notifyErrorListener(final String msg, final Exception e) {
        if (mUdpServerStateListener != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mUdpServerStateListener.onError(UdpServer.this, msg, e);
                }
            });
        }
    }

    @Override
    public String toString() {
        return "XUdp{" +
                "datagramSocket=" + datagramSocket +
                '}';
    }
}
