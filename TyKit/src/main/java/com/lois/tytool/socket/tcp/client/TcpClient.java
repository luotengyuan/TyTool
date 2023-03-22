package com.lois.tytool.socket.tcp.client;

import com.lois.tytool.TyLog;
import com.lois.tytool.base.stream.coder.IMessage;
import com.lois.tytool.base.stream.coder.IMessageReceiver;
import com.lois.tytool.socket.BaseSocket;
import com.lois.tytool.socket.bean.IpPort;
import com.lois.tytool.socket.tcp.client.listener.TcpClientStateListener;
import com.lois.tytool.socket.tcp.client.manager.TcpClientManager;
import com.lois.tytool.socket.tcp.client.state.ClientState;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * tcp客户端
 * @author Administrator
 */
public class TcpClient extends BaseSocket {
    public static final String TAG = TcpClient.class.getSimpleName();

    /**
     * 目标ip和端口号
     */
    protected IpPort mIpPort;
    protected Socket mSocket;
    protected ClientState mClientState;
    protected TcpConnConfig mTcpConnConfig;
    protected ConnectionThread mConnectionThread;
    protected SendThread mSendThread;
    protected ReceiveThread mReceiveThread;
    protected TcpClientStateListener mTcpClientStateListener;
    private LinkedBlockingQueue<IMessage> mMessageQueue;
    private IMessageReceiver mMessageReceiver = null;
    private byte[] mDataBytes = new byte[10240];

    private TcpClient() {
        super();
    }

    /**
     * 创建tcp连接，需要提供服务器信息
     *
     * @param ipPort
     * @return
     */
    public static TcpClient getTcpClient(IpPort ipPort) {
        return getTcpClient(ipPort, null);
    }

    public static TcpClient getTcpClient(IpPort ipPort, TcpConnConfig tcpConnConfig) {
        TcpClient XTcpClient = TcpClientManager.getTcpClient(ipPort);
        if (XTcpClient == null) {
            XTcpClient = new TcpClient();
            XTcpClient.init(ipPort, tcpConnConfig);
            TcpClientManager.putTcpClient(XTcpClient);
        }
        return XTcpClient;
    }

    /**
     * 根据socket创建client端，目前仅用在socketServer接受client之后
     *
     * @param socket
     * @return
     */
    public static TcpClient getTcpClient(Socket socket, IpPort ipPort) {
        return getTcpClient(socket, ipPort, null);
    }

    public static TcpClient getTcpClient(Socket socket, IpPort ipPort, TcpConnConfig connConfig) {
        if (!socket.isConnected()) {
            throw new IllegalArgumentException("socket is closeed");
        }
        TcpClient xTcpClient = new TcpClient();
        xTcpClient.init(ipPort, connConfig);
        xTcpClient.mSocket = socket;
        xTcpClient.mClientState = ClientState.Connected;
        xTcpClient.onConnectSuccess();
        return xTcpClient;
    }

    public IMessageReceiver getMessageReceiver() {
        return mMessageReceiver;
    }

    public void setMessageReceiver(IMessageReceiver messageReceiver) {
        this.mMessageReceiver = messageReceiver;
    }


    private void init(IpPort ipPort, TcpConnConfig connConfig) {
        this.mIpPort = ipPort;
        mClientState = ClientState.Disconnected;
        if (mTcpConnConfig == null && connConfig == null) {
            mTcpConnConfig = new TcpConnConfig();
        } else if (connConfig != null) {
            mTcpConnConfig = connConfig;
        }
    }

    public synchronized IMessage sendMsg(IMessage message) {
        if (isDisconnected()) {
            TyLog.d(TAG, "发送消息 " + message + "，当前没有tcp连接，先进行连接");
            connect();
        }
        boolean re = enqueueTcpMessage(message);
        if (re) {
            return message;
        }
        return null;
    }

    public synchronized void connect() {
        if (!isDisconnected()) {
            TyLog.d(TAG, "已经连接了或正在连接");
            return;
        }
        TyLog.d(TAG, "tcp connecting");
        setClientState(ClientState.Connecting);//正在连接
        getConnectionThread().start();
    }

    public synchronized Socket getSocket() {
        if (mSocket == null || isDisconnected() || !mSocket.isConnected()) {
            mSocket = new Socket();
            try {
                mSocket.setSoTimeout((int) mTcpConnConfig.getReceiveTimeout());
            } catch (SocketException e) {
//                e.printStackTrace();
            }
        }
        return mSocket;
    }

    public synchronized void disconnect() {
        disconnect("手动关闭tcpclient", null);
    }

    protected synchronized void onErrorDisConnect(String msg, Exception e) {
        if (isDisconnected()) {
            return;
        }
        disconnect(msg, e);
        if (mTcpConnConfig.isReconnect()) {//重连
            connect();
        }
    }

    protected synchronized void disconnect(String msg, Exception e) {
        if (isDisconnected()) {
            return;
        }
        closeSocket();
        getConnectionThread().interrupt();
        getSendThread().interrupt();
        getReceiveThread().interrupt();
        setClientState(ClientState.Disconnected);
        notifyDisconnected(msg, e);
        TyLog.d(TAG, "tcp closed");
    }

    private synchronized boolean closeSocket() {
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
        return true;
    }

