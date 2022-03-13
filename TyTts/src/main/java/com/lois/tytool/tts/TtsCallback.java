package com.lois.tytool.tts;

/**
 * Description: TTS回调接口
 * User: Luo.T.Y
 * Date: 2017-12-22
 * Time: 10:28
 */
public interface TtsCallback {

    /**
     * TTS状态改变
     * @param status 状态
     * @param element 内容
     */
    void onTtsStatusChanged(TtsStatus status, AudioElement element);

}
