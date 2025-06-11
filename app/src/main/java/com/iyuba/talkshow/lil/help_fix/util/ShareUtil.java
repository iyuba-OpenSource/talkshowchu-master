package com.iyuba.talkshow.lil.help_fix.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.NetHostManager;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.CommonDataManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Integral_bean;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.util.NetStateUtil;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.text.MessageFormat;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ReflectablePlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @title: 分享功能
 * @date: 2023/5/15 18:52
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ShareUtil {

    private static ShareUtil instance;

    private Context context;
    private String voaId;

    public static ShareUtil getInstance() {
        if (instance == null) {
            synchronized (ShareUtil.class) {
                if (instance == null) {
                    instance = new ShareUtil();
                }
            }
        }
        return instance;
    }

    //分享课程
    public void shareArticle(Context context, String types, String bookId,String voaId, long userId,String title,String text,String imageUrl,String shareUrl) {
        this.context = context;
        this.voaId = voaId;

        if (!NetStateUtil.isConnected(context)) {
            ToastUtil.showToast(context, "分享功能需要打开网络数据连接");
            return;
        }

//        if (!InfoHelper.getInstance().openShare()) {
//            ToastUtil.showToast(context, "暂不支持分享功能");
//            return;
//        }

        PlatformActionListener listener = null;
        if (userId != 0) {
            listener = platformActionListener;
        }

        if (TextUtils.isEmpty(imageUrl)){
            imageUrl = App.Url.APP_ICON_URL;
        }

        OnekeyShare onekeyShare = new OnekeyShare();
        //设置分享是否显示
        /*if (!InfoHelper.getInstance().openQQShare()){
            onekeyShare.addHiddenPlatform(QQ.NAME);
            onekeyShare.addHiddenPlatform(QZone.NAME);
        }
        if (!InfoHelper.getInstance().openWeChatShare()){
            onekeyShare.addHiddenPlatform(Wechat.NAME);
            onekeyShare.addHiddenPlatform(WechatMoments.NAME);
            onekeyShare.addHiddenPlatform(WechatFavorite.NAME);
        }
        if (!InfoHelper.getInstance().openWeiboShare()){
            onekeyShare.addHiddenPlatform(SinaWeibo.NAME);
        }*/
        //设置标题
        onekeyShare.setTitle(title);
        //设置标题的链接-qq和qq空间使用
        onekeyShare.setTitleUrl(shareUrl);
        //分享文本
        onekeyShare.setText(text);
        //分享图片链接
        onekeyShare.setImageUrl(imageUrl);
        //分享链接-标题链接
        onekeyShare.setUrl(shareUrl);
        //分享推荐内容
        onekeyShare.setComment(MessageFormat.format("这款应用 {0} 真的很不错啊~推荐！",context.getResources().getString(R.string.app_name)));
        //分享类型
        onekeyShare.setSite(context.getResources().getString(R.string.app_name));
        //分享类型链接
        onekeyShare.setSiteUrl(shareUrl);
        //设置静音
        onekeyShare.setSilent(true);
        //设置地理位置-这里默认为没有地理位置
        onekeyShare.setLatitude(0);
        onekeyShare.setLongitude(0);
        //设置是否可用小程序分享
//        if (isWxSmallAvailable(context,types)){
//            onekeyShare.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
//                @Override
//                public void onShare(Platform platform, Platform.ShareParams shareParams) {
//                    if (platform.getName().equals("Wechat")){
//                        shareWxSmallCourse(types,bookId,voaId,imageUrl,shareParams);
//                        return;
//                    }
//                }
//            });
//        }
        //设置回调
        onekeyShare.setCallback(listener);
        //展示分享
        onekeyShare.show(context);
    }

    //分享评测
    public void shareEval(Context context, String types, String voaId,String shuoshuoId,String evalUrl,int userId,String title,String text){
        this.context = context;
        this.voaId = voaId;

        if (!NetStateUtil.isConnected(context)) {
            ToastUtil.showToast(context, "分享功能需要打开网络数据连接");
            return;
        }

        /*if (!InfoHelper.getInstance().openShare()) {
            ToastUtil.showToast(context, "暂不支持分享功能");
            return;
        }*/

        //分享的链接
        String shareUrl = getEvalShareUrl(types,shuoshuoId,evalUrl);
        PlatformActionListener listener = null;
        if (userId != 0) {
            listener = platformActionListener;
        }

        OnekeyShare onekeyShare = new OnekeyShare();
        //设置分享是否显示
        /*if (!InfoHelper.getInstance().openQQShare()){
            onekeyShare.addHiddenPlatform(QQ.NAME);
            onekeyShare.addHiddenPlatform(QZone.NAME);
        }
        if (!InfoHelper.getInstance().openWeChatShare()){
            onekeyShare.addHiddenPlatform(Wechat.NAME);
            onekeyShare.addHiddenPlatform(WechatMoments.NAME);
            onekeyShare.addHiddenPlatform(WechatFavorite.NAME);
        }
        if (!InfoHelper.getInstance().openWeiboShare()){
            onekeyShare.addHiddenPlatform(SinaWeibo.NAME);
        }*/
        //设置标题
        onekeyShare.setTitle(title);
        //设置标题的链接-qq和qq空间使用
        onekeyShare.setTitleUrl(shareUrl);
        //分享文本
        onekeyShare.setText(text);
        //分享图片链接
        onekeyShare.setImageUrl(App.Url.APP_ICON_URL);
        //分享链接-标题链接
        onekeyShare.setUrl(shareUrl);
        //分享推荐内容
        onekeyShare.setComment(MessageFormat.format("这款应用 {0} 真的很不错啊~推荐！",context.getResources().getString(R.string.app_name)));
        //分享类型
        onekeyShare.setSite(context.getResources().getString(R.string.app_name));
        //分享类型链接
        onekeyShare.setSiteUrl(shareUrl);
        //设置静音
        onekeyShare.setSilent(true);
        //设置地理位置-这里默认为没有地理位置
        onekeyShare.setLatitude(0);
        onekeyShare.setLongitude(0);
        //设置是否可用小程序分享
//        if (isWxSmallAvailable(context,types)){
//            onekeyShare.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
//                @Override
//                public void onShare(Platform platform, Platform.ShareParams shareParams) {
//                    if (platform.getName().equals("Wechat")){
//                        shareWxSmallEval(types,shuoshuoId,null,shareParams);
//                        return;
//                    }
//                }
//            });
//        }
        //设置回调
        onekeyShare.setCallback(listener);
        //展示分享
        onekeyShare.show(context);
    }

    //分享链接
    public void shareUrl(Context context,String title,String msg,String imageUrl,String shareUrl,int userId,String voaId){
        this.context = context;
        this.voaId = voaId;

        if (!NetStateUtil.isConnected(context)) {
            ToastUtil.showToast(context, "分享功能需要打开网络数据连接");
            return;
        }

        /*if (!InfoHelper.getInstance().openShare()) {
            ToastUtil.showToast(context, "暂不支持分享功能");
            return;
        }*/

        if (TextUtils.isEmpty(imageUrl)){
            imageUrl = App.Url.APP_ICON_URL;
        }

        PlatformActionListener listener = null;
        if (userId != 0) {
            listener = platformActionListener;
        }

        OnekeyShare onekeyShare = new OnekeyShare();
        //设置分享是否显示
        /*if (!InfoHelper.getInstance().openQQShare()){
            onekeyShare.addHiddenPlatform(QQ.NAME);
            onekeyShare.addHiddenPlatform(QZone.NAME);
        }
        if (!InfoHelper.getInstance().openWeChatShare()){
            onekeyShare.addHiddenPlatform(Wechat.NAME);
            onekeyShare.addHiddenPlatform(WechatMoments.NAME);
            onekeyShare.addHiddenPlatform(WechatFavorite.NAME);
        }
        if (!InfoHelper.getInstance().openWeiboShare()){
            onekeyShare.addHiddenPlatform(SinaWeibo.NAME);
        }*/
        //设置标题
        onekeyShare.setTitle(title);
        //设置标题的链接-qq和qq空间使用
        onekeyShare.setTitleUrl(shareUrl);
        //分享文本
        onekeyShare.setText(msg);
        //分享图片链接
        onekeyShare.setImageUrl(imageUrl);
        //分享链接-标题链接
        onekeyShare.setUrl(shareUrl);
        //分享推荐内容
        onekeyShare.setComment(msg);
        //分享类型
        onekeyShare.setSite(context.getResources().getString(R.string.app_name));
        //分享类型链接
        onekeyShare.setSiteUrl(shareUrl);
        //设置静音
        onekeyShare.setSilent(true);
        //设置地理位置-这里默认为没有地理位置
        onekeyShare.setLatitude(0);
        onekeyShare.setLongitude(0);
        //设置回调
        onekeyShare.setCallback(listener);
        //展示分享
        onekeyShare.show(context);
    }

    //分享单词
    public void shareWord() {

    }

    //分享排名
    public void shareRank() {

    }

    /***************************辅助功能*********************************/
    //获取分享链接
    public String getCourseShareUrl(String types, String level,String voaId) {
        String shareUrl = "";
        switch (types) {
            case TypeLibrary.BookType.junior_primary://小学
            case TypeLibrary.BookType.junior_middle://初中
                //http://m.iyuba.cn/voaS/playPY.jsp?apptype=juniorEnglish&id=316044
                shareUrl = "http://m." + NetHostManager.getInstance().getDomainShort() + "/voaS/playPY.jsp?apptype=" + FixUtil.getTopic(types) + "&id=" + voaId;
                break;
            case TypeLibrary.BookType.bookworm://书虫
            case TypeLibrary.BookType.newCamstory://剑桥小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥小说馆彩绘
                //http://www.aimetaverse.net.cn/bookInfo.jsp?bid=0&levelid=0&from=newCamstoryColor
                shareUrl = "http://www.aimetaverse.net.cn/bookInfo.jsp?bid="+voaId+"&from="+types+"&levelid="+level;
                break;
        }

        return shareUrl;
    }

    //获取评测的分享链接
    private String getEvalShareUrl(String types,String shuoshuoId,String evalUrl){
        return "http://voa." + NetHostManager.getInstance().getDomainShort() + "/voa/play.jsp?id=" + shuoshuoId
                + "&addr=" + evalUrl + "&apptype=" + FixUtil.getTopic(types);
    }

    //分享回调
    private PlatformActionListener platformActionListener = new ReflectablePlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
            String integralId = "";
            switch (platform.getName()) {
                case "QQ":
                case "Wechat":
                case "WechatFavorite":
                    integralId = "7";
                    break;
                case "QZone":
                case "WechatMoments":
                case "SinaWeibo":
                case "TencentWeibo":
                    integralId = "19";
                    break;
            }

            Observable.just(String.class)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Class<String>>() {
                        @Override
                        public void accept(Class<String> stringClass) throws Exception {
                            ToastUtil.showToast(context, "分享成功");
                        }
                    });

            //提交积分接口
            CommonDataManager.getIntegralAfterShare(integralId, UserInfoManager.getInstance().getUserId(), voaId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Integral_bean>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Integral_bean integral_bean) {
                            Log.d("分享后获取积分", "获取积分成功");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("分享后获取积分", "获取积分失败");
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

        @Override
        public void onError(Platform platform, int action, Throwable t) {
            Observable.just(String.class)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Class<String>>() {
                        @Override
                        public void accept(Class<String> stringClass) throws Exception {
                            ToastUtil.showToast(context, "分享失败");
                        }
                    });
        }

        @Override
        public void onCancel(Platform platform, int action) {
            Observable.just(String.class)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Class<String>>() {
                        @Override
                        public void accept(Class<String> stringClass) throws Exception {
                            ToastUtil.showToast(context, "分享取消");
                        }
                    });
        }
    };

    //判断小程序是否可用
    private boolean isWxSmallAvailable(Context context,String types){

        if (types.equals(TypeLibrary.BookType.junior_primary)
                ||types.equals(TypeLibrary.BookType.junior_middle)){
            //中小学
            IWXAPI iwxapi = WXAPIFactory.createWXAPI(context,getWxSmallId(types));
            if (iwxapi.isWXAppInstalled()){
                return iwxapi.getWXAppSupportAPI() > Build.MINIPROGRAM_SUPPORTED_SDK_INT;
            }
        }

        return false;
    }

    //根据类型获取小程序的id
    private String getWxSmallId(String types){
        switch (types){
            case TypeLibrary.BookType.junior_primary:
                return "gh_ce4ab26820ab";
            case TypeLibrary.BookType.junior_middle:
                return "";
        }
        return null;
    }


    //小程序-课程内容
    private void shareWxSmallCourse(String types,String bookId,String voaId,String imageUrl,Platform.ShareParams shareParams){
        //设置为正式版
        shareParams.setWxMiniProgramType(ConstantsAPI.WXMiniProgram.MINI_PROGRAM_TYPE_RELEASE);
        //设置路径
        if (TextUtils.isEmpty(voaId)){
            shareParams.setWxPath("pages/index/index");
        }else {
            String path = String.format("pages/detail/detail?voaid=%1$s&series=%2$s", voaId, bookId);
            shareParams.setWxPath(path);
        }
        //设置code
        shareParams.setWxUserName(getWxSmallId(types));
        //设置图片链接
        shareParams.setImageUrl(imageUrl);
        //设置类型
        shareParams.setShareType(Platform.SHARE_WXMINIPROGRAM);
        //设置分享卡片
        shareParams.setWxWithShareTicket(true);
    }

    //小程序-评测内容
    private void shareWxSmallEval(String types,String shuoshuoId,String imageUrl,Platform.ShareParams shareParams){
        //设置为正式版
        shareParams.setWxMiniProgramType(ConstantsAPI.WXMiniProgram.MINI_PROGRAM_TYPE_RELEASE);
        //设置路径
        String path = String.format("pages/shareRec/shareRec?shuoshuoId=%1$s", shuoshuoId);
        shareParams.setWxPath(path);
        //设置code
        shareParams.setWxUserName(getWxSmallId(types));
        //设置图片链接
        shareParams.setImageUrl(imageUrl);
        //设置类型
        shareParams.setShareType(Platform.SHARE_WXMINIPROGRAM);
        //设置分享卡片
        shareParams.setWxWithShareTicket(true);
    }
}
