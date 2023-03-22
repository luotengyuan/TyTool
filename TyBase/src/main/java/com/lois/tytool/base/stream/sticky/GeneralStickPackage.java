package com.lois.tytool.base.stream.sticky;

import com.lois.tytool.base.debug.TyLog;
import com.lois.tytool.base.string.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 通用分包协议（采用7EH表示，如果校验码、命令类型以及用户数据字段中出现7EH，则用7DH和02H表示，如果出现7DH，则用7DH和01H表示）
 * 字段内容      长度
 * 标志	        1字节
 * 校验码	    1字节
 * 用户数据	    N字节
 * 标志	        1字节
 * @Author Luo.T.Y
 * @Date 2023/3/7
 * @Time 20:17
 */
public class GeneralStickPackage implements IStickPackage {
    private static final String TAG = GeneralStickPackage.class.getSimpleName();

    /**
     * 数据缓冲区大小
     */
    public int mMaxSize = 10240;
    /**
     * 数据缓冲区
     */
    private byte[] mRevDataCache;
    /**
     * 解析数据的状态位
     * 0：表示未检测到帧头
     * 1：表示已检测到帧头
     * 2：表示在帧头和帧尾之间的数据部分
     * 3：表示已检测到帧尾
     */
    private int detectState = 0;
    /**
     * 起始处理数据位置
     */
    private int cursor = 0;

    /**
     * 错误计数
     */
    public int mErrCount = 0;

    public GeneralStickPackage() {
        mRevDataCache = new byte[mMaxSize];
    }

    public GeneralStickPackage(int bufSize) {
        mMaxSize = bufSize;
        mRevDataCache = new byte[mMaxSize];
    }

