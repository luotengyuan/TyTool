package com.lois.tytool.exif.adpcm;

import java.io.IOException;
import java.io.InputStream;

/**
 * ADPCM解码器
 *
 * @author Lois
 */
public class ADPCMDecoder implements StreamableDecoder {
    /**
     * 编码数据块
     *
     * @param adpcm 数据块
     * @param offset 偏移量
     * @return ADPCM数据
     */
    public static byte[] decodeBlock(byte[] adpcm, int offset) {
        byte[] data = new byte[ADPCMEncoder.BLOCKSAMPLES * 2];
        int outPos = 0, inPos = offset;
        data[outPos++] = adpcm[inPos++];
        data[outPos++] = adpcm[inPos++];
        int lastOutput = (int) data[0] & 0xff | (int) data[1] << 8;
        int stepIndex = (int) adpcm[inPos++];
        inPos++;
        boolean highNibble = false;
        for (int i = 1; i < ADPCMEncoder.BLOCKSAMPLES; i++) {
            int delta;
            if (highNibble) {
                delta = (int) (((adpcm[inPos] & 0xf0) << 24) >> 28);
                highNibble = false;
                inPos++;
            } else {
                delta = (int) (((adpcm[inPos] & 0xf) << 28) >> 28);
                highNibble = true;
            }
            int step = ADPCM.STEPSIZE[stepIndex];
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
            data[outPos++] = (byte) (lastOutput & 0xff);
            data[outPos++] = (byte) ((lastOutput >> 8) & 0xff);
        }
        return data;
    }

    private InputStream stream;
    private byte[] header = null;

    static byte[] add(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    /**
     * 把一个二维数组里面的一维数组相加，得到一个长的一维数组。
     *
     * @param audioData 二维数组
     * @return
     */
    static byte[] addAll(byte[][] audioData) {
        int length = 0;
        for (byte[] audioDatum : audioData) {
            length += audioDatum.length;
        }
        byte[] result = new byte[length];
        for (int i = 0; i < audioData.length; i++) {
            System.arraycopy(audioData[i], 0, result, i * audioData[0].length,
                    audioData[i].length);
        }
        return result;
    }

    @Override
    public void init(InputStream is) throws AudioException {
        this.stream = is;
    }

    @Override
    public byte[] decode() throws AudioException {
        int size;
        byte[][] block;
        try {
            size = stream.available();
            block = new byte[(size - 60) / 512][ADPCMEncoder.BLOCKBYTES];

            if (header == null) {
                header = new byte[60];
                int headerPos = 0;
                do {
                    int read = stream.read(header, headerPos, header.length
                            - headerPos);
                    if (read == -1) {
                        throw new AudioException(
                                "Unexpected EOF in ADPCM header");
                    }
                    headerPos += read;
                } while (headerPos != header.length);
                if (header[20] != 17) {
                    throw new AudioException(
                            "Does not appear to be ADPCM WAV file");
                }
            }
            // Read block of data
            byte[] input = new byte[ADPCMEncoder.BLOCKBYTES];
            int pos = 0;
            int i = 0;
            do {
                pos = 0;
                int read = stream.read(input, pos, input.length - pos);
                if (read == -1) {
                    stream.close();
                }
                pos += read;
                if (i < (size - 60) / 512) {
                    block[i] = decodeBlock(input, 0);
                }
                i++;
            } while (pos == input.length);
            // 把每个数据块相加成完整一个数据
            // 放回解码后的全部数据
            return addAll(block);
        } catch (IOException e) {
            throw new AudioException(e);
        }
    }
}
