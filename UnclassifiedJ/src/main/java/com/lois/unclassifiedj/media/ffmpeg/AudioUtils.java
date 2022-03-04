package com.lois.unclassifiedj.media.ffmpeg;

import com.lois.tytool.basej.debug.TyLog;

import org.bytedeco.javacv.*;

/**
 * @Description 音频工具类
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class AudioUtils {

    /**
     * 音频格式转换
     * @param inputFile -导入音频文件
     * @param outputFile -导出音频文件
     * @param audioCodec -音频编码
     * @param sampleRate -音频采样率
     * @param audioBitrate -音频比特率
     */
    public static void convert(String inputFile, String outputFile, int audioCodec, int sampleRate, int audioBitrate,
                               int audioChannels) {
        Frame audioSamples;
        // 音频录制（输出地址，音频通道）
        FFmpegFrameRecorder recorder;
        //抓取器
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);

        // 开启抓取器
        if (start(grabber)) {
            recorder = new FFmpegFrameRecorder(outputFile, audioChannels);
            recorder.setAudioOption("crf", "0");
            recorder.setAudioCodec(audioCodec);
            recorder.setAudioBitrate(audioBitrate);
            recorder.setAudioChannels(audioChannels);
            recorder.setSampleRate(sampleRate);
            recorder.setAudioQuality(0);
            recorder.setAudioOption("aq", "10");
            // 开启录制器
            if (start(recorder)) {
                try {
                    // 抓取音频
                    while ((audioSamples = grabber.grab()) != null) {
                        recorder.setTimestamp(grabber.getTimestamp());
                        recorder.record(audioSamples);
                    }

                } catch (org.bytedeco.javacv.FrameGrabber.Exception e1) {
                    TyLog.e("抓取失败");
                } catch (Exception e) {
                    TyLog.e("录制失败");
                }
                stop(grabber);
                stop(recorder);
            }
        }

    }

    /**
     * 打开抓取器
     * @param grabber
     * @return
     */
    private static boolean start(FrameGrabber grabber) {
        try {
            grabber.start();
            return true;
        } catch (org.bytedeco.javacv.FrameGrabber.Exception e2) {
            try {
                TyLog.e("首次打开抓取器失败，准备重启抓取器...");
                grabber.restart();
                return true;
            } catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
                try {
                    TyLog.e("重启抓取器失败，正在关闭抓取器...");
                    grabber.stop();
                } catch (org.bytedeco.javacv.FrameGrabber.Exception e1) {
                    TyLog.e("停止抓取器失败！");
                }
            }

        }
        return false;
    }

    /**
     * 打开录制器
     * @param recorder
     * @return
     */
    private static boolean start(FrameRecorder recorder) {
        try {
            recorder.start();
            return true;
        } catch (Exception e2) {
            try {
                TyLog.e("首次打开录制器失败！准备重启录制器...");
                recorder.stop();
                recorder.start();
                return true;
            } catch (Exception e) {
                try {
                    TyLog.e("重启录制器失败！正在停止录制器...");
                    recorder.stop();
                } catch (Exception e1) {
                    TyLog.e("关闭录制器失败！");
                }
            }
        }
        return false;
    }

    /**
     * 关闭抓取器
     * @param grabber
     * @return
     */
    private static void stop(FrameGrabber grabber) {
        try {
            grabber.flush();
            grabber.stop();
        } catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
            TyLog.e("", e);
            try {
                grabber.stop();
            } catch (org.bytedeco.javacv.FrameGrabber.Exception ex) {
                TyLog.e("关闭抓取器失败");
            }
        }
    }

    /**
     * 录制器
     * @param recorder
     * @return
     */
    private static void stop(FrameRecorder recorder) {
        try {
            recorder.stop();
            recorder.release();
        } catch (Exception e) {
            TyLog.e("", e);
            try {
                recorder.stop();
            } catch (Exception ex) {
                TyLog.e("关闭录制器失败");
            }
        }
    }
}
