package com.iyuba.talkshow.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.iyuba.talkshow.TalkShowApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SelectPicUtils {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static boolean downloadQFile(Context context, String fileUrl, String fileName) {
        if ((context == null) && TextUtils.isEmpty(fileUrl) || TextUtils.isEmpty(fileName)) {
            Log.e("SelectPicUtils", "downloadQFile is null?");
            return false;
        }
        TalkShowApplication.getSubHandler().post(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) new URL(fileUrl).openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(8000);
                    conn.setReadTimeout(8000);
                    InputStream inputStream = conn.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                    Uri uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                    if (uri != null) {
                        OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                        if (outputStream != null) {
                            BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                            byte[] buffer = new byte[4096];
                            int bytes = bis.read(buffer);
                            while (bytes >= 0) {
                                bos.write(buffer, 0 , bytes);
                                bos.flush();
                                bytes = bis.read(buffer);
                            }
                            bos.close();
                        }
                    }
                    bis.close();
                    Log.e("SelectPicUtils", "downloadQFile ok fileName " + fileName);
                } catch (final Exception var2) {
                    if (var2 != null) {
                        Log.e("SelectPicUtils", "downloadQFile Exception " + var2.getMessage());
                    }
                } finally {
                    if (null != conn) conn.disconnect();
                }
            }
        });
        return true;
    }
    /***
     * 选择一张图片 图片类型，这里是image/*，当然也可以设置限制 如：image/jpeg等
     */
    @SuppressLint("InlinedApi")
    public static void selectPicture(Activity activity, int requestCode) {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            activity.startActivityForResult(intent, requestCode);
//        } else {
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//            activity.startActivityForResult(intent, requestCode);
//        }
        Intent pictures = new Intent();
        pictures.setAction(Intent.ACTION_PICK);
        pictures.setType("image/*");
        activity.startActivityForResult(pictures, requestCode);
    }

    /***
     * 裁剪图片
     */
    public static void cropPicture(Activity activity, Uri uri, int requestCode) {
        Intent innerIntent = new Intent("com.android.camera.action.CROP");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            String url = getPath(activity, uri);
            innerIntent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        } else {
            innerIntent.setDataAndType(uri, "image/*");
        }
        innerIntent.putExtra("crop", "true");// 才能出剪辑的小方框，不然没有剪辑功能，只能选取图片
        innerIntent.putExtra("aspectX", 1); // 放大缩小比例的X
        innerIntent.putExtra("aspectY", 1);// 放大缩小比例的X 这里的比例为： 1:1
        innerIntent.putExtra("outputX", 200); // 这个是限制输出图片大小
        innerIntent.putExtra("outputY", 200);
        innerIntent.putExtra("return-data", true);
        innerIntent.putExtra("scale", true);
        activity.startActivityForResult(innerIntent, requestCode);
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String selectImage(Context context, Intent data) {
        Uri selectedImage = data.getData();
        if (selectedImage != null) {
            String uriStr = selectedImage.toString();
            String path = uriStr.substring(10);
            if (path.startsWith("com.sec.android.gallery3d")) {
                return null;
            }
        }
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    public static Uri getPathNew(final Context context, Intent data) {
        Uri uri = data.getData();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            //这里如果是false，就是旧版本的uri
            if (DocumentsContract.isDocumentUri(context, uri)) {
                String path = SelectPicUtils.getPath(context, uri);
                uri = FileUtils.pathToUri(path);
            }
        } else {
            /**
             * 这里selectImage需要传的data是onActivityResult的返回,
             * 当然你也可以在这里不做处理，这里会采用系统默认的uri ,并不会
             * 转义,我实际项目中没有做这里的else判断，使用默认的uri,即返回的
             * data.getData()
             */
            String path = selectImage(context, data);
            uri = FileUtils.pathToUri(path);
        }
        return uri;
    }

    public static void beginCrop(Activity context, Uri uri, File file, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        context.startActivityForResult(intent, requestCode);
    }
}
