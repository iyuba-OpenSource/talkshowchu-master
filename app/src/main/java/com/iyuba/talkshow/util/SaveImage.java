package com.iyuba.talkshow.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class SaveImage {
	public static void saveImage(String path, Bitmap bitmap) {
		File picture = new File(path);
		try {
			FileOutputStream out = new FileOutputStream(picture);
			bitmap.compress(CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveImage(String path, Bitmap bitmap, String name) {
		File picturePackage = new File(path);
		if (!picturePackage.exists()) {
			picturePackage.mkdirs();
		}
		File picture = new File(path + "/" + name);
		try {
			FileOutputStream out = new FileOutputStream(picture);
			bitmap.compress(CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(
				drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(),
				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
						: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		//canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	public static Bitmap resizeImage(Bitmap bitmap, double screenRatio) {
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		double ratio = (height * 1.0) / width;
		int newWidth, newHeight, resizeWidth, resizeHeight;
		if (screenRatio > ratio) {
			newWidth = (int) (width / screenRatio * ratio);
			newHeight = height;
			resizeWidth = (width - newWidth) / 2;
			resizeHeight = 0;
		} else {
			newWidth = width;
			newHeight = (int) (screenRatio * height / ratio);
			resizeWidth = 0;
			resizeHeight = (height - newHeight) / 2;
		}
		Log.e("sad", resizeWidth + " " + resizeHeight + " " + newWidth + " " + newHeight + " " + ratio);
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, resizeWidth, resizeHeight, newWidth,
				newHeight);
		if (BitmapOrg != null && !BitmapOrg.isRecycled()) {
			BitmapOrg.recycle();
			BitmapOrg = null;
		}
		System.gc();
		return resizedBitmap;

	}

}
