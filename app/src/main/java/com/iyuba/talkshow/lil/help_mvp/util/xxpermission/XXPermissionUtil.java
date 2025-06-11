package com.iyuba.talkshow.lil.help_mvp.util.xxpermission;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @desction: 权限申请工具
 * @date: 2023/4/8 12:44
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class XXPermissionUtil {

    //标志
    private static final String Granted = "Granted";//通过
    private static final String Denied = "Denied";//拒绝
    //回调的权限
    private Map<String,List<String>> permissionMap;

    private static XXPermissionUtil instance;

    public static XXPermissionUtil getInstance(){
        if (instance==null){
            synchronized (XXPermissionUtil.class){
                if (instance==null){
                    instance = new XXPermissionUtil();
                }
            }
        }
        return instance;
    }

    //检查权限
    public List<String> checkPermission(Context context,String[] requestPermission){
        return XXPermissions.getDenied(context,requestPermission);
    }

    //申请权限
    public void applyPermission(Context atyOrFragment, List<String> requestList, PermissionBackListener listener){
        if (permissionMap==null){
            permissionMap = new HashMap<>();
        }
        permissionMap.clear();

        XXPermissions.with(atyOrFragment)
                .permission(requestList)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (listener!=null){
                            if (allGranted){
                                listener.allGranted();
                            }else {
                                if (permissions.size()< requestList.size()){
                                    permissionMap.put(Granted,permissions);

                                    listener.halfPart(permissionMap.get(Granted),permissionMap.get(Denied));
                                }
                            }
                        }
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        if (listener!=null){
                            if (doNotAskAgain){
                                listener.warnRequest();
                            }else {
                                if (permissions.size()==requestList.size()){
                                    listener.allDenied();
                                }else {
                                    permissionMap.put(Denied,permissions);
                                }
                            }
                        }
                    }
                });
    }

    //跳转权限界面
    public void jumpPermissionActivity(Context context, List<String> deniedPermissions){
        XXPermissions.startPermissionActivity(context,deniedPermissions);
    }
}
