package com.lois.tytool.socket.tcp.server;

import com.lois.tytool.socket.BaseSocket;
import com.lois.tytool.socket.tcp.client.TcpClient;
import com.lois.tytool.socket.tcp.client.bean.TargetInfo;
import com.lois.tytool.socket.tcp.client.bean.TcpMsg;
import com.lois.tytool.socket.tcp.client.listener.TcpClientListener;
import com.lois.tytool.socket.tcp.server.listener.TcpServerListener;
import com.lois.tytool.socket.tcp.server.manager.TcpServerManager;
import com.lois.tytool.socket.tcp.server.state.ServerState;
import com.lois.tytool.socket.utils.XSocketLog;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * tcp服务端
 */
public class TcpServer extends BaseSocket implements TcpClientListener {
    private static final String TAG = "XTcpServer";
    protected int port;
    protected ServerState mServerState;
    protected ServerSocket mServerSocket;
    protected Map<TargetInfo, TcpClient> mXTcpClients;
    protected ListenThread mListenThread;
    protected TcpServerConfig mTcpServerConfig;
    protected List<TcpServerListener> mTcpServerListeners;

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
        mTcpServerListeners = new ArrayList<>();
        if (mTcpServerConfig == null) {
            mTcpServerConfig = new TcpServerConfig.Builder().create();
        }
    }

    //开启tcpserver
    public void startServer() {
        if (!getListenThread().isAlive()) {
            XSocketLog.d(TAG, "tcp server启动ing ");
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
        XSocketLog.d(TAG, "tcp server closed");
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

    public boolean sendMsgToAll(TcpMsg msg) {
        boolean re = true;
        for (TcpClient client : mXTcpClients.values()) {
            if (client.sendMsg(msg) == null) {
                re = false;
            }
        }
        return re;
    }

    public boolean sendMsgToAll(String msg) {
        boolean re = true;
        for (TcpClient client : mXTcpClients.values()) {
            if (client.sendMsg(msg) == null) {
                re = false;
            }
        }
        return re;
    }

    public boolean sendMsgToAll(byte[] msg) {
        boolean re = true;
        for (TcpClient client : mXTcpClients.values()) {
            if (client.sendMsg(msg) == null) {
                re = false;
            }
        }
        return re;
    }

    public boolean sendMsg(TcpMsg msg, TcpClient client) {
        return client.sendMsg(msg) != null;
    }

    public boolean sendMsg(String msg, TcpClient client) {
        return client.sendMsg(msg) != null;
    }

    public boolean sendMsg(byte[] msg, TcpClient client) {
        return client.sendMsg(msg) != null;
    }

    public boolean sendMsg(TcpMsg msg, String ip) {
        TcpClient client = mXTcpClients.get(ip);
        if (client != null) {
            return client.sendMsg(msg) != null;
        }
        return false;
    }

    public boolean sendMsg(String msg, String ip) {
        TcpClient client = mXTcpClients.get(ip);
        if (client != null) {
            return client.sendMsg(msg) != null;
        }
        return false;
    }

    public boolean sendMsg(byte[] msg, String ip) {
        TcpClient client = mXTcpClients.get(ip);
        if (client != null) {
            return client.sendMsg(msg) != null;
        }
        return false;
    }

    @Override
    public void onConnected(TcpClient client) {
        //no callback,ignore
    }

    @Override
    public void onSended(TcpClient client, TcpMsg tcpMsg) {
        notifyTcpServerSended(client, tcpMsg);
    }

    @Override
    public void onDisconnected(TcpClient client, String msg, Exception e) {
        notifyTcpClientClosed(client, msg, e);
    }

    @Override
    public void onReceive(TcpClient client, TcpMsg tcpMsg) {
        notifyTcpServerReceive(client, tcpMsg);
    }

    @Override
    public void onValidationFail(TcpClient client, TcpMsg tcpMsg) {
        notifyTcpServerValidationFail(client, tcpMsg);
    }

    class ListenThread extends Thread {
        @Override
        public void run() {
            Socket socket;
            while (!Thread.interrupted()) {
                try {
                    XSocketLog.d(TAG, "tcp server listening");
                    socket = getServerSocket().accept();
                    TargetInfo targetInfo = new TargetInfo(socket.getInetAddress().getHostAddress(), socket.getPort());
                    TcpClient xTcpClient = TcpClient.getTcpClient(socket, targetInfo,
                            mTcpServerConfig.getTcpConnConfig());//创建一个client，接受和发送消息
                    notifyTcpServerAccept(xTcpClient);
                    xTcpClient.addTcpClientListener(TcpServer.this);
                    mXTcpClients.put(targetInfo, xTcpClient);
                } catch (IOException e) {
                    XSocketLog.d(TAG, "tcp server listening error:" + e);
//                    e.printStackTrace();
                    stopServer("监听失败", e);
//                    return;
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
//                e.printStackTrace();
                stopServer("创建失败", e);
            }
        }
        return mServerSocket;
    }

    public void addTcpServerListener(TcpServerListener listener) {
        if (mTcpServerListeners.contains(listener)) {
            return;
        }
        this.mTcpServerListeners.add(listener);
    }

    public void removeTcpServerListener(TcpServerListener listener) {
        this.mTcpServerListeners.remove(listener);
    }

    private void notifyTcpServerCreate() {
        for (TcpServerListener wr : mTcpServerListeners) {
            final TcpServerListener l = wr;
            if (l != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        l.onCreated(TcpServer.this);
                    }
                });
            }
        }
    }

    private void notifyTcpServerLinten() {
        for (TcpServerListener wr : mTcpServerListeners) {
            final TcpServerListener l = wr;
            if (l != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        l.onListened(TcpServer.this);
                    }
                });
            }
        }
    }

    private void notifyTcpServerAccept(final TcpClient client) {
        for (TcpServerListener wr : mTcpServerListeners) {
            final TcpServerListener l = wr;
            if (l != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        l.onAccept(TcpServer.this, client);
                    }
                });
            }
        }
    }

    private void notifyTcpServerReceive(final TcpClient client, final TcpMsg tcpMsg) {
        for (TcpServerListener wr : mTcpServerListeners) {
            final TcpServerListener l = wr;
            if (l != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        l.onReceive(TcpServer.this, client, tcpMsg);
                    }
                });
            }
        }
    }

    private void notifyTcpServerSended(final TcpClient client, final TcpMsg tcpMsg) {
        for (TcpServerListener wr : mTcpServerListeners) {
            final TcpServerListener l = wr;
            if (l != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        l.onSended(TcpServer.this, client, tcpMsg);
                    }
                });
            }
        }
    }

    private void notifyTcpServerValidationFail(final TcpClient client, final TcpMsg tcpMsg) {
        for (TcpServerListener wr : mTcpServerListeners) {
            final TcpServerListener l = wr;
            if (l != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        l.onValidationFail(TcpServer.this, client, tcpMsg);
                    }
                });
            }
        }
    }

    private void notifyTcpClientClosed(final TcpClient client, final String msg, final Exception e) {
        for (TcpServerListener wr : mTcpServerListeners) {
            final TcpServerListener l = wr;
            if (l != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        l.onClientClosed(TcpServer.this, client, msg, e);
                    }
                });
            }
        }
    }

    private void notifyTcpServerClosed(final String msg, final Exception e) {
        for (TcpServerListener wr : mTcpServerListeners) {
            final TcpServerListener l = wr;
            if (l != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        l.onServerClosed(TcpServer.this, msg, e);
                    }
                });
            }
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
