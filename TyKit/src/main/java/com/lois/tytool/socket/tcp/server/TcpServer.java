package com.lois.tytool.socket.tcp.server;

import com.lois.tytool.TyLog;
import com.lois.tytool.base.stream.coder.IMessage;
import com.lois.tytool.base.stream.coder.IMessageReceiver;
import com.lois.tytool.socket.BaseSocket;
import com.lois.tytool.socket.tcp.client.TcpClient;
import com.lois.tytool.socket.bean.IpPort;
import com.lois.tytool.socket.tcp.client.listener.TcpClientStateListener;
import com.lois.tytool.socket.tcp.server.listener.TcpServerStateListener;
import com.lois.tytool.socket.tcp.server.manager.TcpServerManager;
import com.lois.tytool.socket.tcp.server.state.ServerState;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * tcp服务端
 * @author Administrator
 */
public class TcpServer extends BaseSocket implements TcpClientStateListener {
    private static final String TAG = TcpServer.class.getSimpleName();

    protected int port;
    protected ServerState mServerState;
    protected ServerSocket mServerSocket;
    protected Map<IpPort, TcpClient> mXTcpClients;
    protected ListenThread mListenThread;
    protected TcpServerConfig mTcpServerConfig;
    protected TcpServerStateListener mTcpServerStateListener;

    private IMessageReceiver mMessageReceiver = null;

    private TcpServer() {
        super();
    }

    public static TcpServer getTcpServer(int port) {
        TcpServer xTcpServer = TcpServerManager.getTcpServer(port);
        if (xTcpServer == null) {
            xTcpServer = new TcpServer();
            xTcpServer.init(port);
            TcpServerManager.putTcpServer(xTcpServer);
        }
        return xTcpServer;
    }

    private void init(int port) {
        this.port = port;
        setServerState(ServerState.Closed);
        mXTcpClients = new LinkedHashMap<>();
        if (mTcpServerConfig == null) {
            mTcpServerConfig = new TcpServerConfig();
        }
    }

    public IMessageReceiver getMessageReceiver() {
        return mMessageReceiver;
    }

    public void setMessageReceiver(IMessageReceiver messageReceiver) {
        this.mMessageReceiver = messageReceiver;
    }

    /**
     * 开启tcpserver
     */
    public void startServer() {
        if (!getListenThread().isAlive()) {
            TyLog.d(TAG, "tcp server启动ing ");
            getListenThread().start();
        }
    }

    public void stopServer() {
        stopServer("手动关闭tcpServer", null);
    }

    protected void stopServer(String msg, Exception e) {
        getListenThread().interrupt();//关闭listen
        setServerState(ServerState.Closed);
        if (closeSocket()) {
            for (TcpClient client : mXTcpClients.values()) {
                if (client != null) {
                    client.disconnect();
                }
            }
            notifyTcpServerClosed(msg, e);
        }
        TyLog.d(TAG, "tcp server closed");
    }

    private boolean closeSocket() {
        if (mServerSocket != null && !mServerSocket.isClosed()) {
            try {
                mServerSocket.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean sendMsgToAll(IMessage msg) {
        boolean re = true;
        for (TcpClient client : mXTcpClients.values()) {
            if (client.sendMsg(msg) == null) {
                re = false;
            }
        }
        return re;
    }

    public boolean sendMsg(IMessage msg, TcpClient client) {
        return client.sendMsg(msg) != null;
    }

    @Override
    public void onConnected(TcpClient client) {
        //no callback,ignore
    }

    @Override
    public void onDisconnected(TcpClient client, String msg, Exception e) {
        notifyTcpClientClosed(client, msg, e);
    }

    class ListenThread extends Thread {
        @Override
        public void run() {
            Socket socket;
            while (!Thread.interrupted()) {
                try {
                    TyLog.d(TAG, "tcp server listening");
                    socket = getServerSocket().accept();
                    IpPort ipPort = new IpPort(socket.getInetAddress().getHostAddress(), socket.getPort());
                    TcpClient xTcpClient = TcpClient.getTcpClient(socket, ipPort,
                            mTcpServerConfig);//创建一个client，接受和发送消息
                    notifyTcpServerAccept(xTcpClient);
                    xTcpClient.setTcpClientStateListener(TcpServer.this);
                    xTcpClient.setMessageReceiver(mMessageReceiver);
                    mXTcpClients.put(ipPort, xTcpClient);
                } catch (IOException e) {
                    TyLog.d(TAG, "tcp server listening error:" + e);
                    e.printStackTrace();
                    stopServer("监听失败", e);
                }
            }
        }
    }

    protected ListenThread getListenThread() {
        if (mListenThread == null || !mListenThread.isAlive()) {
            mListenThread = new ListenThread();
        }
        return mListenThread;
    }

    protected ServerSocket getServerSocket() {
        if (mServerSocket == null || mServerSocket.isClosed()) {
            try {
                mServerSocket = new ServerSocket(port);
                setServerState(ServerState.Created);
                notifyTcpServerCreate();
                setServerState(ServerState.Listening);
                notifyTcpServerLinten();
            } catch (IOException e) {
                e.printStackTrace();
                stopServer("创建失败", e);
            }
        }
        return mServerSocket;
    }

    public void setTcpServerSatteListener(TcpServerStateListener listener) {
        this.mTcpServerStateListener = listener;
    }

    private void notifyTcpServerCreate() {
        if (mTcpServerStateListener != null) {
            runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTcpServerStateListener.onCreated(TcpServer.this);
                    }
                });
        }
    }

    private void notifyTcpServerLinten() {
        if (mTcpServerStateListener != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTcpServerStateListener.onListened(TcpServer.this);
                }
            });
        }
    }

    private void notifyTcpServerAccept(final TcpClient client) {
        if (mTcpServerStateListener != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTcpServerStateListener.onAccept(TcpServer.this, client);
                }
            });
        }
    }

    private void notifyTcpServerReceive(final TcpClient client, final IMessage tcpMsg) {
        if (mMessageReceiver != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMessageReceiver.onReceiveData(client, tcpMsg);
                }
            });
        }
    }

    private void notifyTcpClientClosed(final TcpClient client, final String msg, final Exception e) {
        if (mTcpServerStateListener != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTcpServerStateListener.onClientClosed(TcpServer.this, client, msg, e);
                }
            });
        }
    }

    private void notifyTcpServerClosed(final String msg, final Exception e) {
        if (mTcpServerStateListener != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTcpServerStateListener.onServerClosed(TcpServer.this, msg, e);
                }
            });
        }
    }


    public int getPort() {
        return port;
    }

    private void setServerState(ServerState state) {
        this.mServerState = state;
    }

    public boolean isClosed() {
        return mServerState == ServerState.Closed;
    }

    public boolean isListening() {
        return mServerState == ServerState.Listening;
    }

    public void config(TcpServerConfig tcpServerConfig) {
        mTcpServerConfig = tcpServerConfig;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Xtcpserver port=" + port + ",state=" + mServerState);
        sb.append(" client size=" + mXTcpClients.size());
        return sb.toString();
    }
}
