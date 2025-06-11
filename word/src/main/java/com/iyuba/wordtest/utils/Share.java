package com.iyuba.wordtest.utils;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.iyuba.wordtest.R;
import com.iyuba.wordtest.bean.DownLoadJFResult;
import com.iyuba.wordtest.data.AppData;
import com.iyuba.wordtest.network.HttpManager;
import com.iyuba.wordtest.wxapi.WordAppData;

import java.net.URLEncoder;
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
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Share {

    public static void sharePdfMessage(Context context, String imageUrl, String text, String url,
                                    String title, PlatformActionListener listener) {
        OnekeyShare oks = new OnekeyShare();
        oks.addHiddenPlatform(SinaWeibo.NAME);
        oks.setTitle(title);
        oks.setTitleUrl(url);
        oks.setText(text);
        oks.setImageUrl(imageUrl);
        oks.setUrl(url);
        oks.setComment(MessageFormat.format(
                context.getString(R.string.app_share_content), WordAppData.getInstance(context).getAppNameCn()));
        oks.setSite(WordAppData.getInstance(context).getAppNameCn());
        oks.setSiteUrl(url);
        oks.setSilent(true);
        Location location = null;
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

    //分享本应用
    public static void prepareMessage(Context context,String title,String showText) {
        String text = MessageFormat.format(
                showText,
                WordAppData.getInstance(context).getAppNameCn(), WordAppData.getInstance(context).getAppId());
        String imgUrl = WordAppData.getInstance(context).getAppLogoUrl();
//        String url = "https://a.app.qq.com/o/simple.jsp?pkgname="+ BuildConfig.APPLICATION_ID;
        String url = WordAppData.getInstance(context).getAppShareUrl();
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

    //分享单词到小程序
    public static void prepareWord(Context context,String word,int bookId,int unitId,int voaId,String imageUrl){
        String url = "http://voa.iyuba.cn/voa/shareApp.jsp?appType=juniorenglish";

        OnekeyShare oks = new OnekeyShare();
        oks.addHiddenPlatform(QQ.NAME);
        oks.addHiddenPlatform(QZone.NAME);
        oks.addHiddenPlatform(SinaWeibo.NAME);
        oks.setTitle("我正在学习单词 "+word+" ,快来和我一起学习吧");
        oks.setTitleUrl(url);
        oks.setText(word);

        if (TextUtils.isEmpty(imageUrl)){
            oks.setImageUrl(AppData.APP_PIC);
        }else {
            oks.setImageUrl(imageUrl);
        }

        oks.setUrl(url);
        oks.setComment(MessageFormat.format(
                context.getString(R.string.app_share_content), WordAppData.getInstance(context).getAppNameCn()));
        oks.setSite(WordAppData.getInstance(context).getAppNameCn());
        oks.setSiteUrl(url);
        oks.setSilent(true);
        if (AppData.openWxSmallShare){
            oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                @Override
                public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                    if ("Wechat".equals(platform.getName())) {
                        paramsToShare.setWxMiniProgramType(0);//0-正式，1-测试，2-预览
                        if (bookId > 0) {
                            //packageA/pages/wordStudy/wordStudy?word=encode(word)&bookId=205&unitId=1
                            String wordEncode = word;
                            try {
                                wordEncode = URLEncoder.encode(word, "UTF-8");
                                wordEncode = wordEncode.replaceAll("\\+", "%20");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            String path = "packageA/pages/wordStudy/wordStudy?word="+wordEncode+"&bookId="+bookId+"&unitId="+unitId;
                            Log.e("ShareSDK", "shareWechatMessage MiniProgram path " + path);
                            paramsToShare.setWxPath(path);
                        } else {
                            paramsToShare.setWxPath("pages/index/index");
                        }
                        paramsToShare.setWxUserName(AppData.getWeChatName(context));
                        paramsToShare.setShareType(Platform.SHARE_WXMINIPROGRAM);
                        paramsToShare.setWxWithShareTicket(true);
                        if (TextUtils.isEmpty(imageUrl)){
                            paramsToShare.setImageUrl(AppData.APP_PIC);
                        }else {
                            paramsToShare.setImageUrl(imageUrl);
                        }
                    }
                    Log.e("ShareSDK", "others " + paramsToShare.toMap().toString());
                }
            });
        }
        oks.setLatitude((float) 0);
        oks.setLongitude((float) 0);
        oks.setCallback(getListener(context,voaId,WordAppData.getInstance(context).getAppId(),voaId));
        oks.show(context);
    }

    private static PlatformActionListener getListener(Context context,int uid,int appId,int voaId){
        return new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
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


                String baseUrl = "http://apis.iyuba.cn/credits/updateScore.jsp";

                HttpManager.getUploadExamApi().integral(baseUrl,srid,1,getTime(),uid,appId,voaId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<DownLoadJFResult>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(DownLoadJFResult result) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (result != null && "200".equals(result.getResult())) {
                                            Toast.makeText(context, "分享成功" + result.getAddcredit() + "分，当前积分为："
                                                            + result.getTotalcredit() + "分",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(Throwable e) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancel(Platform platform, int i) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "分享已取消", Toast.LENGTH_SHORT).show();
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
}
