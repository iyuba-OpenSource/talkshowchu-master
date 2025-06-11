package com.iyuba.talkshow.lil.help_fix.util;

import android.Manifest;
import android.content.Context;
import android.os.Build;

import com.hjq.permissions.XXPermissions;
import com.iyuba.talkshow.lil.help_mvp.util.xxpermission.PermissionBackListener;
import com.iyuba.talkshow.lil.help_mvp.util.xxpermission.XXPermissionUtil;

import java.util.List;

/**
 * @desction: 权限工具
 * @date: 2023/4/9 14:04
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class PermissionUtil {

    /**************权限申请*******************/
    //申请存储权限
    public static void requestExternal(Context atyOrFragment, PermissionBackListener listener){
        String[] requestPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            requestPermissions = new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE};
        }

        List<String> deniedList = XXPermissionUtil.getInstance().checkPermission(atyOrFragment,requestPermissions);
        if (deniedList.size()>0){
            XXPermissionUtil.getInstance().applyPermission(atyOrFragment,deniedList,listener);
        }else {
            if (listener!=null){
                listener.allGranted();
            }
        }
    }

    //申请麦克风权限
    public static void requestRecordAudio(Context atyOrFragment, PermissionBackListener listener){
        String[] requestPermissions = new String[]{Manifest.permission.RECORD_AUDIO};
        if (Build.VERSION.SDK_INT<=Build.VERSION_CODES.P){
            requestPermissions = new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }



        List<String> deniedList = XXPermissionUtil.getInstance().checkPermission(atyOrFragment,requestPermissions);
        if (deniedList.size()>0){
            XXPermissionUtil.getInstance().applyPermission(atyOrFragment,deniedList,listener);
        }else {
            if (listener!=null){
                listener.allGranted();
            }
        }
    }

    //申请相机权限
    public static void requestCamera(Context atyOrFragment, PermissionBackListener listener){
        String[] requestPermissions = new String[]{Manifest.permission.CAMERA};
        if (Build.VERSION.SDK_INT<=Build.VERSION_CODES.P){
            requestPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }

        List<String> deniedList = XXPermissionUtil.getInstance().checkPermission(atyOrFragment,requestPermissions);
        if (deniedList.size()>0){
            XXPermissionUtil.getInstance().applyPermission(atyOrFragment,deniedList,listener);
        }else {
            if (listener!=null){
                listener.allGranted();
            }
        }
    }

    //手动授权
    public static void jumpToSetting(Context context){
        XXPermissions.startPermissionActivity(context);
    }

    /****************权限名称******************/
    public static String getPermissionName(String permission){
        switch (permission){
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
            case Manifest.permission.MANAGE_EXTERNAL_STORAGE:
                return "存储权限";
            case Manifest.permission.RECORD_AUDIO:
                return "录音权限";
            case Manifest.permission.CAMERA:
                return "拍照权限";
        }
        return "必要权限";
    }
}
