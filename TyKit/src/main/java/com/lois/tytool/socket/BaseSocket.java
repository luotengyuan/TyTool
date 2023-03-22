package com.lois.tytool.socket;

import android.os.Handler;
import android.os.Looper;

/**
 * @author Administrator
 */
public abstract class BaseSocket {
    private Handler mUIHandler;
    protected Object lock;

    public BaseSocket() {
        mUIHandler = new Handler(Looper.getMainLooper());
        lock = new Object();
    }

    protected void runOnUiThread(Runnable runnable) {
        mUIHandler.post(runnable);
    }
}
