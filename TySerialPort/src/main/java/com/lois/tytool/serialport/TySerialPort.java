package com.lois.tytool.serialport;

import android.util.Log;

import com.lois.tytool.base.stream.coder.IMessageReceiver;
import com.lois.tytool.base.string.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;

/**
 * @Description 串口操作类
 * @Author Luo.T.Y
 * @Date 2022/3/1
 * @Time 18:43
 */
public class TySerialPort {
    private static final String TAG = TySerialPort.class.getSimpleName();
    private SerialPort serialPort;
    private OutputStream outputStream;
    private InputStream inputStream;
    private ReadThread readThread;
    private SendThread sendThread;
    private boolean openState = false;
    private byte[] loopData = new byte[]{0x30};
    private int delay = 100;

    /**
     * 串口参数
     */
    private String port;
    private int baudRate;
    private IMessageReceiver mMessageReceiver = null;
    private OnStatesChangeListener onStatesChangeListener;

    /**
     * 私有构造方法
     * @param port
     * @param baudRate
     * @param messageReceiver
     * @param statesChangeListener
     */
    private TySerialPort(String port, int baudRate, IMessageReceiver messageReceiver, OnStatesChangeListener statesChangeListener) {
        this.port = port;
        this.baudRate = baudRate;
        this.mMessageReceiver = messageReceiver;
        this.onStatesChangeListener = statesChangeListener;
    }

    /**
     * Builder类
     */
    public static class Builder {
        private String port = "/dev/ttyHSL0";
        private int baudRate = 9600;
        private IMessageReceiver mMessageReceiver = null;
        private OnStatesChangeListener onStatesChangeListener = null;

        public Builder setPort(String port) {
            this.port = port;
            return this;
        }

        public Builder setBaudRate(int baudRate) {
            this.baudRate = baudRate;
            return this;
        }

        public Builder setReceiver(IMessageReceiver messageReceiver) {
            this.mMessageReceiver = messageReceiver;
            return this;
        }

        public Builder setSatesListener(OnStatesChangeListener onStatesChangeListener) {
            this.onStatesChangeListener = onStatesChangeListener;
            return this;
        }

        public TySerialPort build() throws Exception {
            if (port == null || port.isEmpty()) {
                throw new Exception("port is null or empty!");
            }
            return new TySerialPort(port, baudRate, mMessageReceiver, onStatesChangeListener);
        }
    }

    /**
     * 设置数据接收回调
     * @param messageReceiver
     */
    public TySerialPort setMessageReceiver(IMessageReceiver messageReceiver) {
        this.mMessageReceiver = messageReceiver;
        return this;
    }

    /**
     * 清除数据接收回调
     */
    public TySerialPort removeMessageReceiver() {
        this.mMessageReceiver = null;
        return this;
    }

    /**
     * 设置状态监听
     * @param onStatesChangeListener
     */
    public TySerialPort setSatesListener(OnStatesChangeListener onStatesChangeListener) {
        this.onStatesChangeListener = onStatesChangeListener;
        return this;
    }

    /**
     * 清除状态监听
     */
    public TySerialPort removeSatesListener() {
        this.onStatesChangeListener = null;
        return this;
    }

    /**
     * 是否开启串口
     *
     * @return
     */
    public boolean isOpen() {
        return openState;
    }

    /**
     * 串口打开方法
     */
    public TySerialPort open() {
        try {
            baseOpen();
            Log.i(TAG, "打开串口成功！");
            if (onStatesChangeListener != null) {
                onStatesChangeListener.onOpen(true, "");
            }
        } catch (SecurityException e) {
            Log.e(TAG, "打开串口失败:没有串口读/写权限!");
            e.printStackTrace();
            if (onStatesChangeListener != null) {
                onStatesChangeListener.onOpen(false, "没有串口读/写权限!");
            }
        } catch (IOException e) {
            Log.e(TAG, "打开串口失败:未知错误!");
            e.printStackTrace();
            if (onStatesChangeListener != null) {
                onStatesChangeListener.onOpen(false, "未知错误!");
            }
        } catch (InvalidParameterException e) {
            Log.e(TAG, "打开串口失败:参数错误!");
            e.printStackTrace();
            if (onStatesChangeListener != null) {
                onStatesChangeListener.onOpen(false, "参数错误!");
            }
        } catch (Exception e) {
            Log.e(TAG, "openComPort: 其他错误");
            e.printStackTrace();
            if (onStatesChangeListener != null) {
                onStatesChangeListener.onOpen(false, "其他错误!");
            }
        }
        return this;
    }

