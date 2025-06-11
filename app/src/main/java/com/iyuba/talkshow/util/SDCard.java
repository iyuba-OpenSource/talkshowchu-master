package com.iyuba.talkshow.util;

import android.os.Environment;

import java.io.File;

public class SDCard {
	/**
	 * 判断SD卡是否存在
	 * 
	 * @return
	 */
	public static boolean hasSDCard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

	/**
	 * 获取SD卡根目录
	 *
	 * @return
	 */
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir.toString();
	}
}
