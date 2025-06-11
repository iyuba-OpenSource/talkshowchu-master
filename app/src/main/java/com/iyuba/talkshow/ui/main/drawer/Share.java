package com.iyuba.talkshow.ui.main.drawer;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.data.model.DownLoadJFResult;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.remote.IntegralService;
import com.iyuba.talkshow.util.GetLocation;
import com.iyuba.talkshow.util.VoaMediaUtil;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Share {

    public static void shareMessage(Context context, int series, int voaid, String imageUrl, String text, String url,
                                     String title, PlatformActionListener listener) {
        OnekeyShare oks = new OnekeyShare();
        if (!ConfigData.openQQShare) {
            oks.addHiddenPlatform(QQ.NAME);
            oks.addHiddenPlatform(QZone.NAME);
        }
        if (!ConfigData.openWeChatShare){
            oks.addHiddenPlatform(Wechat.NAME);
            oks.addHiddenPlatform(WechatFavorite.NAME);
            oks.addHiddenPlatform(WechatMoments.NAME);
        }
        if (!ConfigData.openWeiBoShare){
            oks.addHiddenPlatform(SinaWeibo.NAME);
        }
        oks.setTitle(title);
        oks.setTitleUrl(url);
        oks.setText(text);
        oks.setImageUrl(imageUrl);
        oks.setUrl(url);
        oks.setComment(MessageFormat.format(
                context.getString(R.string.app_share_content), App.APP_NAME_CH));
        oks.setSite(App.APP_NAME_CH);
        oks.setSiteUrl(url);
        oks.setSilent(true);
        Location location = GetLocation.getInstance().getLocation();
        if (location != null) {
            oks.setLatitude((float) location.getLatitude());
            oks.setLongitude((float) location.getLongitude());
        }else {
            oks.setLatitude((float) 0);
            oks.setLongitude((float) 0);
        }
        oks.setCallback(listener);
        oks.show(context);
    }

    public static void sharePdfMessage(Context context, String imageUrl, String text, String url,
                                    String title, PlatformActionListener listener) {
        OnekeyShare oks = new OnekeyShare();
        if (!ConfigData.openQQShare) {
            oks.addHiddenPlatform(QQ.NAME);
            oks.addHiddenPlatform(QZone.NAME);
        }
        if (!ConfigData.openWeChatShare){
            oks.addHiddenPlatform(Wechat.NAME);
            oks.addHiddenPlatform(WechatFavorite.NAME);
            oks.addHiddenPlatform(WechatMoments.NAME);
        }
        if (!ConfigData.openWeiBoShare){
            oks.addHiddenPlatform(SinaWeibo.NAME);
        }
        oks.setTitle(title);
        oks.setTitleUrl(url);
        oks.setText(text);
        oks.setImageUrl(imageUrl);
        oks.setUrl(url);
        oks.setComment(MessageFormat.format(
                context.getString(R.string.app_share_content), App.APP_NAME_CH));
        oks.setSite(App.APP_NAME_CH);
        oks.setSiteUrl(url);
        oks.setSilent(true);
        Location location = GetLocation.getInstance().getLocation();
        if (location != null) {
            oks.setLatitude((float) location.getLatitude());
            oks.setLongitude((float) location.getLongitude());
        }else {
            oks.setLatitude((float) 0);
            oks.setLongitude((float) 0);
        }
        oks.setCallback(listener);
        oks.show(context);
    }

    public static boolean isWXSmallAvailable(Context context) {
        IWXAPI wxapi = WXAPIFactory.createWXAPI(context, ConfigData.mob_key);
        if ((wxapi != null) && wxapi.isWXAppInstalled()) {
            return wxapi.getWXAppSupportAPI() > Build.MINIPROGRAM_SUPPORTED_SDK_INT;
        }
        return false;
    }

    //分享本应用
    public static void prepareMessage(Context context) {
        String text = MessageFormat.format(
                context.getString(R.string.share_content),
                App.APP_NAME_CH, App.APP_ID);
        String imgUrl = App.Url.APP_ICON_URL;
//        String url = "https://a.app.qq.com/o/simple.jsp?pkgname="+ BuildConfig.APPLICATION_ID;
        String url = App.Url.SHARE_APP_URL;
        String title = App.APP_NAME_CH;
        sharePdfMessage(context, imgUrl, text, url, title, new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                if (throwable != null) {
                    Log.e("share", "shareMessage onError " + throwable.getMessage());
                }
            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });

    }

    public static void prepareVideoMessage(Context context, int series, Voa voa, IntegralService service, int uid) {
        String url = VoaMediaUtil.getShareUrl(voa.category(),voa.voaId());
        shareMessage(context, series, voa.voaId(), voa.pic(), getText(context, voa), url, voa.titleCn(), getListener(context, service, uid, voa.voaId()));
    }

    private static String getText(Context context, Voa voa) {
        return MessageFormat.format(context.getString(R.string.video_share_content),
                voa.titleCn(), voa.title(), voa.descCn());
    }

    public static void prepareWechatMessage(Context context, int series, Voa voa, IntegralService service, int uid) {
        String url = VoaMediaUtil.getShareUrl(voa.category(),voa.voaId());
        //增加未登录的积分限制
        PlatformActionListener listener = null;
        if (uid != 0){
            listener = getListener(context, service, uid, voa.voaId());
        }
        shareWechatMessage(context, uid, series, voa.voaId(), voa.pic(), getText(context, voa), url, voa.titleCn(), listener);
    }

    public static void shareWechatMessage(Context context, int uid, int series, int voaid, String imageUrl, String text, String url,
                                    String title, PlatformActionListener listener) {
        OnekeyShare oks = new OnekeyShare();
        if (!ConfigData.openQQShare) {
            oks.addHiddenPlatform(QQ.NAME);
            oks.addHiddenPlatform(QZone.NAME);
        }
        if (!ConfigData.openWeChatShare){
            oks.addHiddenPlatform(Wechat.NAME);
            oks.addHiddenPlatform(WechatFavorite.NAME);
            oks.addHiddenPlatform(WechatMoments.NAME);
        }
        if (!ConfigData.openWeiBoShare){
            oks.addHiddenPlatform(SinaWeibo.NAME);
        }
        oks.setTitle(title);
        oks.setTitleUrl(url);
        oks.setText(text);
        oks.setImageUrl(imageUrl);
        oks.setUrl(url);
        oks.setComment(MessageFormat.format(
                context.getString(R.string.app_share_content), App.APP_NAME_CH));
        oks.setSite(App.APP_NAME_CH);
        oks.setSiteUrl(url);
        oks.setSilent(true);
        if (ConfigData.openWxSmallShare) {
            oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                @Override
                public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                    if ("Wechat".equals(platform.getName())) {
                        Log.e("ShareSDK", "Wechat title " + title);
                        if (Share.isWXSmallAvailable(context)) {
                            paramsToShare.setWxMiniProgramType(0);
                            if (voaid > 0) {
                                String path = String.format("pages/detail/detail?voaid=%d&series=%d", voaid, series);
                                Log.e("ShareSDK", "shareWechatMessage MiniProgram path " + path);
                                paramsToShare.setWxPath(path);
                            } else {
                                paramsToShare.setWxPath("pages/index/index");
                            }
                            paramsToShare.setWxUserName(ConfigData.wx_small_name);
                            paramsToShare.setShareType(Platform.SHARE_WXMINIPROGRAM);
                            paramsToShare.setWxWithShareTicket(true);
                            paramsToShare.setImageUrl(imageUrl);
                            Log.e("ShareSDK", "Wechat MiniProgram " + paramsToShare.toMap().toString());
                            return;
                        }
                    }
                    Log.e("ShareSDK", "others " + paramsToShare.toMap().toString());
                }
            });
        }
        Location location = GetLocation.getInstance().getLocation();
        if (location != null) {
            oks.setLatitude((float) location.getLatitude());
            oks.setLongitude((float) location.getLongitude());
        }else {
            oks.setLatitude((float) 0);
            oks.setLongitude((float) 0);
        }
        oks.setCallback(listener);
        oks.show(context);
    }

    public static void prepareDubbingMessage(Context context, Voa voa, int backId, String user, IntegralService service, int uid) {
//        String url1= VoaMediaUtil.getAudioVipUrl(voa.sound());
//        String descUrl = Constant.Url.MY_DUBBING_PREFIX_VOA+voa.voaId()+
//                "&shuoshuo="+url1+"&apptype=talkshow";
        String descUrl = Constant.Url.getMyDubbingUrl(backId,App.SHARE_NAME_EN);

        sharePdfMessage(context, voa.pic(), getText(context, voa), descUrl, "播音员：" + user + " " + voa.titleCn(), getListener(context, service, uid, backId));
    }


    public static PlatformActionListener getListener(final Context mContext, final IntegralService service, final int uid, final int id) {
        return new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.e("share", "onComplete");
                String srid = "";
                if (platform.getName().equals("QQ")
                        || platform.getName().equals("Wechat")
                        || platform.getName().equals("WechatFavorite")) {
                    srid = "7";
                } else if (platform.getName().equals("QZone")
                        || platform.getName().equals("WechatMoments")
                        || platform.getName().equals("SinaWeibo")
                        || platform.getName().equals("TencentWeibo")) {
                    srid = "19";
                }

                service.integral(srid, 1, getTime(), uid, App.APP_ID, id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<DownLoadJFResult>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (e != null) {
                                    Log.e("share", "onError " + e.getMessage());
                                }
                                Log.e("onError", "分享积分失败");
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                    Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT).show();
                                }
                                });
                            }

                            @Override
                            public void onNext(DownLoadJFResult result) {
                                Log.e("onNext", "分享积分成功---");
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                    if (result != null && "200".equals(result.getResult())) {
                                        Toast.makeText(mContext, "分享成功" + result.getAddcredit() + "分，当前积分为："
                                                        + result.getTotalcredit() + "分",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            }
                        });
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                if (throwable != null) {
                    Log.e("share", "onError " + throwable.getMessage());
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "分享失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.e("share", "onCancel");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "分享已取消", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
    }

    public static String getTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        //设置日期格式
        return "1234567890" + df.format(new Date());// new Date()为获取当前系统时间
    }

    //分享内容和链接
    public static void shareContentAndUrl(Context context,String title,String shareText,String shareUrl){
        String imgUrl = App.Url.APP_ICON_URL;
        sharePdfMessage(context, imgUrl, shareText, shareUrl, title, new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
    }
}
