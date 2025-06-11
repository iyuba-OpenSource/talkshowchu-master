package com.iyuba.talkshow.lil.help_mvp.util;

import android.util.Base64;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;

/**
 * 编码工具类
 */
public class EncodeUtil {

    //md5编码
    public static String md5(String dataStr){
        try {
            //获取摘要器
            MessageDigest digest = MessageDigest.getInstance("MD5");
            //使用摘要器对数据进行hash处理
            byte[] data = digest.digest(dataStr.getBytes());

            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < data.length; i++) {
                //将每个数据转换为正整数
                int digestInt = data[i] & 0xff;
                //将10进制转换为16进制
                String hexString = Integer.toHexString(digestInt);
                //转换之后需要在前面进行补0
                if (hexString.length()<2){
                    buffer.append(0);
                }
                buffer.append(hexString);
            }
            //返回数据
            return buffer.toString();
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    //base64编码
    public static String encode64(byte[] bytes){
        String encodeStr = "";

        try {
            encodeStr = Base64.encodeToString(bytes,Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return encodeStr;
        }
    }

    //base64解码
    public static String decode64(){
        return null;
    }

    //aes加密

    //aes解密

    //URLEncoder编码
    public static String encode(String dataStr){
        String encodeStr = "";

        try {
            encodeStr = URLEncoder.encode(dataStr,"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return encodeStr;
        }
    }

    //URLEncoder解码
    public static String decode(String dataStr){
        String decodeStr = "";

        try {
            decodeStr = URLDecoder.decode(dataStr,"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return decodeStr;
        }
    }
}
