package com.iyuba.wordtest.utils;

import androidx.sqlite.db.SupportSQLiteDatabase;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.text.DecimalFormat;

public class FileUtils {

	/** 获取SD卡路径 **/
//	public static String getSDPath() {
//		if (SDCard.hasSDCard()) {
//			return SDCard.getSDPath();
//		}
//		return "/";
//	}

	/** 格式化文件大小(K,M,G等) **/
	public static String formetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = fileS + " B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + " K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + " M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + " G";
		}
		return fileSizeString;
	}

	/** 拼接路径名和文件名 **/
	public static String combinPath(String path, String fileName) {
		return path + (path.endsWith(File.separator) ? "" : File.separator) + fileName;
	}

	/** 复制文件 **/
	public static boolean copyFile(File src, File tar) throws Exception {
		if (src.isFile()) {
			InputStream is = new FileInputStream(src);
			OutputStream op = new FileOutputStream(tar);
			BufferedInputStream bis = new BufferedInputStream(is);
			BufferedOutputStream bos = new BufferedOutputStream(op);
			byte[] bt = new byte[1024 * 8];
			int len = bis.read(bt);
			while (len != -1) {
				bos.write(bt, 0, len);
				len = bis.read(bt);
			}
			bis.close();
			bos.close();
		}
		if (src.isDirectory()) {
			File[] f = src.listFiles();
			tar.mkdir();
			for (int i = 0; i < f.length; i++) {
				copyFile(f[i].getAbsoluteFile(), new File(tar.getAbsoluteFile() + File.separator
						+ f[i].getName()));
			}
		}
		return true;
	}

	/** 移动文件（原位置删除） **/
	public static boolean moveFile(File src, File tar) throws Exception {
		if (copyFile(src, tar)) {
			deleteFile(src);
			return true;
		}
		return false;
	}

	/** 删除文件或文件夹 **/
	public static void deleteFile(File f) {
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			if (files != null && files.length > 0) {
				for (int i = 0; i < files.length; ++i) {
					deleteFile(files[i]);
				}
			}
		}
		f.delete();
	}

	/** 删除文件夹下的所有内容 **/
	public static void clearFileDir(File f) {
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			if (files != null && files.length > 0) {
				for (int i = 0; i < files.length; ++i) {
					deleteFile(files[i]);
				}
			}
		}
	}

	/** 获取文件MIME类型 **/
	public static String getMIMEType(String name) {
		String type = "";
		String end = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
		if (end.equals("apk")) {
			return "application/vnd.android.package-archive";
		} else if (end.equals("mp4") || end.equals("avi") || end.equals("3gp")
				|| end.equals("rmvb")) {
			type = "video";
		} else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf")
				|| end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else if (end.equals("txt") || end.equals("log")) {
			type = "text";
		} else {
			type = "*";
		}
		type += "/*";
		return type;
	}

	public static Reader getReaderFromRaw(Context context, int rawId) {
		return new BufferedReader(
						new InputStreamReader(
								context.getResources().openRawResource(rawId)));

	}

	public static void executeSQL(SupportSQLiteDatabase db, InputStream stream){
		BufferedReader in = null ;
		try {
			in = new BufferedReader(new InputStreamReader(stream));
			String line;
			String buffer = "";
			//开启事务
			db.beginTransaction();
			while ((line = in.readLine()) != null) {
				buffer += line;
				if (line.trim().endsWith(";")) {
					db.execSQL(buffer.replace(";", ""));
					buffer = "";
				}
			}
			//设置事务标志为成功，当结束事务时就会提交事务
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("db-error", e.toString());
		} finally {
			//事务结束
			db.endTransaction();
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
				Log.e("db-error", e.toString());
			}
		}
	}

	public static boolean executeAssetsSQL(Context context , SupportSQLiteDatabase db, String dbfilepath) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(context.getAssets().open(dbfilepath)));
			Log.e("db-error", "路径: " + dbfilepath);
			String line;
			String buffer = "";
			//开启事务
			db.beginTransaction();
			while ((line = in.readLine()) != null) {
				//这里增加判断，如果是以 ## 开头的，说明是注释
				if (line.startsWith("##")){
					continue;
				}

				buffer += line;
				if (line.trim().endsWith(";")) {
					db.execSQL(buffer.replace(";", ""));
					buffer = "";
				}
			}
			//设置事务标志为成功，当结束事务时就会提交事务
			db.setTransactionSuccessful();
			Log.e("db-error", "executeAssetsSQL end :" + dbfilepath);
		} catch (Exception e) {
			Log.e("db-error", e.toString());
			return false;
		} finally {
			//事务结束
			db.endTransaction();
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
				Log.e("db-error", e.toString());
			}
		}
		return true;
	}

	public static String getFileName(File file) {
		String name = file.getName();
		return name.split("\\.")[0];
	}

	public static Uri pathToUri(String path){
		Uri uri = Uri.fromFile(new File(path));
        return uri;
    }

	public static File getNewFile(Context context, String name ) {
		File file ;
		File parentFile;
		file = new File(context.getExternalFilesDir(null) + "/iyuba/audio/" + name.trim() + ".amr");
		parentFile = new File(context.getExternalFilesDir(null) + "/iyuba/audio");
		if (!parentFile.exists())
			parentFile.mkdirs();
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file ;
	}
}
