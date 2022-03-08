package com.lois.tytool;

import android.os.Handler;
import android.os.Message;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * @Description 定时器驱动
 * @Author Luo.T.Y
 * @Date 2017-09-23
 * @Time 9:17
 */
public class TyTimer {
    /**
     * 定时线程定时周期, 单位: 毫秒
     */
    private static final int PERIOD_MS = 200;
    private static TimerServer mServer = new TimerServer(PERIOD_MS);
    private int mPeriod; // 定时周期
    private int mLeft; // 剩下的计时时间
    private boolean mIsRunning; // 标识是否处于运行状态
    private Object mObj; // 用户对象
    private TyTimerListener mListener; // 定时器监听器

    /**
     * 缺省构造器
     */
    public TyTimer() {
        mPeriod = 0;
        mLeft = 0;
        mIsRunning = false;
        mObj = null;
        mListener = null;
    }

    /**
     * 构造器
     *
     * @param listener 定时器监听器
     */
    public TyTimer(TyTimerListener listener) {
        this(listener, null);
    }

    /**
     * 构造器
     *
     * @param listener 定时器监听器
     * @param obj      用户数据对象
     */
    public TyTimer(TyTimerListener listener, Object obj) {
        this();
        this.mObj = obj;
        this.mListener = listener;
    }

    /**
     * 启动定时器
     *
     * @param period 定时周期, 单位: ms
     * @return true: 启动成功, false: 启动失败
     */
    public boolean start(int period) {
        return start(period, this.mListener, this.mObj);
    }

    /**
     * 启动定时器
     *
     * @param period   定时周期, 单位: 毫秒
     * @param listener 定时监听器
     * @return true: 启动成功, false: 启动失败
     */
    public boolean start(int period, TyTimerListener listener) {
        return start(period, listener, this.mObj);
    }

    /**
     * 启动定时器
     *
     * @param period   定时器周期
     * @param listener 定时监听器
     * @param obj      用户数据对象
     * @return true: 启动成功, false: 启动失败
     */
    public boolean start(int period, TyTimerListener listener, Object obj) {
        if (listener == null) {
            return false;
        }
        this.mListener = listener;
        this.mObj = obj;
        this.mPeriod = period / PERIOD_MS;
        if (this.mPeriod == 0) {
            this.mPeriod = 1;
        }
        mLeft = this.mPeriod;
        if (!mIsRunning) {
            mIsRunning = true;
            mServer.addTimer(this);
        }
        return true;
    }

    /**
     * 停止定时器
     */
    public void stop() {
        if (mIsRunning) {
            mIsRunning = false;
            mServer.removeTimer(this);
        }
    }

    /**
     * 测试定时器是否处于运行状态
     *
     * @return true: 运行中, false: 已停止
     */
    public boolean isRunning() {
        return mIsRunning;
    }

    /**
     * 获取定时剩余时间
     *
     * @return 剩余时间, 单位: 毫秒
     */
    public int leftTime() {
        if (mIsRunning) {
            return mLeft * PERIOD_MS;
        } else {
            return 0;
        }
    }

    /**
     * 定时服务器
     *
     * @author 陈从华 V1.00 2011.05.18 创建<br>
     */
    private static class TimerServer {
        private TimerThread mThread = null;
        private LinkedList<TyTimer> runList = new LinkedList<TyTimer>();
        private LinkedList<TyTimer> addList = new LinkedList<TyTimer>();
        private boolean handling = false;
        private boolean needClear = false;

        /**
         * 构造器
         *
         * @param period 定时周期, 单位: 毫秒
         */
        private TimerServer(int period) {
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message ms) {
                    // 遍历链表中的定时器, 测试定时时间是否已到
                    handling = true;
                    for (TyTimer tm : runList) {
                        if (tm.mIsRunning && --tm.mLeft == 0) {
                            tm.mLeft = tm.mPeriod;
                            if (tm.mListener != null) {
                                tm.mListener.onPeriod(tm.mObj);
                            }
                        }
                    }
                    handling = false;

                    // 将追加链表中的定时器添加到运行链表中
                    for (TyTimer tm : addList) {
                        runList.add(tm);
                    }
                    addList.clear();

                    // 移除链表中待删除的定时器
                    if (needClear) {
                        Iterator<TyTimer> it = runList.iterator();
                        while (it.hasNext()) {
                            TyTimer timer = it.next();
                            if (!timer.mIsRunning) {
                                it.remove();
                            }
                        }
                        needClear = false;
                    }

                    // 如链表中无定时器, 则使得定时线程进入睡眠
                    if (runList.size() == 0) {
                        mThread.setStop();
                    }
                }
            };

            mThread = new TimerThread(period);
            mThread.start();
            mThread.setServerHandler(handler);
        }

        /**
         * 往定时器服务器中追加一个定时器
         *
         * @param timer 待追加的定时器
         */
        private void addTimer(TyTimer timer) {
            if (runList.contains(timer)) {
                return;
            }
            if (addList.contains(timer)) {
                return;
            }
            if (handling) {
                addList.add(timer);
            } else {
                runList.add(timer);
                if (runList.size() == 1) {
                    // 如链表中追加前无定时器, 则启动定时线程
                    mThread.setRun();
                }
            }
        }

        /**
         * 从定时器服务器中移除一个定时器
         *
         * @param timer 待移除的定时器
         */
        private void removeTimer(TyTimer timer) {
            if (addList.remove(timer)) {
                return;
            }
            // 先测试是否处于处理定时溢出阶段
            if (!handling) {
                runList.remove(timer);
            } else {
                needClear = true;
            }
        }
    }

    /**
     * 定时线程
     *
     * @author 陈从华 V1.00 2011.05.18 创建<br>
     */
    private static class TimerThread extends Thread {
        private Handler serHandler; // server消息处理句柄
        private boolean running = false;
        private int period = 0;

        /**
         * 构造器
         *
         * @param period 定时周期, 单位: 毫秒
         */
        TimerThread(int period) {
            super();
            this.period = period;
        }

        /**
         * 设置server的消息处理句柄
         *
         * @param serHandler 消息处理句柄
         */
        void setServerHandler(Handler serHandler) {
            synchronized (this) {
                this.serHandler = serHandler;
            }
        }

        /**
         * 线程执行函数
         */
        @Override
        public void run() {
            // 设置线程名称
            setName("timer thread");
            while (true) {
                synchronized (this) {
                    while (!running || period == 0) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            ;
                        }
                    }
                }
                if (period > 0) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(period);
                    } catch (InterruptedException e) {
                        ;
                    }
                    synchronized (this) {
                        if (serHandler != null) {
                            serHandler.sendEmptyMessage(0);
                        }
                    }
                }
            }
        }

        /**
         * 设置成运行状态
         */
        void setRun() {
            synchronized (this) {
                if (!running) {
                    running = true;
                    this.notify();
                }
            }
        }

        /**
         * 设置成停止状态
         */
        void setStop() {
            synchronized (this) {
                if (running) {
                    running = false;
                }
            }
        }
    }
}