    //连接已经连接，接下来的流程，创建发送和接受消息的线程
    private void onConnectSuccess() {
        TyLog.d(TAG, "tcp connect 建立成功");
        setClientState(ClientState.Connected);//标记为已连接
        getSendThread().start();
        getReceiveThread().start();
    }

    /**
     * tcp连接线程
     */
    private class ConnectionThread extends Thread {
        @Override
        public void run() {
            try {
                int localPort = mTcpConnConfig.getLocalPort();
                if (localPort > 0) {
                    if (!getSocket().isBound()) {
                        getSocket().bind(new InetSocketAddress(localPort));
                    }
                }
                getSocket().connect(new InetSocketAddress(mIpPort.getIp(), mIpPort.getPort()),
                        (int) mTcpConnConfig.getConnTimeout());
                TyLog.d(TAG, "创建连接成功,target=" + mIpPort + ",localport=" + localPort);
            } catch (Exception e) {
                TyLog.d(TAG, "创建连接失败,target=" + mIpPort + "," + e);
                onErrorDisConnect("创建连接失败", e);
                return;
            }
            notifyConnected();
            onConnectSuccess();
        }
    }

    public boolean enqueueTcpMessage(final IMessage tcpMsg) {
        if (tcpMsg == null || getMessageQueue().contains(tcpMsg)) {
            return false;
        }
        try {
            getMessageQueue().put(tcpMsg);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected LinkedBlockingQueue<IMessage> getMessageQueue() {
        if (mMessageQueue == null) {
            mMessageQueue = new LinkedBlockingQueue<>();
        }
        return mMessageQueue;
    }

    private class SendThread extends Thread {
        @Override
        public void run() {
            try {
                while (isConnected() && !Thread.interrupted()) {
                    IMessage take = getMessageQueue().take();
                    if (take != null) {
                        byte[] data = take.encode();
                        if (data != null && data.length > 0) {
                            // 粘包打包
                            if (mTcpConnConfig.getStickPackage() != null) {
                                byte[] packageBytes = mTcpConnConfig.getStickPackage().doPack(data, data.length);
                                if (packageBytes != null && packageBytes.length > 0) {
                                    try {
                                        getSocket().getOutputStream().write(packageBytes);
                                        getSocket().getOutputStream().flush();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        onErrorDisConnect("发送消息失败", e);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class ReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                InputStream is = getSocket().getInputStream();
                while (isConnected() && !Thread.interrupted()) {
                    int len = 0;
                    try {
                        if ((len = is.read(mDataBytes)) == -1) {
                            continue;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }
                    final List<byte[]> packages = mTcpConnConfig.getStickPackage().unPack(mDataBytes, len);
                    if (packages != null) {
                        notifyReceive(packages);
                    }
                }
            } catch (Exception e) {
                TyLog.d(TAG, "tcp Receive  error  " + e);
                onErrorDisConnect("接受消息错误", e);
            }
        }
    }

    protected ReceiveThread getReceiveThread() {
        if (mReceiveThread == null || !mReceiveThread.isAlive()) {
            mReceiveThread = new ReceiveThread();
        }
        return mReceiveThread;
    }

    protected SendThread getSendThread() {
        if (mSendThread == null || !mSendThread.isAlive()) {
            mSendThread = new SendThread();
        }
        return mSendThread;
    }

    protected ConnectionThread getConnectionThread() {
        if (mConnectionThread == null || !mConnectionThread.isAlive() || mConnectionThread.isInterrupted()) {
            mConnectionThread = new ConnectionThread();
        }
        return mConnectionThread;
    }

    public ClientState getClientState() {
        return mClientState;
    }

    protected void setClientState(ClientState state) {
        if (mClientState != state) {
            mClientState = state;
        }
    }

    public boolean isDisconnected() {
        return getClientState() == ClientState.Disconnected;
    }

    public boolean isConnected() {
        return getClientState() == ClientState.Connected;
    }

    private void notifyConnected() {
        if (mTcpClientStateListener != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTcpClientStateListener.onConnected(TcpClient.this);
                }
            });
        }
    }

    private void notifyDisconnected(final String msg, final Exception e) {
        if (mTcpClientStateListener != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTcpClientStateListener.onDisconnected(TcpClient.this, msg, e);
                }
            });
        }
    }


    private void notifyReceive(final List<byte[]> packages) {
        if (mMessageReceiver != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; packages != null && i < packages.size(); i++) {
                        mMessageReceiver.onReceiveData(TcpClient.this, mMessageReceiver.decode(packages.get(i)));
                    }
                }
            });
        }
    }

    public IpPort getTargetInfo() {
        return mIpPort;
    }

    public TcpClientStateListener getTcpClientStateListener() {
        return mTcpClientStateListener;
    }

    public void setTcpClientStateListener(TcpClientStateListener tcpClientStateListener) {
        this.mTcpClientStateListener = tcpClientStateListener;
    }

    public void config(TcpConnConfig tcpConnConfig) {
        mTcpConnConfig = tcpConnConfig;
    }

    @Override
    public String toString() {
        return "XTcpClient{" +
                "mIpPort=" + mIpPort + ", state=" + mClientState + ", isConnected=" + isConnected() +
                '}';
    }
}