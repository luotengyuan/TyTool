package com.lois.unclassifieda.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Description: 蓝牙服务
 * User: Luo.T.Y
 * Date: 2017-09-22
 * Time: 9:38
 */
public class BluetoothService {
    private static final String TAG = BluetoothService.class.getSimpleName();
    private static final boolean D = true;
    private static final String NAME = "BlueTooth";
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BluetoothAdapter btAdapter;
    private final Handler btHandler;
    private AcceptThread btAcceptThread;
    private ConnectThread btConnectThread;
    private ConnectedThread btConnectedThread;
    private int btState;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final String DEVICE_NAME = "device_name";

    public static final String TOAST = "toast";

    public BluetoothService(Context context, Handler handler) {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        btState = STATE_NONE;
        btHandler = handler;
    }

    private synchronized void setState(int state) {
        if (D)
            Log.d(TAG, "setState() " + btState + " -> " + state);
        btState = state;
        btHandler.obtainMessage(MESSAGE_STATE_CHANGE, state,
                -1).sendToTarget();
    }

    public synchronized int getState() {
        return btState;
    }

    public synchronized void start() {
        if (D)
            Log.d(TAG, "start");

        if (btConnectThread != null) {
            btConnectThread.cancel();
            btConnectThread = null;
        }

        if (btConnectedThread != null) {
            btConnectedThread.cancel();
            btConnectedThread = null;
        }

        if (btAcceptThread == null) {
            btAcceptThread = new AcceptThread();
            btAcceptThread.start();
        }
        setState(STATE_LISTEN);
    }

    public synchronized void connect(BluetoothDevice device) {
        if (D)
            Log.d(TAG, "connect to: " + device);
        System.out.println("-----buletoothChat device");

        if (btState == STATE_CONNECTING) {
            if (btConnectThread != null) {
                btConnectThread.cancel();
                btConnectThread = null;
            }
        }
        System.out.println("-----buletoothChat STATE_CONNECTING");

        if (btConnectedThread != null) {
            btConnectedThread.cancel();
            btConnectedThread = null;
        }

        btConnectThread = new ConnectThread(device);
        System.out.println("-----buletoothChatConnectThread");
        btConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket,
                                       BluetoothDevice device) {
        if (D)
            Log.d(TAG, "connected");
        if (btConnectThread != null) {
            btConnectThread.cancel();
            btConnectThread = null;
        }
        if (btConnectedThread != null) {
            btConnectedThread.cancel();
            btConnectedThread = null;
        }
        if (btAcceptThread != null) {
            btAcceptThread.cancel();
            btAcceptThread = null;
        }
        btConnectedThread = new ConnectedThread(socket);
        btConnectedThread.start();

        Message msg = btHandler
                .obtainMessage(MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_NAME, device.getName());
        msg.setData(bundle);
        btHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        if (D)
            Log.d(TAG, "stop");
        if (btConnectThread != null) {
            btConnectThread.cancel();
            btConnectThread = null;
        }
        if (btConnectedThread != null) {
            btConnectedThread.cancel();
            btConnectedThread = null;
        }
        if (btAcceptThread != null) {
            btAcceptThread.cancel();
            btAcceptThread = null;
        }
        setState(STATE_NONE);
    }

    public void write(byte[] out) {

        ConnectedThread r;

        synchronized (this) {
            if (btState != STATE_CONNECTED)
                return;
            r = btConnectedThread;
        }
        r.write(out);
    }

    private void connectionFailed() {
        setState(STATE_LISTEN);

        Message msg = btHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "Unable to connect device");
        msg.setData(bundle);
        btHandler.sendMessage(msg);
    }

    private void connectionLost() {
        setState(STATE_LISTEN);

        Message msg = btHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "Device connection was lost");
        msg.setData(bundle);
        btHandler.sendMessage(msg);
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = btAdapter.listenUsingRfcommWithServiceRecord(NAME,
                        MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            if (D) Log.d(TAG, "BEGIN btAcceptThread" + this);
            setName("AcceptThread");
            BluetoothSocket socket = null;
            while (btState != STATE_CONNECTED) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (btState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            if (D) Log.i(TAG, "END btAcceptThread");
        }

        public void cancel() {
            if (D)
                Log.d(TAG, "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN btConnectThread");
            setName("ConnectThread");

            btAdapter.cancelDiscovery();

            try {

                mmSocket.connect();
            } catch (IOException e) {
                connectionFailed();

                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG,
                            "unable to close() socket during connection failure",
                            e2);
                }

                BluetoothService.this.start();
                return;
            }

            synchronized (BluetoothService.this) {
                btConnectThread = null;
            }

            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN btConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {

                    bytes = mmInStream.read(buffer);

                    btHandler.obtainMessage(MESSAGE_READ,
                            bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                btHandler.obtainMessage(MESSAGE_WRITE, -1,
                        -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