    private void baseOpen() throws SecurityException, IOException, InvalidParameterException {
        serialPort = new SerialPort(new File(port), baudRate, 0);
        outputStream = serialPort.getOutputStream();
        inputStream = serialPort.getInputStream();
        readThread = new ReadThread();
        readThread.start();
        sendThread = new SendThread();
        sendThread.setSuspendFlag();
        sendThread.start();
        openState = true;
    }

    /**
     * 串口关闭方法
     */
    public void close() {
        if (readThread != null) {
            readThread.interrupt();
        }
        if (serialPort != null) {
            serialPort.close();
            serialPort = null;
        }
        openState = false;
        if (onStatesChangeListener != null) {
            onStatesChangeListener.onClose();
        }
    }

    /**
     * 执行发送程序，若未开启，则会先开启，然后再发送
     *
     * @param bOutArray
     */
    private TySerialPort send(byte[] bOutArray) {
        try {
            if (openState) {
                outputStream.write(bOutArray);
            } else {
                open();
                outputStream.write(bOutArray);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 发送十六进制字符串
     *
     * @param hexString
     */
    public TySerialPort sendHex(String hexString) {
        byte[] bOutArray = StringUtils.hexStringToBytes(hexString);
        send(bOutArray);
        return this;
    }

    /**
     * 发送文本
     *
     * @param txtString
     */
    public TySerialPort sendTxtString(String txtString) {
        sendTxtString(txtString, "UTF-8");
        return this;
    }

    /**
     * 发送文本
     *
     * @param txtString
     * @param charsetName
     */
    public TySerialPort sendTxtString(String txtString, String charsetName) {
        byte[] bOutArray;
        try {
            bOutArray = txtString.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return this;
        }
        send(bOutArray);
        return this;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 读取数据线程
     */
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    if (inputStream == null) {
                        return;
                    }
                    byte[] buffer = new byte[1024];
                    int size = inputStream.read(buffer);
                    if (size > 0) {
                        byte[] temp = new byte[size];
                        System.arraycopy(buffer, 0, temp, 0, size);
                        if (mMessageReceiver != null) {
                            mMessageReceiver.onReceiveData(TySerialPort.this, mMessageReceiver.decode(temp));
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    /**
     * 发送数据线程
     */
    private class SendThread extends Thread {
        /**
         * 线程运行标志
         */
        boolean runFlag = true;

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                synchronized (this) {
                    while (runFlag) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                send(getLoopData());
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 线程暂停
         */
        private void setSuspendFlag() {
            this.runFlag = true;
        }

        /**
         * 唤醒线程
         */
        private synchronized void setResume() {
            this.runFlag = false;
            notify();
        }
    }

    /**
     * 读取波特率
     * @return
     */
    public int getBaudRate() {
        return baudRate;
    }

    /**
     * 设置波特率
     * @param iBaud
     * @return
     */
    public boolean setBaudRate(int iBaud) {
        if (openState) {
            return false;
        } else {
            baudRate = iBaud;
            return true;
        }
    }

    /**
     * 设置波特率
     * @param sBaud
     * @return
     */
    public boolean setBaudRate(String sBaud) {
        int iBaud = Integer.parseInt(sBaud);
        return setBaudRate(iBaud);
    }

    /**
     * 获取端口
     * @return
     */
    public String getPort() {
        return port;
    }

    /**
     * 设置端口
     * @param sPort
     * @return
     */
    public boolean setPort(String sPort) {
        if (openState) {
            return false;
        } else {
            this.port = sPort;
            return true;
        }
    }

    /**
     * 读取循环发送的数据
     * @return
     */
    public byte[] getLoopData() {
        return loopData;
    }

    /**
     * 设置循环发送的数据
     *
     * @param loopData byte数据
     */
    public TySerialPort setLoopData(byte[] loopData) {
        this.loopData = loopData;
        return this;
    }

    /**
     * 设置循环发送的数据
     *
     * @param str         传入的字符串
     * @param isHexString 是否为16进制字符串
     */
    public TySerialPort setLoopData(String str, boolean isHexString) {
        if (isHexString) {
            this.loopData = str.getBytes();
        } else {
            this.loopData = StringUtils.hexStringToBytes(str);
        }
        return this;
    }

    /**
     * 获取延迟
     *
     * @return 时间（毫秒）
     */
    public int getDelay() {
        return delay;
    }

    /**
     * 设置延时（毫秒）
     *
     * @param delay
     */
    public TySerialPort setDelay(int delay) {
        this.delay = delay;
        return this;
    }

    /**
     * 开启循环发送
     */
    public TySerialPort startSend() {
        if (sendThread != null) {
            sendThread.setResume();
        }
        return this;
    }

    /**
     * 停止循环发送
     */
    public TySerialPort stopSend() {
        if (sendThread != null) {
            sendThread.setSuspendFlag();
        }
        return this;
    }
}
