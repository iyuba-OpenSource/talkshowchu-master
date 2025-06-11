package com.iyuba.wordtest.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Pair;

import androidx.appcompat.app.AlertDialog;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 权限弹窗工具
 * @date: 2023/11/30 14:11
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class PermissionDialogUtil {
    private static PermissionDialogUtil instance;

    public static PermissionDialogUtil getInstance() {
        if (instance == null) {
            synchronized (PermissionDialogUtil.class) {
                if (instance == null) {
                    instance = new PermissionDialogUtil();
                }
            }
        }
        return instance;
    }

    //显示数据
    public void showMsgDialog(Context context, List<Pair<String, Pair<String, String>>> pairList,OnPermissionResultListener resultListener) {
        //权限不存在处理
        if (pairList == null || pairList.size() == 0) {
            if (resultListener != null) {
                resultListener.onGranted(true);
            }
            return;
        }

        //获取数据并判断权限存在
        //权限申请的数据
        List<String> needPermissionList = new ArrayList<>();
        //简要信息显示的数据
        List<String> tipsPermissionList = new ArrayList<>();
        //详细信息显示的数据
        StringBuffer showPermissionBuffer = new StringBuffer();
        for (int i = 0; i < pairList.size(); i++) {
            Pair<String, Pair<String, String>> pair = pairList.get(i);
            //这里需要判断下是否权限已经被处理
            if (!XXPermissions.isGranted(context, pair.first)) {
                //权限申请
                needPermissionList.add(pair.first);
                //信息显示
                showPermissionBuffer.append(pair.second.first).append("：").append(pair.second.second).append("\n");
                //简要信息
                tipsPermissionList.add(pair.second.first);
            }
        }

        //无需要的权限处理
        if (needPermissionList.size() == 0) {
            if (resultListener != null) {
                resultListener.onGranted(true);
            }
            return;
        }

        //权限简要信息
        StringBuffer tipsBuffer = new StringBuffer();
        for (int i = 0; i < tipsPermissionList.size(); i++) {
            tipsBuffer.append(tipsPermissionList.get(i));
            if (i < tipsPermissionList.size() - 1) {
                tipsBuffer.append("、");
            }
        }
        //完整信息文本
        String showMsgText = "当前功能需要授权 " + tipsBuffer.toString() + "：\n\n" + showPermissionBuffer.toString();
        //显示权限弹窗
        new AlertDialog.Builder(context)
                .setTitle("权限说明")
                .setMessage(showMsgText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermission(context,needPermissionList,resultListener);
                    }
                }).setNegativeButton("取消",null)
                .setCancelable(false)
                .create()
                .show();
    }

    //权限申请
    private void requestPermission(Context context, List<String> needPermissionList,OnPermissionResultListener resultListener) {
        XXPermissions.with(context)
                .permission(needPermissionList)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            if (resultListener != null) {
                                resultListener.onGranted(true);
                            }
                        } else {
                            ToastUtil.showToast(context, "请授权后使用当前功能");
                            if (resultListener != null) {
                                resultListener.onGranted(false);
                            }
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            ToastUtil.showToast(context, "请前往应用授权界面手动授权");
                            if (resultListener != null) {
                                resultListener.onGranted(false);
                            }
                        }
                    }
                });
    }

    //权限结果回调
    private OnPermissionResultListener onPermissionResultListener;

    public interface OnPermissionResultListener {
        void onGranted(boolean isSuccess);
    }
}
