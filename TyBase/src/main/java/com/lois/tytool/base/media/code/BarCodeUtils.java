package com.lois.tytool.base.media.code;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.lois.tytool.base.debug.TyLog;
import com.lois.tytool.base.io.FileUtils;
import com.lois.tytool.base.io.IOUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

import javax.imageio.ImageIO;

/**
 * 条形码生成工具类
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class BarCodeUtils {

    private BarCodeUtils(){

    }
    /**
     * 条形码编码，默认宽高 只支持13位商品条形码
     * @param contents 内容
     * @param imgPath 保存图片路径(可以含后缀)
     * @throws NoSuchFileException 找不到文件异常
     */
    public static void encodeBarCodeEan13(String contents, String imgPath) throws NoSuchFileException {
        encodeBarCodeEan13(contents, 0, 0, imgPath);
    }
    /**
     * 条形码编码
     * @param contents 内容
     * @param width 宽度
     * @param height 高度
     * @param imgPath 保存图片路径(可以含后缀)
     * @throws NoSuchFileException 找不到文件异常
     */
    public static void encodeBarCodeEan13(String contents, int width, int height, String imgPath) throws NoSuchFileException {
        String pageType = FileUtils.getFileType(imgPath);
        int startGuard = 3;
        int leftBars = 7 * 6;
        int middleGuard = 5;
        int rightBars = 7 * 6;
        int endGuard = 3;
        int codeWidth = startGuard + leftBars + middleGuard + rightBars + endGuard;
        codeWidth = Math.max(codeWidth, width);
        BitMatrix bitMatrix = null;
        FileOutputStream stream = null;
        try {
            bitMatrix = new MultiFormatWriter().encode(contents,
                    BarcodeFormat.EAN_13, codeWidth, height, null);
            stream = new FileOutputStream(imgPath);
            MatrixToImageWriter.writeToStream(bitMatrix, pageType, stream);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        } finally {
            if(bitMatrix != null){
                bitMatrix.clear();
            }
            IOUtils.close(stream);
        }
    }

    /**
     * 解析条形码
     * @param imgPath 条形码图片路径
     * @return 条形码内容
     */
    public static String decodeBarCode(String imgPath) {
        BufferedImage image = null;
        Result result;
        MultiFormatReader multiReader = new MultiFormatReader();
        try {
            image = ImageIO.read(new File(imgPath));
            if (image == null) {
                throw new RuntimeException("条形码图片" + imgPath + "解析失败！");
            }
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            result = multiReader.decode(bitmap, null);
            return result.getText();
        } catch (IOException e) {
            TyLog.e(e.getMessage(), e);
            TyLog.e("解析条形码【{}】失败！", imgPath);
            return null;
        } catch (NotFoundException e) {
            TyLog.e(e.getMessage(), e);
            TyLog.e("条形码图片路径：【{}】不存在！", imgPath);
            return null;
        } finally {
            if(image != null){
                image.flush();
            }
            if(multiReader != null){
                multiReader.reset();
            }
        }
    }
}
