package com.lois.tytool.tts;

/**
 * @Description TTS播报及JNI接口类
 * @Author Luo.T.Y
 * @Date 2017-09-22
 * @Time 11:32
 */
public class TtsJni {

	static {
		System.loadLibrary("tts");
	}

	/**
	 * 获取版本
	 * @return
	 */
	public static native int JniGetVersion();

	/**
	 * 初始化TTS语音实例
	 * @param resFilename 资源文件路径
	 * @return
	 */
	public static native int JniCreate(String resFilename);

	/**
	 * 销毁语音对象
	 * @return
	 */
	public static native int JniDestory();

	/**
	 * 停止语音播报
	 * @return
	 */
	public static native int JniStop();

	/**
	 * 播报指定文本
	 * @param text	待播报文本内容
	 * @return
	 */
	public static native int JniSpeak(String text);

	/**
	 * 设置语音参数
	 * @param paramId	参数ID
	 * @param value		参数值
	 * @return
	 */
	public static native int JniSetParam(int paramId, int value);

	/**
	 * 获取语音参数
	 * @param paramId	参数ID
	 * @return
	 */
	public static native int JniGetParam(int paramId);

	/**
	 * 是否正在播放
	 * @return	0:	没有播放
	 * 			1:	正在播放
	 * 			2:	播放完成
	 */
	public static native int JniIsPlaying();

	/**
	 * 语音对象是否创建
	 * @return
	 */
	public static native boolean JniIsCreated();
}

