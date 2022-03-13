package com.lois.tytool.tts;

/**
 * @Description 语音队列中的元素对象定义
 * @User Luo.T.Y
 * @Date 2017-12-22
 * @Time 16:04
 */
public class AudioElement {
    /** 语音内容 */
    private String text;
    /** 播报等级 */
    private AudioLevel level;
    /** 是否播放引导音 */
    private boolean isPlayTone;
    /** 是否可延时播报 */
    private boolean canDelay;
    /** 最大延时时间 单位: 纳秒 */
    private long maxDelayTime;
    /** 时间戳 单位: 纳秒 */
    private long timestamp;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public AudioLevel getLevel() {
        return level;
    }

    public void setLevel(AudioLevel level) {
        this.level = level;
    }

    public boolean isPlayTone() {
        return isPlayTone;
    }

    public void setPlayTone(boolean isPlayTone) {
        this.isPlayTone = isPlayTone;
    }

    public boolean isCanDelay() {
        return canDelay;
    }

    public void setCanDelay(boolean canDelay) {
        this.canDelay = canDelay;
    }

    public long getMaxDelayTime() {
        return maxDelayTime;
    }

    public void setMaxDelayTime(long maxDelayTime) {
        this.maxDelayTime = maxDelayTime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public AudioElement(String text, AudioLevel level, boolean isPlayTone, boolean canDelay, long maxDelayTime, long timestamp) {
        this.text = text;
        this.level = level;
        this.isPlayTone = isPlayTone;
        this.canDelay = canDelay;
        this.maxDelayTime = maxDelayTime;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AudioElement{" +
                "text='" + text + '\'' +
                ", level=" + level +
                ", isPlayTone=" + isPlayTone +
                ", canDelay=" + canDelay +
                ", maxDelayTime=" + maxDelayTime +
                ", timestamp=" + timestamp +
                '}';
    }
}
