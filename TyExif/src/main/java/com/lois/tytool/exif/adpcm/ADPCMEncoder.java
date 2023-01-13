package com.lois.tytool.exif.adpcm;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 把16位16000Hz的单声道音频编码成IMA-ADPCM格式。
 */
public class ADPCMEncoder {
    /**
     * 样本块中的样本数。
     */
    public final static int BLOCKSAMPLES = 1017;
    /**
     * 采样率
     */
    public final static int SAMPLERATE = 16000;
    /**
     * 被压缩块的字节数。按照最初的样本未压缩的初始步骤(2)+指数(1)+空白(1)+样品除了初始/ 2
     */
    public final static int BLOCKBYTES = (BLOCKSAMPLES - 1) / 2 + 4;

    /**
     * 把数据写到一个.wav为后缀的文件。
     *
     * @param encoded 被压缩块的数组.
     * @param f 目标文件
     * @throws IOException
     */
    public static void writeToWav(Block[] encoded, File f) throws IOException {
        // 打开文件
        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(f));
        // 计算样本数量并写文件头
        int samples = encoded.length * BLOCKSAMPLES;
        int blocks = writeWavHeader(output, samples);
        if (blocks != encoded.length) {
			throw new Error("Ooops");
		}
        // Write all the data
		for (Block block : encoded) {
			output.write(block.getData());
		}
        output.close();
    }

    /**
     * 压缩数据,并将其写入一个wav文件。
     *
     * @param allData 被压缩块的数组.
     * @param f 目标文件
     * @throws IOException
     */
    public static void encodeToWav(byte[] allData, File f) throws IOException {
        // 打开文件
        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(f));
        // 计算出有多少块
        int samples = allData.length / 2;
        int blocks = writeWavHeader(output, samples);
        // 编码并写入所以块
        int pos = 0;
        for (int i = 0; i < blocks; i++) {
            int size = Math.min(BLOCKSAMPLES * 2, allData.length - pos);
            Block block = encodeBlock(allData, pos, size);
            output.write(block.getData());
            pos += size;
        }
        output.close();
    }

    /**
     * 压缩数据,并将其写入一个wav文件。
     *
     * @param allData 被压缩块的数组.
     * @throws IOException
     */
    public static byte[] encodeToByteArray(byte[] allData) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        // 计算出有多少块
        int samples = allData.length / 2;
        int blocks = writeWavHeader(output, samples);
        // 编码并写入所以块
        int pos = 0;
        for (int i = 0; i < blocks; i++) {
            int size = Math.min(BLOCKSAMPLES * 2, allData.length - pos);
            Block block = encodeBlock(allData, pos, size);
            output.write(block.getData());
            pos += size;
        }
        byte[] bytes = output.toByteArray();
        output.close();
        return bytes;
    }

    /**
     * 写一个60字节的wav文件的文件头。
     *
     * @param output 接收头文件的流
     * @param samples 样本数
     * @return 预期的块的数量
     * @throws IOException
     */
    public static int writeWavHeader(OutputStream output, int samples)
            throws IOException {
        int blocks = (samples + (BLOCKSAMPLES - 1)) / BLOCKSAMPLES;
        int bytes = blocks * BLOCKBYTES;
        // WAV头
        writeASCII(output, "RIFF");
        write4Byte(output, 52 + bytes); // 在这之后的文件大小
        writeASCII(output, "WAVE");
        writeASCII(output, "fmt ");
        write4Byte(output, 20); // 块数
        write2Byte(output, 17); // 格式
        write2Byte(output, 1); // 通道数
        write4Byte(output, SAMPLERATE); // 采样率
        write4Byte(output, (SAMPLERATE * BLOCKBYTES + BLOCKSAMPLES / 2) / BLOCKSAMPLES); // 每秒数据量
        write2Byte(output, BLOCKBYTES); // 块的大小
        write2Byte(output, 4);
        write2Byte(output, 2);
        write2Byte(output, BLOCKSAMPLES); // 样本块中的样本数
        writeASCII(output, "fact");
        write4Byte(output, 4);
        write4Byte(output, samples);
        writeASCII(output, "data");
        write4Byte(output, bytes); // 数据总长度
        return blocks;
    }

    private static void writeASCII(OutputStream o, String s) throws IOException {
        byte[] ascii = s.getBytes("US-ASCII");
        o.write(ascii);
    }

    private static void write4Byte(OutputStream o, int i) throws IOException {
        byte[] data = new byte[4];
        data[0] = (byte) (i & 0xff);
        data[1] = (byte) ((i >> 8) & 0xff);
        data[2] = (byte) ((i >> 16) & 0xff);
        data[3] = (byte) ((i >> 24) & 0xff);
        o.write(data);
    }

    private static void write2Byte(OutputStream o, int i) throws IOException {
        byte[] data = new byte[2];
        data[0] = (byte) (i & 0xff);
        data[1] = (byte) ((i >> 8) & 0xff);
        o.write(data);
    }

    /**
     * 类代表一块编码的数据。还存储统计信息块。
     */
    public static class Block {
        private byte[] data;
        private int maxLevel, minLevel;

        public byte[] getData() {
            return data;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public int getMinLevel() {
            return minLevel;
        }
    }

    /**
     * 编码的数据块在Windows块格式。
     *
     * @param data 数据
     * @param offset 偏移
     * @param length 长度
     * @return 编码块
     */
    public static Block encodeBlock(byte[] data, int offset, int length) {
        if (length < BLOCKSAMPLES * 2) {
            byte[] newData = new byte[BLOCKSAMPLES * 2];
            System.arraycopy(data, offset, newData, 0, length);
            data = newData;
            offset = 0;
            length = BLOCKSAMPLES * 2;
        } else if (length > BLOCKSAMPLES * 2) {
            throw new IllegalArgumentException("Cannot encode block larger than " +
                    BLOCKSAMPLES + " samples");
        }
        Block result = new Block();
        byte[] adpcm = new byte[BLOCKBYTES];
        result.data = adpcm;
        int outPos = 0;
        // 初始化未压缩数据
        int lastOutput = (int) data[offset] & 0xff | (int) data[1 + offset] << 8;
        adpcm[outPos++] = data[offset];
        adpcm[outPos++] = data[1 + offset];
        result.maxLevel = lastOutput;
        result.minLevel = lastOutput;
        // 初始步骤指数——让我们找到下一个样品,挑选最接近
        int nextSample = (int) data[2 + offset] & 0xff | (int) data[3 + offset] << 8;
        int initialDifference = Math.abs(nextSample - lastOutput);
        int stepIndex = 0;
        for (; stepIndex < ADPCM.STEPSIZE.length; stepIndex++) {
            if (ADPCM.STEPSIZE[stepIndex] > initialDifference) {
				break;
			}
        }
        if (stepIndex > 0) {
			stepIndex--;
		}
        adpcm[outPos++] = (byte) stepIndex;
        adpcm[outPos++] = 0;
        boolean highNibble = false;
        for (int i = 2; i < length; i += 2) {
            int target = (int) data[i + offset] & 0xff | (int) data[i + offset + 1] << 8;
            result.maxLevel = Math.max(result.maxLevel, target);
            result.minLevel = Math.min(result.minLevel, target);
            int difference = target - lastOutput;
            int step = ADPCM.STEPSIZE[stepIndex];
            int delta = (Math.abs(difference) << 2) / step;
            if (delta > 7) {
				delta = 7;
			}
            if (difference < 0) {
				delta |= 0x08;
			}
            if (highNibble) {
                adpcm[outPos++] |= (byte) ((delta & 0xf) << 4);
                highNibble = false;
            } else {
                adpcm[outPos] = (byte) (delta & 0xf);
                highNibble = true;
            }
            int deltaMagnitude = delta & 0x07;
            int valueAdjust = 0;
            if ((deltaMagnitude & 4) != 0) {
				valueAdjust += step;
			}
            step = step >> 1;
            if ((deltaMagnitude & 2) != 0) {
				valueAdjust += step;
			}
            step = step >> 1;
            if ((deltaMagnitude & 1) != 0) {
				valueAdjust += step;
			}
            step = step >> 1;
            valueAdjust += step;
            if (deltaMagnitude != delta) {
                lastOutput -= valueAdjust;
                if (lastOutput < -0x8000) {
					lastOutput = -0x8000;
				}
            } else {
                lastOutput += valueAdjust;
                if (lastOutput > 0x7fff) {
					lastOutput = 0x7fff;
				}
            }
            stepIndex += ADPCM.STEPINCREMENT_MAGNITUDE[deltaMagnitude];
            if (stepIndex < 0) {
				stepIndex = 0;
			} else if (stepIndex >= ADPCM.STEPSIZE.length) {
				stepIndex = ADPCM.STEPSIZE.length - 1;
			}
        }
        if (outPos != adpcm.length) {
			throw new Error("Unexpected buffer length mismatch");
		}
        return result;
    }
}

