package com.iyuba.talkshow.lil.help_mvp.util;

import java.io.InputStream;
import java.util.Scanner;

/**
 * @desction: 文件辅助类
 * @date: 2023/3/22 09:47
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class FileHelpUtil {
    private static final String TAG = "FileHelpUtil";

    //将字节流转换为文本
    public static String convertSteamToString(InputStream is){
        String text = null;

        try {
            Scanner scanner = new Scanner(is,"UTF-8");
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()){
                text = scanner.next();
            }

            is.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return text;
        }
    }
}
