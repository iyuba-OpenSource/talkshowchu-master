package com.iyuba.talkshow.newce.search.util;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.ImageView;

import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util;
import com.permissionx.guolindev.PermissionX;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class SearchFileHelper {

    //获取文件的俄下载路径
    public static String getAudioFilePath(String audioName, String savePathName) {
        if (TextUtils.isEmpty(audioName)) {
            return null;
        }

        String path = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //外部存储
            path = TalkShowApplication.getContext().getExternalFilesDir(null).getAbsolutePath() + savePathName;
        } else {
            //共享存储
            if (PermissionX.isGranted(TalkShowApplication.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + App.APP_NAME_EN + savePathName;
            }
        }

        if (path == null) {
            return null;
        }

        return path + audioName;
    }

    //保存文件
    public static boolean saveFile(InputStream is, String filePath) {
        if (is == null || TextUtils.isEmpty(filePath)) {
            return false;
        }

        OutputStream os = null;

        try {
            //保存文件
            os = new FileOutputStream(filePath);
            int len = 0;
            byte[] bytes = new byte[1024];

            while ((len = is.read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }

            os.flush();

            is.close();
            os.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //图片加载
    public static void loadImg(Context context, String imgUrl, ImageView imageView) {
        Glide3Util.loadImg(context,imgUrl,0,imageView);
    }
}
