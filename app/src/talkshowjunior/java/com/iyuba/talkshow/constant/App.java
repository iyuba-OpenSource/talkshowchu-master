package com.iyuba.talkshow.constant;

import android.text.TextUtils;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.manager.AbilityControlManager;
import com.iyuba.talkshow.lil.help_fix.data.bean.BookChooseShowBean;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.ui.courses.coursechoose.TypeHelper;
import com.tencent.vasdolly.helper.ChannelReaderUtil;

/**
 * App
 */
public interface App {
    int APP_ID = 259;
    String SHARE_NAME_EN = "juniorEnglish";
    String APP_NAME_EN = "juniorEnglish";
//        String APP_NAME_CH = "初中英语口语秀";//华为渠道
    String APP_NAME_CH = ResUtil.getInstance().getContext().getResources().getString(R.string.app_name);
    String APP_NAME_PRIVACY = "隐私政策";
    String PLATFORM = "android";
    // TODO: 2022/6/16 这里是大版本，开启更新功能
    boolean APP_CHECK_UPGRADE = true;
    boolean APP_TENCENT_PRIVACY = true;
    boolean APP_HUAWEI_PRIVACY = true;
    boolean APP_HUAWEI_COMPLAIN = false;
    boolean APP_MINI_PRIVACY = false;
    boolean APP_TENCENT_MOOC = true;
    boolean APP_MOC_BOTTOM = true;
    boolean APP_WORD_BOTTOM = false;
    boolean APP_CHECK_PERMISSION = false;
    boolean APP_CHECK_AGREE = false;
    int APP_SHOW_SUBPAGE = 1;

    String APP_SAVE_DIR = "appUpdate";
    String UNDERLINE = "_";
    String APK_SUFFIX = ".apk";
    String AD_FOLDER = "ad";

    class Url {
        //public static String APP_ICON_URL = "http://app."+Constant.Web.WEB_SUFFIX.replace("/","")+"/android/images/juniorenglish/juniorenglish.png";
        public static String APP_ICON_URL = "http://app." + Constant.Web.WEB_SUFFIX.replace("/", "") + "/ios/images/junior%20English/junior%20English.png";

        //北京爱语吧
//        public static String PROTOCOL_USAGE = Constant.Url.PROTOCOL_BJIYB_USAGE;
//        public static String PROTOCOL_URL = Constant.Url.PROTOCOL_BJIYB_PRIVACY;
//        public static String CHILD_PROTOCOL_URL = Constant.Url.CHILD_PROTOCOL_BJIYB_PRIVACY;
//        public static String VIP_AGREEMENT_URL = Constant.Url.VIP_AGREEMENT_BJIYB_PRIVACY;

        //北京爱语言
        public static String PROTOCOL_USAGE = Constant.Url.PROTOCOL_BJIYY_USAGE;
        public static String PROTOCOL_URL = Constant.Url.PROTOCOL_BJIYY_PRIVACY;
        public static String CHILD_PROTOCOL_URL = Constant.Url.CHILD_PROTOCOL_BJIYY_PRIVACY;
        public static String VIP_AGREEMENT_URL = Constant.Url.VIP_AGREEMENT_BJIYY_PRIVACY;


        public static String SHARE_APP_URL = Constant.Url.APP_SHARE_URL + SHARE_NAME_EN;
    }

    int DEFAULT_QQ_ID = 9972;
    String DEFAULT_QQ_GROUP = "初中英语官方群";
    String DEFAULT_WORDS = "middle";
    String DEFAULT_SERIES = "313,314,315,316";
    String DEFAULT_TITLE = "7年级上";

    // TODO: 2023/8/30 oaid留待后续使用，暂时不删除
    /****oaid升级****/
    //oaid的证书名称
    String oaid_pem = "com.iyuba.talkshow.junior.cert.pem";
    //是否需要oaid重新赋值
    boolean APP_OAID_UPDATE = true;

    /****人教版处理****/
    //默认显示的数据内容
    static BookChooseShowBean getBookDefaultShowData(){
        //默认显示的
        BookChooseShowBean showBean_default = new BookChooseShowBean(316,488,"新7年级上");

        //处理资质问题显示的
        BookChooseShowBean showBean_fixed = new BookChooseShowBean(331,388,"北师版七年级上");

//        if (UserInfoManager.getInstance().isVip()){
//            return seriesId_default;
//        }

        //华为默认不显示人教版
//        String channel = ChannelReaderUtil.getChannel(ResUtil.getInstance().getContext());
//        if (channel.toLowerCase().equals("huawei")){
//            return showBean_fixed;
//        }

        //限制人教版显示
        if (AbilityControlManager.getInstance().isLimitPep()){
            return showBean_fixed;
        }

        //正常默认显示人教版
        return showBean_default;
    }
}
