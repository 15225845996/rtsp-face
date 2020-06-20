package com.zs.rtspface.utils;

import org.springframework.util.Base64Utils;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @Auther: zs
 * @Date: 2020/6/17 17:31
 * @Description:
 */
public class ImageUtils {

    /**
     * base64字符串转换成图片 (对字节数组字符串进行Base64解码并生成图片)
     * @param imgStr		base64字符串
     * @param imgFilePath	指定图片存放路径  （注意：带文件名）
     * @return
     */
    public static boolean Base64ToImage(String imgStr,String imgFilePath) {

        if (imgStr == null) { // 图像数据为空
            return false;
        }

        try {
            // Base64解码
            byte[] b = Base64Utils.decodeFromString(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }

            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();

            return true;
        } catch (Exception e) {
            return false;
        }

    }
}
