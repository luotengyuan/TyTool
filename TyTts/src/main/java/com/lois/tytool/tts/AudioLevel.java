package com.lois.tytool.tts;

/**
 * Description: 语音提示等级
 * User: Luo.T.Y
 * Date: 2017-12-22
 * Time: 16:17
 */
public enum AudioLevel {
    /** 激进的特殊提醒或危险操作，需打断低等级的语音 High middle low*/
    AUDIO_LEVEL_1_RADICAL,
    /** 实时提醒，有时间要求，不可延时播报 */
    AUDIO_LEVEL_2_REALTIME,
    /** 普通语音，无时间要求，可延时播报 */
    AUDIO_LEVEL_3_GENERAL
}
