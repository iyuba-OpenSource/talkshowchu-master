package com.iyuba.talkshow.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {

    /**
     * 判断qq是否可用
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取SD卡路径
     **/
    public static String getSDPath() {
        if (SDCard.hasSDCard()) {
            return SDCard.getSDPath();
        }
        return "/";
    }

    /**
     * 格式化文件大小(K,M,G等)
     **/
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

    /**
     * 拼接路径名和文件名
     **/
    public static String combinPath(String path, String fileName) {
        return path + (path.endsWith(File.separator) ? "" : File.separator) + fileName;
    }

    /**
     * 复制文件
     **/
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

    /**
     * 移动文件（原位置删除）
     **/
    public static boolean moveFile(File src, File tar) throws Exception {
        if (copyFile(src, tar)) {
            deleteFile(src);
            return true;
        }
        return false;
    }

    /**
     * 删除文件或文件夹
     **/
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


    /**
     * 删除文件夹下的所有内容
     **/
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

    /**
     * 获取文件MIME类型
     **/
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

    public static void executeAssetsSQL(Context context, SQLiteDatabase db, String dbfilepath) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(context.getAssets().open(dbfilepath)));
            Log.e("db-error", "路径:" + dbfilepath);
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

    public static String getFileName(File file) {
        String name = file.getName();
        return name.split("\\.")[0];
    }

    public static Uri pathToUri(String path) {
        Uri uri = Uri.fromFile(new File(path));
        return uri;
    }

    public static void deleteFile(List<Integer> mDatas) {


    }

    /**
     * 解压到指定目录
     */
    public static void unZipFiles(String zipPath, String descDir) throws IOException {
		unZipFile(zipPath, descDir,true);
    }
	/**
	 *
	 * @param archive 解压文件得路径
	 * @param decompressDir 解压文件目标路径
	 * @param isDeleteZip  解压完毕是否删除解压文件
	 * @throws IOException
	 */
	public static void unZipFile(String archive, String decompressDir, boolean isDeleteZip) throws IOException {
		BufferedInputStream bi;
		ZipFile zf = new ZipFile(archive);
		Enumeration e = zf.entries();
		while (e.hasMoreElements()) {
			ZipEntry ze2 = (ZipEntry) e.nextElement();
			String entryName = ze2.getName();
			String path = decompressDir + "/" + entryName;
			if (ze2.isDirectory()) {
				File decompressDirFile = new File(path);
				if (!decompressDirFile.exists()) {
					decompressDirFile.mkdirs();
				}
			} else {
				String fileDir = path.substring(0, path.lastIndexOf("/"));
				if (decompressDir.endsWith(".zip")) {
					decompressDir = decompressDir.substring(0, decompressDir.lastIndexOf(".zip"));
				}
				File fileDirFile = new File(decompressDir);
				if (!fileDirFile.exists()) {
					fileDirFile.mkdirs();
				}
				String substring = entryName.substring(entryName.lastIndexOf("/") + 1);
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(decompressDir + "/" + substring));
				bi = new BufferedInputStream(zf.getInputStream(ze2));
				byte[] readContent = new byte[1024];
				int readCount = bi.read(readContent);
				while (readCount != -1) {
					bos.write(readContent, 0, readCount);
					readCount = bi.read(readContent);
				}
				bos.close();
			}
		}
		zf.close();
		if (isDeleteZip) {
			File zipFile = new File(archive);
			if (zipFile.exists() && zipFile.getName().endsWith(".zip")) {
				zipFile.delete();
			}
		}
	}


    /**
     * 解压文件到指定目录
     */
//    @SuppressWarnings("rawtypes")
//    public static void unZipFiles(File zipFile, String descDir) throws IOException {
//        File pathFile = new File(descDir);
//        if (!pathFile.exists()) {
//            pathFile.mkdirs();
//        }
//        //解决zip文件中有中文目录或者中文文件
////        ZipFile zip = new ZipFile(zipFile, Charset.forName("GBK"));
//        for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
//            ZipEntry entry = (ZipEntry) entries.nextElement();
//            String zipEntryName = entry.getName();
//            InputStream in = zip.getInputStream(entry);
//            String outPath = (descDir + zipEntryName).replaceAll("\\*", "/");
//            ;
//            //判断路径是否存在,不存在则创建文件路径
//            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
//            if (new File(outPath).isDirectory()) {
//                continue;
//            }
//            //输出文件路径信息
//            System.out.println(outPath);
//            OutputStream out = new FileOutputStream(outPath);
//            byte[] buf1 = new byte[1024];
//            int len;
//            while ((len = in.read(buf1)) > 0) {
//                out.write(buf1, 0, len);
//            }
//            in.close();
//            out.close();
//        }
//        System.out.println("******************解压完毕********************");
//    }

//    public static void main(String[] args) throws IOException {
//        /**
//         * 解压文件
//         */
//        File zipFile = new File("d:/资料.zip");
//        String path = "d:/zipfile/";
//        unZipFiles(zipFile, path);
//    }
}
