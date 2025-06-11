package com.iyuba.talkshow.newdata;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.ui.main.drawer.Share;
import com.tencent.mm.opensdk.constants.ConstantsAPI;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.sharesdk.wechat.utils.WXMiniProgramObject;
import personal.iyuba.personalhomelibrary.utils.ToastFactory;

/**
 * Created by carl shen on 2020/8/3
 * New Primary English, new study experience.
 */
public class ShareUtils {

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if (mContext == null) {
                return;
            }
            if (i != 19 && i != 49) {
                return;
            }else {
                ToastFactory.showShort(mContext,"分享成功！");
            }
        }
    };
    private Context mContext;
    public PlatformActionListener platformActionListener = new PlatformActionListener() {
        public void onCancel(Platform paramAnonymousPlatform, int paramAnonymousInt) {
        }

        public void onComplete(Platform platform, int paramAnonymousInt, HashMap<String, Object> paramAnonymousHashMap) {
            Message message = new Message();
            message.obj = platform.getName();
            if ((!platform.getName().equals("QQ")) && (!platform.getName().equals("Wechat")) && (!platform.getName().equals("WechatFavorite"))) {
                if ((platform.getName().equals("QZone")) || (platform.getName().equals("WechatMoments")) || (platform.getName().equals("SinaWeibo")) || (platform.getName().equals("TencentWeibo"))) {
                    message.what = 19;
                }
            } else {
                message.what = 49;
            }
            handler.sendMessage(message);
        }

        public void onError(Platform paramAnonymousPlatform, int paramAnonymousInt, Throwable paramAnonymousThrowable) {
        }
    };

    public PlatformActionListener defaultPlatformActionListener = new PlatformActionListener() {
        public void onCancel(Platform paramAnonymousPlatform, int paramAnonymousInt) {
        }

        public void onComplete(Platform platform, int paramAnonymousInt, HashMap<String, Object> paramAnonymousHashMap) {
        }

        public void onError(Platform paramAnonymousPlatform, int paramAnonymousInt, Throwable paramAnonymousThrowable) {
        }
    };

    private int voaId;

    public void setMContext(Context paramContext) {
        this.mContext = paramContext;
    }

    public void setVoaId(int paramInt) {
        this.voaId = paramInt;
    }

    public void showShare(Context mContext, String shuoId, String imageUrl, String siteUrl, String title, String content, PlatformActionListener paramPlatformActionListener) {
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.removeAccount(true);
        ShareSDK.removeCookieOnAuthorize(true);

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

        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(siteUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        oks.setImageUrl(imageUrl);

        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(siteUrl);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(mContext.getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(siteUrl);
        oks.setCallback(platformActionListener);
        oks.disableSSOWhenAuthorize();
        if (ConfigData.openWxSmallShare) {
            oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                @Override
                public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                    if ("Wechat".equals(platform.getName())) {
                        Log.e("ShareUtils", "Wechat title " + title);
                        if (Share.isWXSmallAvailable(mContext)) {
                            paramsToShare.setWxMiniProgramType(WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE);
                            //更换路径
//                            String path = String.format("pages/shareRec/shareRec?shuoshuoId=%s", shuoId);
                            String path = String.format("/component/pages/shareRec/shareRec?shuoshuoId=%s", shuoId);
                            Log.e("ShareSDK", "shareMessage MiniProgram path " + path);
                            paramsToShare.setWxPath(path);
                            paramsToShare.setWxUserName(ConfigData.wx_small_name);
                            paramsToShare.setShareType(Platform.SHARE_WXMINIPROGRAM);
                            paramsToShare.setWxWithShareTicket(true);
                            paramsToShare.setImageUrl(imageUrl);
                            Log.e("ShareUtils", "Wechat MiniProgram " + paramsToShare.toMap().toString());
                            return;
                        }
                    }
                    Log.e("ShareUtils", "others " + paramsToShare.toMap().toString());
                }
            });
        }
        // 启动分享GUI
        oks.show(mContext);
    }
}