    @Override
    public List<byte[]> unPack(byte[] data, int len) {
        if (data == null || len <= 0) {
            return null;
        }
        len = Math.min(data.length, len);

        List<byte[]> ret = new ArrayList<>();

        try {
            for (int i = 0; i < len && i < data.length; i++) {
                if (data[i] == 0x7E) {
                    if (detectState == 0 || detectState == 1) {
                        detectState = 1;
                        continue;
                    } else if (detectState == 2 || detectState == 3) {
                        detectState = 3;
                    }
                }
                if (detectState == 0) {
                    continue;
                }
                if (detectState == 1) {
                    // 紧接帧头的数据部分开始
                    detectState = 2;
                    cursor = 0;
                    mRevDataCache[cursor++] = data[i];
                    continue;
                }
                if (detectState == 2) {
                    // 帧头和帧尾之间，记录数据
                    mRevDataCache[cursor++] = data[i];
                    continue;
                }
                if (detectState == 3) {
                    // 帧尾，判断接收的数据完整性和正确性
                    detectState = 0;
                    // 协议数据中包括字段内容	长度最少也有5个，如果少于5则说明有问题
                    // 标志      	1字节
                    // 校验码     	1字节
                    // 用户数据    	N字节
                    // 标志      	1字节
                    if (cursor < 2) {
                        byte[] err = new byte[cursor];
                        System.arraycopy(mRevDataCache, 0, err, 0, cursor);
                        TyLog.e(TAG, "ERROR : 协议长度不符合  cursor = " + cursor + ";  err = " + StringUtils.bytesToHexString(err));
                        mErrCount++;
                    } else {
                        byte[] escapesData = escapesData(mRevDataCache, cursor);
                        if (escapesData == null) {
                            // 说明转义失败
                            byte[] err = new byte[cursor];
                            System.arraycopy(mRevDataCache, 0, err, 0, cursor);
                            TyLog.e(TAG, "ERROR : 转义失败;  err = " + StringUtils.bytesToHexString(err));
                            mErrCount++;
                        } else {
                            // 校验和验证
                            byte checkSum = sumCheck(escapesData, 1, escapesData.length, 1)[0];
                            if (escapesData[0] != checkSum) {
                                // 说明检验失败
                                byte[] err = new byte[cursor];
                                System.arraycopy(mRevDataCache, 0, err, 0, cursor);
                                TyLog.e(TAG, "ERROR : 校验失败;  err = " + StringUtils.bytesToHexString(err));
                                mErrCount++;
                            } else {
                                byte[] megData = new byte[escapesData.length - 1];
                                System.arraycopy(escapesData, 1, megData, 0, escapesData.length - 1);
                                ret.add(megData);
                            }
                        }
                    }
                }
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * 校验和
     *
     * @param buf    需要计算校验和的byte数组
     * @param offset 需要计算校验和的byte数组偏移量
     * @param bufLen 需要计算校验和的byte数组从偏移位置开始的长度
     * @param length 校验和位数
     * @return 计算出的校验和数组
     */
    private static byte[] sumCheck(byte[] buf, int offset, int bufLen, int length) {
        long mSum = 0;
        byte[] mByte = new byte[length];

        /** 逐Byte添加位数和 */
        for (int i = offset; i < bufLen; i++) {
            byte byteMsg = buf[i];
            long mNum = ((long) byteMsg >= 0) ? (long) byteMsg : ((long) byteMsg + 256);
            mSum += mNum;
        } /** end of for (byte byteMsg : msg) */

        /** 位数和转化为Byte数组 */
        for (int liv_Count = 0; liv_Count < length; liv_Count++) {
            mByte[length - liv_Count - 1] = (byte) (mSum >> (liv_Count * 8) & 0xff);
        } /** end of for (int liv_Count = 0; liv_Count < length; liv_Count++) */

        return mByte;
    }

    /**
     * 将用7DH 01H和用7DH 02H转义的数据改回用7DH和7EH表示
     *
     * @return
     */
    private static byte[] escapesData(byte[] buf, int len) {
        byte[] temp = new byte[len];
        boolean success = true;
        int count = 0;
        for (int j = 0; j < len; j++) {
            if (buf[j] == 0x7D) {
                if (j == len - 1) {
                    // 说明0x7D后面没跟数据了，与协议不符，说明有问题
                    success = false;
                    break;
                } else if (buf[j + 1] == 0x01) {
                    // 0x7D后跟0x01说明是0x7D转义的
                    temp[j - count] = 0x7D;
                    j++;
                    count++;
                } else if (buf[j + 1] == 0x02) {
                    // 0x7D后跟0x01说明是0x7D转义的
                    temp[j - count] = 0x7E;
                    j++;
                    count++;
                } else {
                    // 说明0x7D后面跟的不是0x01或者0x02数据，与协议不符，说明有问题
                    success = false;
                    break;
                }
            } else {
                temp[j - count] = buf[j];
            }
        }
        if (success) {
            byte[] data = new byte[len - count];
            System.arraycopy(temp, 0, data, 0, data.length);
            return data;
        } else {
            return null;
        }
    }

    @Override
    public byte[] doPack(byte[] data, int len) {
        if (data == null || len <= 0) {
            return null;
        }
        len = Math.min(data.length, len);

        byte[] temp = new byte[mMaxSize];
        // 计算校验和
        byte[] sumCheck = sumCheck(data, 0, len, 1);
        temp[0] = 0x7E;
        temp[1] = sumCheck[0];
        int idx = 2;
        for (int i = 0; i < data.length && i < len; i++) {
            byte b = data[i];
            switch (b) {
                case 0x7E:
                    temp[idx++] = 0x7D;
                    temp[idx++] = 0x02;
                    break;
                case 0x7D:
                    temp[idx++] = 0x7D;
                    temp[idx++] = 0x01;
                    break;
                default:
                    temp[idx++] = b;
                    break;
            }
        }
        temp[idx++] = 0x7E;
        byte[] raw = new byte[idx];
        System.arraycopy(temp, 0, raw, 0, idx);
        return raw;
    }
}
