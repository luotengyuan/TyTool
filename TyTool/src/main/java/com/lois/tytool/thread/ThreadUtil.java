package com.lois.tytool.thread;

import android.os.Handler;

/**
 * 线程工具类
 * @Author Lois
 * @Date 2022/1/21 16:09
 */
public class ThreadUtil {

	private static Handler handler=new Handler();

	/**
	 * 启动一个子线程
	 * @param runnable
	 */
	public static void runInChildThread(Runnable runnable){
		new Thread(runnable).start();
	}
	

	/**
	 * 在子线程中执行UI操作
	 * @param runnable
	 */
	public static void runInUIThread(Runnable runnable){
		handler.post(runnable);
	}
	
}
