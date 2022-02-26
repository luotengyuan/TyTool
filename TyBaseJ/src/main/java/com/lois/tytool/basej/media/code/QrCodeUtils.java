package com.lois.tytool.basej.media.code;

import com.google.zxing.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.lois.tytool.basej.constant.FileConstants;
import com.lois.tytool.basej.io.FileUtils;
import com.lois.tytool.basej.media.ImageUtils;
import com.lois.tytool.basej.string.StringUtils;
import com.swetake.util.Qrcode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import jp.sourceforge.qrcode.QRCodeDecoder;
import jp.sourceforge.qrcode.data.QRCodeImage;
import jp.sourceforge.qrcode.exception.DecodingFailedException;

/**
 * 二维码生成工具类
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class QrCodeUtils {

    private static Logger logger = LoggerFactory.getLogger(QrCodeUtils.class);

    /**
     * 二维码颜色
     */
    private static final int QRCOLOR = 0xFF000000;
    /**
     * 背景色
     */
    private static final int BGCOLOR = 0xFFFFFFFF;
    /**
     * 二维码宽度
     */
    private static final int WIDTH = 400;
    /**
     * 二维码默认高度
     */
    private static final int HEIGHT = 400;
    /**
     * 二维码白边宽度
     */
    private static final int WHITE_WIDTH = 2;

    private QrCodeUtils() {
    }

    /**
     * 输出普通二维码，默认宽高
     * @param content 二维码内容
     * @param fileFullPath 输出图片全路径，含后缀
     * @throws IOException io异常
     */
    public static void encodeCode(String content, String fileFullPath) throws IOException {
        encodeCode(content, WIDTH, HEIGHT, fileFullPath);
    }
    /**
     * 输出普通二维码，指定宽高
     * @param content 二维码内容
     * @param width 指定宽度
     * @param height 指定高度
     * @param fileFullPath 输出图片全路径，含后缀（例如：D:/a/b/c.png）
     * @throws IOException io异常
     */
    public static void encodeCode(String content, int width, int height, String fileFullPath) throws IOException {
        //获取图片后缀
        String pageType = FileUtils.getFileType(fileFullPath);
        BufferedImage bim = createCode(content, width, height, WHITE_WIDTH);
        ImageIO.write(bim, pageType, new File(fileFullPath));
    }

    /**
     * 输出带logo二维码，默认宽高
     * @param content 二维码内容
     * @param fileFullPath 输出图片全路径，含后缀（例如：D:/a/b/c.png不带后缀则默认为png格式）
     * @param logoPath logo图片全路径
     * @throws IOException io异常
     */
    public static void encodeCodeByLogo(String content, String fileFullPath, String logoPath) throws IOException {
        encodeCodeByLogo(content, WIDTH, HEIGHT, fileFullPath, logoPath);
    }

    /**
     * 输出带logo二维码，指定宽高
     * @param content 二维码内容
     * @param width 指定宽度
     * @param height 指定高度
     * @param fileFullPath 输出图片全路径，含后缀（例如：D:/a/b/c.png不带后缀则默认为png格式）
     * @param logoPath logo图片全路径
     * @throws IOException io异常
     */
    public static void encodeCodeByLogo(String content,int width, int height, String fileFullPath, String logoPath) throws IOException {
        //获取图片后缀
        String pageType = FileUtils.getFileType(fileFullPath);
        BufferedImage bim = createCodeWithLogo(content, width, height, logoPath);
        ImageIO.write(bim, pageType, new File(fileFullPath));
    }

    /**
     * 输出带logo和文字的二维码，默认宽高
     * @param content 二维码内容
     * @param fileFullPath 输出图片全路径，含后缀（例如：D:/a/b/c.png不带后缀则默认为png格式）
     * @param logoPath logo图片全路径
     * @param title 二维码标题
     * @throws IOException io异常
     */
    public static void encodeCodeByLogoAndTitle(String content, String fileFullPath, String logoPath, String title) throws IOException {
        encodeCodeByLogoAndTitle(content, WIDTH, HEIGHT, fileFullPath, logoPath, title);
    }
    /**
     * 输出带logo和文字的二维码，指定宽高
     * @param content 二维码内容
     * @param width 指定宽度
     * @param height 指定高度
     * @param fileFullPath 输出图片全路径，含后缀（例如：D:/a/b/c.png不带后缀则默认为png格式）
     * @param logoPath logo图片全路径
     * @param title 二维码标题
     * @throws IOException io异常
     */
    public static void encodeCodeByLogoAndTitle(String content,int width, int height, String fileFullPath, String logoPath, String title) throws IOException {
        //获取图片后缀
        String pageType = FileUtils.getFileType(fileFullPath);
        BufferedImage bim = createCodeWithLogoAndText(content,width, height, logoPath, title);
        ImageIO.write(bim, pageType, new File(fileFullPath));
    }


    /**
     * 生成普通的二维码，使用默认的宽高
     * @param qrUrl 二维码内容
     * @return 二维码
     */
    public static BufferedImage createCode(String qrUrl) {
        return createCode(qrUrl, WIDTH, HEIGHT, WHITE_WIDTH);
    }

    /**
     * 生成普通的二维码
     * @param qrUrl 二维码内容
     * @param width 指定二维码宽度
     * @param height 指定二维码高度
     * @return 二维码
     */
    public static BufferedImage createCode(String qrUrl, int width, int height) {
        return createCode(qrUrl, width, height, WHITE_WIDTH);
    }

    /**
     * 生成普通二维码
     * @param qrUrl 二维码内容
     * @param width 指定二维码宽度
     * @param height 指定二维码高度
     * @param whiteWidth 指定二维码白边宽度
     * @return 二维码
     */
    private static BufferedImage createCode(String qrUrl, int width, int height, int whiteWidth) {
        MultiFormatWriter multiFormatWriter = null;
        BitMatrix bm = null;
        BufferedImage image = null;
        Map<EncodeHintType, Object> hints = getDecodeHintType(whiteWidth);
        try {
            multiFormatWriter = new MultiFormatWriter();
            // 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
            bm = multiFormatWriter.encode(qrUrl, BarcodeFormat.QR_CODE, width, height, hints);
            int w = bm.getWidth();
            int h = bm.getHeight();
            image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            // 开始利用二维码数据创建Bitmap图片，分别设为黑（0xFFFFFFFF）白（0xFF000000）两色
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    image.setRGB(x, y, bm.get(x, y) ? QRCOLOR : BGCOLOR);
                }
            }
        } catch (WriterException e) {
            logger.error(e.getMessage(), e);
            logger.error("二维码图片生成失败！");
            return null;
        } finally {
            if(image != null){
                image.flush();
            }
            if(bm != null){
                bm.clear();
            }
        }
        return image;
    }

    /**
     * 设置二维码的格式参数
     * @param whiteWidth 指定二维码白边宽度
     * @return QR二维码参数
     */
    private static Map<EncodeHintType, Object> getDecodeHintType(int whiteWidth) {
        // 用于设置QR二维码参数
        Map<EncodeHintType, Object> hints = new HashMap<>(8);
        // 设置QR二维码的纠错级别（H为最高级别）具体级别信息
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 设置编码方式
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //控制白边宽度
        hints.put(EncodeHintType.MARGIN, whiteWidth);
        hints.put(EncodeHintType.MAX_SIZE, 350);
        hints.put(EncodeHintType.MIN_SIZE, 100);

        return hints;
    }
    /**
     * 生成带logo的二维码图片流
     * @param qrUrl 二维码内容
     * @param width 指定宽度
     * @param height 指定高度
     * @param logoPath logo图片全路径
     * @return 二维码
     */
    private static BufferedImage createCodeWithLogo(String qrUrl,int width, int height, String logoPath) {
        BufferedImage bim = null;
        if(width <= 0 || height <= 0){
            bim = createCode(qrUrl, WIDTH, HEIGHT, WHITE_WIDTH);
        }else {
            bim = createCode(qrUrl, width, height, WHITE_WIDTH);
        }
        // 读取二维码图片，并构建绘图对象
        BufferedImage image = bim;
        BufferedImage logo = null;
        try {

            Graphics2D g = image.createGraphics();

            // 读取Logo图片
            logo = ImageIO.read(new File(logoPath));
            //设置logo的大小,这里设置为二维码图片的20%,过大会盖掉二维码
            int widthLogo = logo.getWidth(null) > image.getWidth() * 3 / 10 ? (image.getWidth() * 3 / 10) : logo.getWidth(null),
                    heightLogo = logo.getHeight(null) > image.getHeight() * 3 / 10 ? (image.getHeight() * 3 / 10) : logo.getWidth(null);

            // logo放在中心
            int x = (image.getWidth() - widthLogo) / 2;
            int y = (image.getHeight() - heightLogo) / 2;

            //开始绘制图片
            g.drawImage(logo, x, y, widthLogo, heightLogo, null);
            g.dispose();
            logo.flush();
            image.flush();
            return image;
        }catch (IOException e) {
            logger.error(e.getMessage(), e);
            logger.error("生成带logo的二维码图片流失败！");
            return null;
        } finally {
            if(logo != null){
                logo.flush();
            }
            if(image != null){
                image.flush();
            }
        }
    }

    /**
     * 生成带logo的二维码图片流
     * @param qrUrl 二维码内容
     * @param logoPath logo图片全路径
     * @return 二维码
     */
    public static BufferedImage createCodeWithLogo(String qrUrl, String logoPath) {
        return createCodeWithLogo(qrUrl, 0, 0, logoPath);
    }

    /**
     * 生成带logo和文字的二维码图片流 指定宽高
     * @param qrUrl 二维码内容
     * @param width 指定宽度
     * @param height 指定高度
     * @param logoPath logo图片全路径
     * @param text 二维码名称
     * @return 二维码
     */
    private static BufferedImage createCodeWithLogoAndText(String qrUrl, int width, int height, String logoPath, String text) {
        BufferedImage image = createCodeWithLogo(qrUrl, width, height, logoPath);
        //把文字添加上去，文字不要太长，这里最多支持两行。太长就会自动截取啦
        if (StringUtils.isNotBlank(text)) {
            //新的图片，把带logo的二维码下面加上文字
            BufferedImage outImage = new BufferedImage(400, 445, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D outg = outImage.createGraphics();
            //画二维码到新的面板
            outg.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
            //画文字到新的面板
            outg.setColor(Color.BLACK);
            //字体、字型、字号
            outg.setFont(new Font("宋体", Font.BOLD, 30));
            int strWidth = outg.getFontMetrics().stringWidth(text);
            if (strWidth > 399) {
                //长度过长就截取前面部分
                //outg.drawString(productName, 0, image.getHeight() + (outImage.getHeight() - image.getHeight())/2 + 5 ); //画文字
                //长度过长就换行
                String productName1 = text.substring(0, text.length() / 2);
                String productName2 = text.substring(text.length() / 2, text.length());
                int strWidth1 = outg.getFontMetrics().stringWidth(productName1);
                int strWidth2 = outg.getFontMetrics().stringWidth(productName2);
                outg.drawString(productName1, 200 - strWidth1 / 2, image.getHeight() + (outImage.getHeight() - image.getHeight()) / 2 + 12);
                BufferedImage outImage2 = new BufferedImage(400, 485, BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D outg2 = outImage2.createGraphics();
                outg2.drawImage(outImage, 0, 0, outImage.getWidth(), outImage.getHeight(), null);
                outg2.setColor(Color.BLACK);
                //字体、字型、字号
                outg2.setFont(new Font("宋体", Font.BOLD, 30));
                outg2.drawString(productName2, 200 - strWidth2 / 2, outImage.getHeight() + (outImage2.getHeight() - outImage.getHeight()) / 2 + 5);
                outg2.dispose();
                outImage2.flush();
                outImage = outImage2;
            } else {
                //画文字
                outg.drawString(text, 200 - strWidth / 2, image.getHeight() + (outImage.getHeight() - image.getHeight()) / 2 + 12);
            }
            outg.dispose();
            outImage.flush();
            image = outImage;
            image.flush();
        }
        return image;
    }

    /**
     * 生成带logo和文字的二维码图片流 默认宽高
     * @param qrUrl 二维码内容
     * @param logoPath logo图片全路径
     * @param text 二维码名称
     * @return 二维码
     */
    public static BufferedImage createCodeWithLogoAndText(String qrUrl, String logoPath, String text) {
        return createCodeWithLogoAndText(qrUrl, 0, 0, logoPath, text);
    }

    /**
     * 获取二维码输出内容
     * @param content
     * version 共有版本号1-40
     * @return
     */
    private static boolean[][] getCode(String content){
        boolean[][] result = null;
        Qrcode qrcode = new Qrcode();
        /*
         *设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，
         *但对二维码清晰度的要求越小
         */
        qrcode.setQrcodeErrorCorrect('M');
        //编码模式：Numeric 数字, Alphanumeric 英文字母,Binary 二进制,Kanji 汉字(第一个大写字母表示)
        qrcode.setQrcodeEncodeMode('B');
		/*
	            二维码的版本号：也象征着二维码的信息容量；二维码可以看成一个黑白方格矩阵，版本不同，矩阵长宽方向方格的总数量分别不同。
	     1-40总共40个版本，版本1为21*21矩阵，版本每增1，二维码的两个边长都增4；
	            版本2 为25x25模块，最高版本为是40，是177*177的矩阵；
       */
        qrcode.setQrcodeVersion(7);
        try {
            byte[] contentBytes = content.getBytes("utf-8");
            if (contentBytes.length > 0 && contentBytes.length < 120) {
                result = qrcode.calQrcode(contentBytes);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * BufferedImage加载二维码输出内容并获取BufferedImage对象
     * @param content
     * @param logo 若为null，则表示不带logo，否则输入logo路径
     * @return
     */
    public static BufferedImage getImage(String content, String logo){
        boolean[][] codeOut = getCode(content);
        BufferedImage img = new BufferedImage(140, 140, BufferedImage.TYPE_INT_RGB);
        Graphics2D gs = img.createGraphics();

        gs.setBackground(Color.WHITE);
        gs.clearRect(0, 0, 140, 140);
        // 设定图像颜色> BLACK
        gs.setColor(Color.BLACK);
        // 设置偏移量 不设置可能导致解析出错
        int off = 2;
        // 输出内容> 二维码
        for (int i = 0; i < codeOut.length; i++) {
            for (int j = 0; j < codeOut.length; j++) {
                if (codeOut[j][i]) {
                    gs.fillRect(j * 3 + off, i * 3 + off, 3, 3);
                }
            }
        }
        if(logo != null){
            Image image = null;
            try {
                //实例化一个Image对象。
                image = ImageIO.read(new File(logo));
            } catch (IOException e) {
                e.printStackTrace();
            }
            gs.drawImage(image, 55, 55, 30, 30, null);
        }
        gs.dispose();
        img.flush();
        return img;
    }

    /**
     * 生成png类型的二维码
     * @param path 生成二维码图片路径
     * @param img
     */
    public static void genCode(String path, BufferedImage img){
        ImageUtils.writeImage(new File(path), img);
    }

    /**
     * 解析二维码
     * @param imgPath
     * @return
     */
    public static String parseCode(String imgPath) {

        // QRCode 二维码图片的文件
        File imageFile = new File(imgPath);
        String decodedData = null;
        try {
            final BufferedImage img = ImageIO.read(imageFile);
            QRCodeDecoder decoder = new QRCodeDecoder();
            QRCodeImage qrCodeImage = new QRCodeImage() {

                @Override
                public int getWidth() {
                    return img.getWidth();
                }

                @Override
                public int getHeight() {
                    return img.getHeight();
                }

                @Override
                public int getPixel(int x, int y) {
                    return img.getRGB(x, y);
                }
            };
            decodedData = new String(decoder.decode(qrCodeImage), FileConstants.ENCODE_UTF8);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } catch (DecodingFailedException dfe) {
            System.out.println("Error: " + dfe.getMessage());
            dfe.printStackTrace();
        }

        return decodedData;
    }
}
