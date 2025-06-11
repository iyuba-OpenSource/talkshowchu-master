package com.iyuba.talkshow.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.content.FileProvider;

import java.io.File;

public class FailOpera {

    public static void openAPKFile(Context context, String filePath) {
        openAPKFile(context, new File(filePath));
    }

    public static void openAPKFile(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    //根据版本处理
    public static void installApk(Context context,File file){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    &&Build.VERSION.SDK_INT <= Build.VERSION_CODES.R){
                boolean hasPermission = context.getPackageManager().canRequestPackageInstalls();
                if (hasPermission){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                    intent.setData(Uri.parse("package:"+context.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else {
                    openAPKFile(context,file);
                }
            }else {
                openAPKFile(context,file);
            }
        }catch (Exception e){
            openAPKFile(context,file);
        }
    }
}
