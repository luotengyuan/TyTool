package com.lois.tytool.exif;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.lois.tytool.exif.adpcm.ADPCMDecoder;
import com.lois.tytool.exif.adpcm.ADPCMEncoder;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 * @Description TyExif工具类
 * @Author Luo.T.Y
 * @Date 2022/6/18
 * @Time 10:19
 */
public class TyExif {
    private static final String TAG = TyExif.class.getSimpleName();

    private double longitude;
    private double latitude;
    private double altitude;
    private int azimuth;
    private int pitch;
    private int roll;
    private int orientation;
    private String datetime;
    private String make;
    private String model;
    private int length;
    private int width;
    private String comment;
    private byte[] audioPcm;
    private int audioTime;

    private String path;
    private boolean isAudioModif = false;
    private ExifInterface exif = null;

    public TyExif(String path) {
        try {
            this.path = path;
            exif = new ExifInterface(path);
            readExif();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readExif() {
        try {
            if (exif != null) {
                // 读取经度纬度海拔高度
                String nLongitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                String nLatitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String nLongitudeREF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                String nLatitudeREF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                String nAltitude = exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE);
                String nAltitudeREF = exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF);
                if (nLongitude != null && nLatitude != null && nLongitudeREF != null && nLatitudeREF != null) {
                    if (nLongitudeREF.toUpperCase().equals("W")) {
                        longitude = -convertRationalLatLonToDouble(nLongitude);
                    } else {
                        longitude = convertRationalLatLonToDouble(nLongitude);
                    }
                    if (nLatitudeREF.toUpperCase().equals("S")) {
                        latitude = -convertRationalLatLonToDouble(nLatitude);
                    } else {
                        latitude = convertRationalLatLonToDouble(nLatitude);
                    }
                }
                if (nAltitude != null) {
                    String[] newAltitude = nAltitude.split("/");
                    if (nAltitudeREF == "1") {
                        altitude = -Double.parseDouble(newAltitude[0]) / 10000;
                    } else {
                        altitude = Double.parseDouble(newAltitude[0]) / 10000;
                    }
                }
                // 读取方位角 值的格式value/1
                String azimuthStr = exif.getAttribute(ExifInterface.TAG_FLASH);
                if (azimuthStr != null) {
                    String[] newAzi = azimuthStr.split("/");
                    azimuth = Integer.parseInt(newAzi[0]);
                }
                // 读取俯仰角 值的格式value/1
                String pitchStr = exif.getAttribute(ExifInterface.TAG_ISO);
                if (pitchStr != null) {
                    String[] newPitch = pitchStr.split("/");
                    pitch = Integer.parseInt(newPitch[0]) - 90;
                }
                // 读取翻滚角 值的格式value/1
                String rollStr = exif.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
                if (rollStr != null) {
                    String[] newRoll = rollStr.split("/");
                    roll = Integer.parseInt(newRoll[0]) - 90;
                }
                // 读取图片中相机方向信息
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                // 计算旋转角度
                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        orientation = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        orientation = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        orientation = 270;
                        break;
                    default:
                        orientation = 0;
                        break;
                }
                // 读取拍摄时间
                datetime = exif.getAttribute(ExifInterface.TAG_DATETIME);
                make = exif.getAttribute(ExifInterface.TAG_MAKE);
                model = exif.getAttribute(ExifInterface.TAG_MODEL);
                length = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
                width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
                // 读取用户定义数据
                String temp = exif.getAttribute(ExifInterface.TAG_USER_COMMENT);
                if (temp != null) {
                    comment = ASCIIToString(temp);
                }

                // 读取音频数据
                byte[] photoBuffer = readFile(path);
                if (hasAudio(photoBuffer)) {
                    byte[] adpcmBuffer = readAudioPhoto(photoBuffer);
                    ADPCMDecoder decord = new ADPCMDecoder();
                    decord.init(new ByteArrayInputStream(adpcmBuffer));
                    audioPcm = decord.decode();
                    audioTime = audioPcm == null ? 0 : audioPcm.length / 64000 * 2;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getLongitude() {
        return longitude;
    }

    public TyExif setLongitude(double longitude) {
        this.longitude = longitude;
        if (exif != null) {
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,
                    convertDoubleLatLonToRational(longitude));
            if (longitude >= 0) {
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
            } else {
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
            }
        }
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public TyExif setLatitude(double latitude) {
        this.latitude = latitude;
        if (exif != null) {
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE,
                    convertDoubleLatLonToRational(latitude));
            if (latitude >= 0) {
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
            } else {
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
            }
        }
        return this;
    }

    public double getAltitude() {
        return altitude;
    }

    public TyExif setAltitude(double altitude) {
        this.altitude = altitude;
        if (exif != null) {
            exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE,
                    convertDoubleLatLonToRational(altitude));
            if (altitude >= 0) {
                exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF, "0");
            } else {
                exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF, "1");
            }
        }
        return this;
    }

    public int getAzimuth() {
        return azimuth;
    }

    public TyExif setAzimuth(int azimuth) {
        this.azimuth = azimuth;
        if (exif != null) {
            // 设置方位角，用TAG_FOCAL_LENGTH来存储，(int)(360*Math.random())是得到一个随机的0-360之间的整数
            exif.setAttribute(ExifInterface.TAG_FLASH,
                    Integer.toString(azimuth));
        }
        return this;
    }

    public int getPitch() {
        return pitch;
    }

    public TyExif setPitch(int pitch) {
        this.pitch = pitch;
        if (exif != null) {
            // 可能是负数，加90消去负数，在读取时减去90
            exif.setAttribute(ExifInterface.TAG_ISO,
                    Integer.toString(pitch + 90));
        }
        return this;
    }

    public int getRoll() {
        return roll;
    }

    public TyExif setRoll(int roll) {
        this.roll = roll;
        if (exif != null) {
            // 可能是负数，加90消去负数，在读取时减去90
            exif.setAttribute(ExifInterface.TAG_WHITE_BALANCE,
                    Integer.toString(roll + 90));
        }
        return this;
    }

    public int getOrientation() {
        return orientation;
    }

    public TyExif setOrientation(int orientation) {
        this.orientation = orientation;
        if (exif != null) {
            int ori;
            switch (orientation) {
                case 90:
                    ori = ExifInterface.ORIENTATION_ROTATE_90;
                    break;
                case 180:
                    ori = ExifInterface.ORIENTATION_ROTATE_180;
                    break;
                case 270:
                    ori = ExifInterface.ORIENTATION_ROTATE_270;
                    break;
                default:
                    ori = ExifInterface.ORIENTATION_NORMAL;
                    break;
            }
            exif.setAttribute(ExifInterface.TAG_ORIENTATION, Integer.toString(ori));
        }
        return this;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public String getComment() {
        return comment;
    }

    public TyExif setComment(String comment) {
        this.comment = comment;
        if (exif != null) {
            exif.setAttribute(ExifInterface.TAG_USER_COMMENT, comment);
        }
        return this;
    }

    public byte[] getAudioPcm() {
        return audioPcm;
    }

    public TyExif setAudioPcm(byte[] audioPcm) {
        if (audioPcm != null) {
            this.audioPcm = audioPcm;
            this.audioTime = audioPcm.length / 64000;
            isAudioModif = true;
        }
        return this;
    }

    public int getAudioTime() {
        return audioTime;
    }

    public void commit() throws IOException {
        if (exif != null) {
            try {
                exif.saveAttributes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (isAudioModif) {
            isAudioModif = false;
            // 先进行ADPCM编码
            byte[] adpcnBuffer = ADPCMEncoder.encodeToByteArray(audioPcm);
            // 读取音频数据
            byte[] photoBuffer = readFile(path);
            if (hasAudio(photoBuffer)) {
                photoBuffer = delAudio(photoBuffer);
            }
            writeAudioToPhoto(adpcnBuffer, photoBuffer, path);
        }
    }

    @Override
    public String toString() {
        return "TyExif{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", altitude=" + altitude +
                ", azimuth=" + azimuth +
                ", pitch=" + pitch +
                ", roll=" + roll +
                ", orientation=" + orientation +
                ", datetime='" + datetime + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", length=" + length +
                ", width=" + width +
                ", comment='" + comment + '\'' +
                ", audioPcm=" + audioPcm +
                ", audioTime=" + audioTime +
                ", path='" + path + '\'' +
                ", isAudioModif=" + isAudioModif +
                ", exif=" + exif +
                '}';
    }


    public String toFormatString() {
        return "longitude=" + longitude +
                "\nlatitude=" + latitude +
                "\naltitude=" + altitude +
                "\nazimuth=" + azimuth +
                "\npitch=" + pitch +
                "\nroll=" + roll +
                "\norientation=" + orientation +
                "\ndatetime='" + datetime + '\'' +
                "\nmake='" + make + '\'' +
                "\nmodel='" + model + '\'' +
                "\nlength=" + length +
                "\nwidth=" + width +
                "\ncomment='" + comment + '\'' +
                "\naudioPcm=" + audioPcm +
                "\naudioTime=" + audioTime +
                "\npath='" + path + '\'' +
                "\nisAudioModif=" + isAudioModif +
                "\nexif=" + exif;
    }

    /**
     * 将转码后的ASCII字符转换成文本字符串
     *
     * @param string ASCII字符
     * @return 文本字符串
     */
    private String ASCIIToString(String string) {
        String result = null;
        try {
            byte[] asciiByte = string.getBytes("UTF-8");

            byte[] stringByte = new byte[asciiByte.length / 2];
            for (int i = 0; i < stringByte.length; i++) {
                stringByte[i] = (byte) ((asciiByte[i * 2 + 1] - 66) << 4 | (asciiByte[i * 2] - 66));
            }
            result = new String(stringByte, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 从给定的路径加载图片，并指定是否自动旋转方向
     *
     * @param imgpath      path
     * @param inSampleSize size
     * @return bitmap
     */
    public static Bitmap loadBitmap(String imgpath, int inSampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        Bitmap bm = BitmapFactory.decodeFile(imgpath, options);
        int digree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imgpath);
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
        if (exif != null) {
            // 读取图片中相机方向信息
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            // 计算旋转角度
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    digree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    digree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    digree = 270;
                    break;
                default:
                    digree = 0;
                    break;
            }
        }
        if (digree != 0) {
            // 旋转图片
            Matrix m = new Matrix();
            m.postRotate(digree);
            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
                    m, true);
        }
        return bm;
    }

// --------------------------- GPS信息格式转换 --------------------------- //

    private String convertDoubleLatLonToRational(double number) {
        double degree = Math.abs(number);
        int d = (int) degree;
        double second = getPoint60(degree);
        int dd = (int) second;
        double minute = getPoint60(second);

        return d + "/1," + dd + "/1," + minute + "/1";

    }

    private double getPoint60(double number) {
        // 整数部分
        int i = (int) number;
        BigDecimal b1 = new BigDecimal(Double.toString(number));
        // 整数部分
        BigDecimal b2 = new BigDecimal(Integer.toString(i));
        return b1.subtract(b2).multiply(new BigDecimal("60"))
                .doubleValue();
    }

    private double convertRationalLatLonToDouble(String lon) {
        String[] lonStrings = lon.split("/1,");

        if (lonStrings[2].endsWith("/1")) {
            return Double.parseDouble(lonStrings[0])
                    + Double.parseDouble(lonStrings[1]) / 60
                    + Double.parseDouble(lonStrings[2].split("/1")[0]) / 3600;
        } else {
            return Double.parseDouble(lonStrings[0])
                    + Double.parseDouble(lonStrings[1])
                    / 60
                    + (Double.parseDouble(lonStrings[2].split("/")[0]) / Double
                    .parseDouble(lonStrings[2].split("/")[1])) / 3600;
        }

    }

    // --------------------------- 图像添加音频相关 --------------------------- //

    /**
     * 判断一张图片是否是有声照片，如果是，返回true；如果不是，返回false。
     *
     * @param imagebuffer
     * @return
     */
    static boolean hasAudio(byte[] imagebuffer) {
        boolean result = false;
        int position = audioPosition(imagebuffer);
        if (imagebuffer[position + 1] == -1 && imagebuffer[position + 2] == -30) {
            result = true;
        }
        return result;
    }

    /**
     * 判断一张图片是否是有声照片，如果是，返回true；如果不是，返回false。
     *
     * @param imagePath
     * @return
     */
    static boolean hasAudio(String imagePath) {
        byte[] imagebuffer = readFile(imagePath);

        if (imagebuffer == null) {
            return false;
        }
        return hasAudio(imagebuffer);
    }

    /**
     * 把一个字节数组从下标startPisition删到下标endPosition，再把剩下的重新组成一个字节数组。
     *
     * @param imagebuffer   输入带有声音的图片字节数组
     * @param startPosition 开始下标
     * @param endPosition   结束下标
     * @return
     */
    static byte[] delete(byte[] imagebuffer, int startPosition, int endPosition) {
        byte[] newImageBuffer = new byte[imagebuffer.length - (endPosition - startPosition + 1)];
        System.arraycopy(imagebuffer, 0, newImageBuffer, 0, startPosition);
        System.arraycopy(imagebuffer, endPosition + 1, newImageBuffer, startPosition, imagebuffer.length - endPosition - 1);
        return newImageBuffer;
    }

    /**
     * 把一张有声照片中的音频数据删掉。
     *
     * @param imagebuffer 带有声音的照片字节数组
     * @return 没有声音在照片字节数组
     */
    static byte[] delAudio(byte[] imagebuffer) {
        int position = audioPosition(imagebuffer);
        int startPosition = position + 1;
        int endPosition = 0;
        int len;
        for (int i = position + 1, j = position + 2; imagebuffer[i] == -1
                && imagebuffer[j] == -30; ) {
            byte[] length = {imagebuffer[j + 1], imagebuffer[j + 2]};
            len = array2RightToInt(length) + 2;
            i += len;
            j += len;
            endPosition = i - 1;
        }
        return delete(imagebuffer, startPosition, endPosition);
    }

    /**
     * 判断FFE2音频数据插入位置，并返回这个位置的int数值。
     *
     * @param imagebuffer 原图片字节数组数据
     * @return
     */
    static int audioPosition(byte[] imagebuffer) {
        int position = 0;
        int len;
        for (int i = 2, j = 3; imagebuffer[i] == -1
                && (imagebuffer[j] == -32 || imagebuffer[j] == -31); ) {
            byte[] length = {imagebuffer[j + 1], imagebuffer[j + 2]};
            len = array2RightToInt(length) + 2;
            i += len;
            j += len;
            position = i - 1;
        }
        return position;

    }

    /**
     * 把一个文件转换成字节数组形式。
     *
     * @param mFileName 文件的全路径
     * @return 返回一个文件的字节数据
     */
    public static byte[] readFile(String mFileName) {
        byte[] bytes = null;
        try {
            FileInputStream fis = new FileInputStream(mFileName);
            int nCount = fis.available();
            if (nCount > 0) {
                bytes = new byte[nCount];
                int read = fis.read(bytes);
                if (read <= 0) {
                    return null;
                }
            }
            fis.close();
            return bytes;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }


    /**
     * 把一个长的字节数组按照指定长度分割成若干个子数组。
     *
     * @param ary     待分割的数组
     * @param subSize 指定的分割长度
     * @return 返回一个二维数组
     */

    static byte[][] splitAry(byte[] ary, int subSize) {
        int count = ary.length % subSize == 0 ? ary.length / subSize
                : ary.length / subSize + 1;

        byte[][] bytes = new byte[count][subSize];
        for (int i = 0; i < count - 1; i++) {
            int index = i * subSize;
            int j = 0;
            while (j < subSize && index < ary.length) {
                bytes[i][j] = ary[index];
                index++;
                j++;

            }
            byte[] b = null;
            if (ary.length % subSize != 0) {
                b = new byte[ary.length % subSize];
            } else {
                b = new byte[subSize];
            }
            int count1 = 0;
            for (int j1 = (count - 1) * subSize; j1 < ary.length; j1++) {
                b[count1] = ary[j1];
                count1++;
            }
            bytes[count - 1] = b;
        }
        return bytes;

    }

    /**
     * 把两个byte数组相加。
     *
     * @param a 数组1
     * @param b 数组2
     * @return 得到一个新的数组
     */

    static byte[] add(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    /**
     * 把字节数组b添加到数组a指定的index位置。插在下标index值a[index]后面。
     *
     * @param a     原数组
     * @param b     添加数组
     * @param index 插入位置
     * @return
     */
    static byte[] chaZhi(byte[] a, byte[] b, int index) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, index);
        System.arraycopy(b, 0, result, index, b.length);
        System.arraycopy(a, index, result, index + b.length, a.length - index);
        return result;
    }

    /**
     * 把一个int的值转换成两位byte形式
     *
     * @param length
     * @return
     */
    static byte[] intToArray(int length) {
        byte[] arr = new byte[2];
        arr[0] = (byte) ((length >>> 8) & 0xFF);
        arr[1] = (byte) (length & 0xFF);
        return arr;
    }

    /**
     * 把一个长度为2的byte数组转化为一个int长度，并且是高位在前，低位在后。
     *
     * @param length
     * @return
     */
    static int array2RightToInt(byte[] length) {
        int[] len = new int[2];
        for (int i = 0; i < len.length; i++) {
            if ((int) length[i] < 0) {
                len[i] = (int) length[i] + 256;
            } else {
                len[i] = (int) length[i];
            }
        }
        return (int) len[1] & 0xff | (int) (len[0] << 8);
    }

    /**
     * 把一个长度为2的byte数组转化为一个int长度，并且是低位在前，高位在后。
     *
     * @param length
     * @return
     */
    static int array2LeftToInt(byte[] length) {
        int[] len = new int[2];
        for (int i = 0; i < len.length; i++) {
            if ((int) length[i] < 0) {
                len[i] = (int) length[i] + 256;
            } else {
                len[i] = (int) length[i];
            }
        }

        return (int) len[0] | (int) len[1] << 8;
    }

    /**
     * 把一个长度为4的byte数组转化为一个int长度，并且是高位在前，低位在后。
     *
     * @param length
     * @return
     */
    static int array4RightToInt(byte[] length) {
        int[] len = new int[4];
        for (int i = 0; i < len.length; i++) {
            if ((int) length[i] < 0) {
                len[i] = (int) length[i] + 256;
            } else {
                len[i] = (int) length[i];
            }
        }
        return (int) len[3] | (int) len[2] << 8 | (int) len[1] << 16
                | (int) len[0] << 24;
    }

    /**
     * 把一个长度为4的byte数组转化为一个int长度，并且是低位在前，高位在后。
     *
     * @param length
     * @return
     */
    static int array4LeftToInt(byte[] length) {
        int[] len = new int[4];
        for (int i = 0; i < len.length; i++) {
            if ((int) length[i] < 0) {
                len[i] = (int) length[i] + 256;
            } else {
                len[i] = (int) length[i];
            }
        }
        return (int) len[0] | (int) len[1] << 8 | (int) len[2] << 16
                | (int) len[3] << 24;
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

    public static byte[] readAudioPhoto(byte[] photoBuffer) {
        int num = FFE2Counter(photoBuffer);
        byte[][] arrayData = null;
        if (num >= 2) {
            arrayData = new byte[num - 2][65517];
        }
        byte[] firstOne = null;
        byte[] lastOne = null;
        if (hasAudio(photoBuffer)) {
            int position = audioPosition(photoBuffer);
            int len;
            int counter = 0;
            for (int i = position + 1, j = position + 2; photoBuffer[i] == -1
                    && photoBuffer[j] == -30; ) {
                byte[] length = {photoBuffer[j + 1], photoBuffer[j + 2]};
                len = array2RightToInt(length) + 2;
                if (counter == 0) {
                    firstOne = new byte[len - 45];
                    int startPosition = i + 45;
                    int dataLength = len - 45;
                    System.arraycopy(photoBuffer, startPosition, firstOne, 0,
                            dataLength);
                } else if (counter == num - 1) {
                    lastOne = new byte[len - 17];
                    int startPosition = i + 17;
                    int dataLength = len - 17;
                    System.arraycopy(photoBuffer, startPosition, lastOne, 0,
                            dataLength);
                } else {
                    int startPosition = i + 17;
                    int dataLength = len - 17;
                    System.arraycopy(photoBuffer, startPosition,
                            arrayData[counter - 1], 0, dataLength);
                }
                i += len;
                j += len;
                counter++;
            }
        }
        if (arrayData == null) {
            if (lastOne == null) {
                return firstOne;
            } else {
                return add(firstOne, lastOne);
            }
        } else {
            byte[] middle = addAll(arrayData);
            byte[] addFirst = add(firstOne, middle);
            byte[] audioBuffer = add(addFirst, lastOne);
            return audioBuffer;
        }

    }

    public static int FFE2Counter(byte[] photoBuffer) {
        int len;
        int counter = 0;
        int position = audioPosition(photoBuffer);
        for (int i = position + 1, j = position + 2; photoBuffer[i] == -1
                && photoBuffer[j] == -30; ) {
            byte[] length = {photoBuffer[j + 1], photoBuffer[j + 2]};
            len = array2RightToInt(length) + 2;
            i += len;
            j += len;
            counter++;
        }
        return counter;
    }

    public void writeAudioToPhoto(byte[] audioBuffer, byte[] photoBuffer, String path) {
        byte[] newPhotoData;
        // 将音频写入FFE2段中
        byte[] newAudioData = editAudioData(audioBuffer);
        // 判断照片是否为有声照片
        if (!hasAudio(photoBuffer)) {
            newPhotoData = chaZhi(photoBuffer, newAudioData,
                    audioPosition(photoBuffer) + 1);
            File file = new File(path);
            try {
                if (file.exists()) {
                    file.delete();
                } else {
                    file.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(newPhotoData);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                System.err.println("FileNoFound");
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("已经是有声照片");
        }
    }

    /**
     * 编辑FFE2段内容。
     *
     * @param audioBuffer
     * @return
     */
    public byte[] editAudioData(byte[] audioBuffer) {
        // 下面这个文件头加在第一段FFE2段的音频数据之前，ACDSee软件是这样做的，不知道存的什么数据。
        byte[] audioHeader = {-2, -1, 00, 00, 00, 04, 02, 00, 01, 01, 00, 16,
                -64, 111, -48, 17, -67, 01, 00, 96, -105, 25, -95, -128, 01,
                00, 00, 00};
        byte[] newAudioBuffer = add(audioHeader, audioBuffer);
        byte[] APP2Marker;
        if (newAudioBuffer.length > 0xFFED) { // 判断音频数据是否大于64KB
            byte[][] subAry = splitAry(newAudioBuffer, 0xFFED);// 如果大于，需要将数据分成多段存储
            byte[][] newSubAry = new byte[subAry.length][subAry[0].length + 17];
            /* 制作FFE2的头文件,得到一个新的二维数组 */
            for (int i = 0; i < subAry.length; i++) {
                byte[] header = new byte[17];
                int length = subAry[i].length + 15;
                byte[] lengthByte = intToArray(length);
                header[0] = -1; // 0xFF
                header[1] = -30; // 0xE2
                header[2] = lengthByte[0]; // 该段长度
                header[3] = lengthByte[1];
                header[4] = 0x46; // ‘F’
                header[5] = 0x50; // ‘P’
                header[6] = 0x58; // ‘X’
                header[7] = 0x52; // ‘R’
                header[8] = 0x00;
                header[9] = 0x00;
                header[10] = 0x02; // 流数据存储标识
                header[11] = 0x00; // 索引内容列表
                header[12] = 0x04;
                header[13] = 0x00; // 偏移量
                header[14] = 0x00;
                header[15] = 0x00;
                header[16] = 0x00;
                newSubAry[i] = add(header, subAry[i]);// 添加音频数据
            }
            APP2Marker = addAll(newSubAry); // 将所有段相加
        } else { // 如果小于64KB，就只要一个APP2段保存
            byte[] header = new byte[17];
            int length = newAudioBuffer.length + 15; // 计算该段长度
            byte[] lengthByte = intToArray(length);
            header[0] = -1; // 0xFF
            header[1] = -30; // 0xE2
            header[2] = lengthByte[0]; // 该段长度
            header[3] = lengthByte[1];
            header[4] = 0x46; // ‘F’
            header[5] = 0x50; // ‘P’
            header[6] = 0x58; // ‘X’
            header[7] = 0x52; // ‘R’
            header[8] = 0x00;
            header[9] = 0x00;
            header[10] = 0x02; // 流数据存储标识
            header[11] = 0x00; // 索引内容列表
            header[12] = 0x04;
            header[13] = 0x00; // 偏移量
            header[14] = 0x00;
            header[15] = 0x00;
            header[16] = 0x00;
            APP2Marker = add(header, newAudioBuffer);
        }
        return APP2Marker; // 返回APP2段数据
    }
}
