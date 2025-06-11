package com.iyuba.talkshow.lil.help_fix.data.library;

/**
 * @desction: 类型库
 * @date: 2023/4/10 18:38
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public interface TypeLibrary {

    //主体类型
    class CompanyType{
        public static final String BJ_IYUBA = "BJ_IYUBA";//北京爱语吧
        public static final String BJ_IYUYAN = "BJ_IYUYAN";//爱语言(北京)
        public static final String SD_IYUBA = "SD_IYUBA";//山东爱语吧
        public static final String SZ_IYUBA = "SZ_IYUBA";//苏州爱语吧
        public static final String JN_WYT = "JN_WYT";//济南万云天
        public static final String RZ_GD = "RZ_GD";//日照国东
        public static final String SY_YF = "SY_YF";//沈阳一方
        public static final String JN_ZMLM = "JN_ZMLM";//济南珠穆朗玛
    }

    /*************************参数类型***********************/
    //解析数据类型
    class DataFixType{
        public static final String JSON = "json";
        public static final String XML = "xml";
    }

    //渠道类型
    class PlatformType{
        public static final String Android = "android";
        public static final String iOS = "ios";
        public static final String Windows = "windows";
        public static final String Mac = "mac";
        public static final String Linux = "linux";
    }

    //刷新数据的界面
    class RefreshDataType{
        public static final String eval_rank = "evalRank";//评测排行
        public static final String dubbing_rank = "dubbing_rank";//配音排行

        public static final String userInfo = "userInfo";//用户信息

        //小说界面
        public static final String novel = "database/novel";
        //新概念界面
        public static final String concept = "concept";
        //单词界面
        public static final String word = "word";
        //中小学界面
        public static final String junior = "junior";

        //听力报告
        public static final String listenDialog = "listenDialog";
        //学习界面
        public static final String study = "study";

        //新概念单词界面
        public static final String concept_word = "concept_word";
        //新概念界面暂停播放
        public static final String concept_play = "concept_play";

        //中小学的课程收藏刷新
        public static final String junior_lesson_collect = "junior_lesson_collect";
        //小说的课程收藏刷新
        public static final String novel_lesson_collect = "novel_lesson_collect";

        //切换为上一章节
        public static final String study_pre = "study_pre";
        //切换为下一章节
        public static final String study_next = "study_next";
        //随机切换章节播放
        public static final String study_random = "study_random";

        //退出app
        public static final String existApp = "existApp";

        //原文停止
        public static final String read_stop = "read_stop";

        //原文刷新提示
        public static final String read_refresh_tips = "read_refresh_tips";

        //奖励接口刷新
        public static final String reward_refresh_toast = "reward_refresh_toast";
        public static final String reward_refresh_dialog = "reward_refresh_dialog";

        //界面类型
        public static final String study_read = "study_read";//原文界面
        public static final String study_other = "study_other";//其他界面
    }

    //文件类型
    class FileType{
        public static final String MP3 = ".mp3";//mp3
        public static final String MP4 = ".mp4";//mp4
    }

    //支付方式类型
    class PayType{
        public static final String aliPay = "aliPay";//支付宝
        public static final String weChat = "weChat";//微信支付
    }

    /********************用户信息类型***********************/
    //用户信息激活类型
    class UserActiveType{
        public static final String ACTIVE = "ACTIVE";//激活
        public static final String UN_ACTIVE = "UN_ACTIVE";//未激活
    }

    //用户信息禁用类型
    class UserWarnType{
        public static final String WARN = "WARN";//禁用
        public static final String UN_WARN = "UN_WARN";//未禁用
    }

    /***************************页面跳转类型***************************/
    //选书跳转类型
    class ChooseJumpType{
        public static final String NOVEL = "database/novel";//小说
        public static final String CONCEPT = "concept";//新概念
    }

    //登录跳转类型
    class LoginJumpType{
        public static final String ACCOUNT = "account";//账号
        public static final String WECHAT = "wechat";//微信
    }

    //会员跳转类型
    class VipJumpType{
        public static final String APP = "app";//本应用会员
        public static final String ALL = "all";//全站会员
        public static final String GOLD = "gold";//黄金会员
        public static final String IYUB = "iyub";//爱语币
    }

    /***********************************内容类型*******************/
    //课程类型
    class BookType{
        public static final String bookworm = "bookworm";//书虫
        public static final String newCamstoryColor = "newCamstoryColor";//剑桥彩绘
        public static final String newCamstory = "newCamstory";//剑桥非彩绘

        public static final String conceptFourUS = "conceptFourUS";//新概念-全四册-美音
        public static final String conceptFourUK = "conceptFourUK";//新概念-全四册-英音
        public static final String conceptJunior = "conceptJunior";//新概念-青少版
        public static final String conceptFour = "conceptFour";//新概念-全四册(单词使用)

        public static final String junior_primary = "junior_primary";//中小学-小学
        public static final String junior_middle = "junior_middle";//中小学-初中
    }

    //文本显示类型
    class TextShowType{
        public static final String ALL = "all";//中英文显示
        public static final String CN = "cn";//只显示中文
        public static final String EN = "en";//只显示英文
    }

    //pdf文件下载类型
    class PdfFileType{
        public static final String ALL = "all";//双语显示
        public static final String CN = "cn";//只显示中文
        public static final String EN = "en";//只显示英文
    }

    //播放模式类型
    class PlayModeType{
        public static final String SINGLE_SYNC = "single_sync";//单曲循环
        public static final String RANDOM_PLAY = "random_play";//随机播放
        public static final String ORDER_PLAY = "order_play";//顺序播放
    }

    //类型设置数据
    class SettingType{
        public static final String STUDY_SPEED = "study_speed";//学习界面-倍速
    }

    //单词闯关类型
    class WordTrainType{
        public static final String Train_enToCn = "enToCn";//英汉训练
        public static final String Train_cnToEn = "cnToEn";//汉英训练
        public static final String Word_spell = "wordSpell";//单词拼写
        public static final String Train_listen = "listenTrain";//听力训练
    }
}
