package com.iyuba.talkshow.lil.help_mvp.util.xxpermission;

import java.util.List;

/**
 * @desction: 权限回调接口
 * @date: 2023/4/8 12:46
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public interface PermissionBackListener {

    //全部申请成功
    void allGranted();

    //全部申请失败
    void allDenied();

    //部分申请成功或失败
    void halfPart(List<String> grantedList, List<String> deniedList);

    //禁止申请权限
    void warnRequest();
}
