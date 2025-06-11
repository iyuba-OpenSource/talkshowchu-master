package com.iyuba.talkshow.lil.help_fix.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.util.ToastUtil;
import com.tencent.vasdolly.helper.ChannelReaderUtil;

import java.util.List;

/**
 * 应用市场工具类
 */
public class AppStoreUtil {

    //根据不同的手机类型判断需要跳转的应用市场
    public static void toAppStoreDetailByType(Context context) {
        String packageName = "";
        //先根据设备型号判断包名
        String brandName = Build.BRAND.toLowerCase();
        /*switch (brandName) {
            case "huawei":
            case "honor":
            case "meizu":
            case "samsung":
            case "lenovo":
                packageName = Constant.PackageName.Package_junior;
                break;
            case "vivo":
            case "xiaomi":
            case "redmi":
            case "oppo":
            case "oneplus":
                packageName = Constant.PackageName.Package_juniorenglish;
                break;
            default:
                packageName = "";
                break;
        }*/

        //再根据当前渠道决定跳转包名
        /*if (TextUtils.isEmpty(packageName)) {
            String channelName = ChannelReaderUtil.getChannel(context);
            switch (channelName) {
                case "huawei":
                case "honor":
                    packageName = Constant.PackageName.Package_junior;
                    break;
                case "oppo":
                    packageName = Constant.PackageName.Package_juniorenglish;
                    break;
                default:
                    packageName = "";
                    break;
            }
        }*/

        //都没有的话使用默认的操作
        if (TextUtils.isEmpty(packageName)) {
            packageName = context.getPackageName();
        }

        //根据类型跳转
        if (brandName.equals("huawei")) {
            toHuaweiAppStoreDetail(context, packageName);
        } else if (brandName.equals("honor")) {
            if (isPackageExist(context, "com.huawei.appmarket")) {
                toHuaweiAppStoreDetail(context,packageName);
            } else if (isPackageExist(context, "com.hihonor.appmarket")) {
                toHonorAppStoreDetail(context, packageName);
            } else {
                toAppStoreDetail(context, packageName);
            }
        }
        /*else if (brandName.equals("vivo") || brandName.endsWith("realme")) {
            toVivoAppStoreDetail(context, packageName);
        } else if (brandName.equals("oppo") || brandName.equals("oneplus")) {
            toOppoAppStoreDetail(context, packageName);
        }*/
        else {
            toAppStoreDetail(context, packageName);
        }
    }

    //跳转到华为应用市场的应用详情界面
    private static void toHuaweiAppStoreDetail(Context context, String packageName) {
        try {
            Intent intent = new Intent();
            intent.setAction("com.huawei.appmarket.intent.action.AppDetail");
            intent.setPackage("com.huawei.appmarket");
            intent.putExtra("APP_PACKAGENAME", packageName);
            context.startActivity(intent);
        } catch (Exception e) {
            toAppStoreDetail(context, packageName);
        }
    }

    //跳转到荣耀应用市场
    private static void toHonorAppStoreDetail(Context context, String packageName) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
            intent.setPackage("com.hihonor.appmarket");
            context.startActivity(intent);
        } catch (Exception e) {
            toAppStoreDetail(context, packageName);
        }
    }

    //跳转到vivo应用市场
    private static void toVivoAppStoreDetail(Context context, String packageName) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName + "&th_name=need_comment"));
            intent.setPackage("com.bbk.appstore");
            context.startActivity(intent);
        } catch (Exception e) {
            toAppStoreDetail(context, packageName);
        }
    }

    //跳转到oppo应用市场
    private static void toOppoAppStoreDetail(Context context, String packageName) {
        try {
            //跳转的url
            String appUrl = "oaps://mk/developer/comment?pkg=" + packageName;
            //判断后需要跳转的包名
            String appStorePackage = "";
            if (getOppoAppStoreVersion(context, "com.heytap.market") > 84000) {
                appStorePackage = "com.heytap.market";
            } else {
                if (getOppoAppStoreVersion(context, "com.oppo.market") > 84000) {
                    appStorePackage = "com.oppo.market";
                }
            }
            if (TextUtils.isEmpty(appStorePackage)) {
                toAppStoreDetail(context, packageName);
            } else {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setPackage(appStorePackage);
                intent.setData(Uri.parse(appUrl));
                // 建议采用startActivityForResult 方法启动商店页面，requestCode由调用方自定义且必须大于0，软件商店不关注
                ((Activity) context).startActivityForResult(intent, 100);
            }
        } catch (Exception e) {
            toAppStoreDetail(context, packageName);
        }
    }

    //获取oppo应用市场的版本
    private static long getOppoAppStoreVersion(Context context, String packageName) {
        long versionCode = -1;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
            if (info != null) {
                versionCode = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? info.getLongVersionCode() : info.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        return versionCode;
    }

    //跳转到应用市场
    public static void toAppStoreDetail(Context context, String packageName) {
        try {
            Uri uri = Uri.parse("market://details?id=" + packageName);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            context.startActivity(intent);
        }catch (Exception e){
            ToastUtil.showToast(context,"您未安装应用市场，无法操作");
        }
    }

    //获取包名是否存在
    private static boolean isPackageExist(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
