package com.iyuba.talkshow.lil.help_fix.util;


import android.text.TextUtils;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.UrlLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.NetHostManager;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.util.TimeUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @desction: 链接拼接的工具
 * @date: 2023/4/21 16:36
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class FixUtil {

    /***********图片拼接***************/
    //用户信息接口的头像的拼接(头像以20001接口为准，其他接口可能不准)
    //http://static1.iyuba.cn/uc_server
    public static String fixUserPicUrl(String picSuffix){
        return "http://static1."+ NetHostManager.getInstance().getDomainShort() +"/uc_server/"+picSuffix;
    }

    //新概念单词的图片拼接
    //http://static2.iyuba.cn/images/words/287/25/8.jpg
    public static String fixConceptWordPicUrl(String suffix){
        String preFix = "http://static2."+NetHostManager.getInstance().getDomainShort()+"/images/words/";
        return preFix+suffix;
    }

    //中小学英语的点读的图片拼接
    //http://staticvip.iyuba.cn/images/voa//202110/313001_1.jpg
    public static String fixJuniorImagePicUrl(String suffix){
        String prefix = "http://staticvip"+NetHostManager.getInstance().getDomainShort()+"/images/voa";
        return prefix+suffix;
    }

    //小说的音频和图片的拼接--和音频的一样
    //会员：http://staticvip2.iyuba.cn/+字段返回的内容（例如：http://static2.iyuba.cn/bookworm/images/1_1.jpg）
    //非会员：http://static2.iyuba.cn/+字段返回的内容（例如：http://staticvip2.iyuba.cn/bookworm/images/1_1.jpg）
    public static String fixNovelPicUrl(String suffix){
        String prefix = "http://static2."+NetHostManager.getInstance().getDomainShort();
        if (UserInfoManager.getInstance().isVip()){
            prefix = "http://staticvip2."+NetHostManager.getInstance().getDomainShort();
        }

        if (suffix.startsWith("/")){
            return prefix+suffix;
        }
        return prefix+"/"+suffix;
    }

    /**
     * 单词的图片拼接
     * //http://static2.iyuba.cn/images/words/287/25/8.jpg
     * 接口数据为：289/25/1.jpg
     * 适用的类型：所有的类型
     * 使用的模块：单词模块
     */
    public static String fixWordPicUrl(String suffix){
        String preFix = "http://static2."+NetHostManager.getInstance().getDomainShort()+"/images/words";
        if (!suffix.startsWith("/")){
            suffix = "/"+suffix;
        }
        return preFix+suffix;
    }

    /*************音频拼接*************/
    /****小说****/
    //音频文件的拼接
    //会员：http://staticvip2.iyuba.cn/+字段返回的内容（例如：http://static2.iyuba.cn/bookworm/images/1_1.jpg）
    //非会员：http://static2.iyuba.cn/+字段返回的内容（例如：http://staticvip2.iyuba.cn/bookworm/images/1_1.jpg）
    public static String fixNovelAudioUrl(String urlSuffix){
        if (urlSuffix.startsWith("/")){
            return "http://staticvip2."+NetHostManager.getInstance().getDomainShort()+urlSuffix;
        }

        return "http://staticvip2."+NetHostManager.getInstance().getDomainShort()+"/"+urlSuffix;
    }

    /****新概念****/
    //音频播放地址--美音
    //规则:http://static2.iyuba.cn/newconcept/bookid_voaid%1000.mp3 //bookid_voaid对1000取余数.mp3
    public static String fixConceptUSPlayUrl(String bookId,String voaId){
        if (TextUtils.isEmpty(bookId)||TextUtils.isEmpty(voaId)){
            return null;
        }

        int play1 = Integer.parseInt(voaId)%1000;
        String playSuffix = bookId+"_"+play1+".mp3";
        return UrlLibrary.HTTP_STATIC2+ NetHostManager.getInstance().getDomainShort()+"/newconcept/"+playSuffix;
    }

    //音频播放地址--英音
    //规则:http://static2.iyuba.cn/newconcept/british/bookid/bookid_(voaid/10)%1000.mp3//bookid_voaid除以10后对1000取余数.mp3
    public static String fixConceptUKPlayUrl(String bookId,String voaId){
        if (TextUtils.isEmpty(bookId)||TextUtils.isEmpty(voaId)){
            return null;
        }

        int play1 = Integer.parseInt(voaId)/10;
        play1 = play1%1000;
        String playSuffix = bookId+"_"+play1;
        return UrlLibrary.HTTP_STATIC2+NetHostManager.getInstance().getDomainShort()+"/newconcept/british/"+bookId+"/"+playSuffix+".mp3";
    }

    //音频播放地址--青少版
    //规则：
    public static String fixConceptJuniorPlayUrl(String voaId){
        if (TextUtils.isEmpty(voaId)){
            return null;
        }

        return "http://staticvip."+NetHostManager.getInstance().getDomainShort()+"/sounds/voa/sentence/202005/"+voaId+"/"+voaId+ TypeLibrary.FileType.MP3;
    }

    /***中小学****/
    //获取课程播放的音频地址
    //这里需要使用的地址：http://staticvip.iyuba.cn/sounds/voa/202002/313002.mp3
    //需要处理成的地址：http://staticvip.iyuba.cn/sounds/voa/sentence/202002/313003/313003.mp3
    public static String fixJuniorAudioUrl(String voaId,String suffix){
        if (TextUtils.isEmpty(suffix)){
            return null;
        }

        //将完整的音频url中截取相应的数据
        String patternText = "/sounds/voa/";
        int index = suffix.indexOf(patternText);

        //取出后面需要使用的数据
        int fileIndex = suffix.lastIndexOf(".");

        //第一段数据
        //这里的数据：http://staticvip.iyuba.cn/sounds/voa/
        String prefixStr = suffix.substring(0,index+patternText.length());

        //第二段数据
        //这里的数据：/202002/313001
        String suffixStr = suffix.substring(index+patternText.length()-1,fileIndex);

        //合并数据
        return prefixStr+"sentence"+suffixStr+"/"+voaId+TypeLibrary.FileType.MP3;
    }

    /****评测****/
    /**
     * 评测的音频播放地址--中小学类型
     * http://userspeech.iyuba.cn/voa/wav8/202303/concept/20230302/16777456102709654.mp3
     * 接口数据为：wav8/202303/concept/20230302/16777456102709654.mp3
     * 适用于类型：中小学类型
     * 适用于模块：句子评测或者单词评测
     */
    public static String fixJuniorEvalAudioUrl(String suffix){
        String playPrefix = UrlLibrary.HTTP_USERSPEECH+NetHostManager.getInstance().getDomainShort()+"/voa";
        if (!suffix.startsWith("/")){
            suffix = "/"+suffix;
        }
        return playPrefix+suffix;
    }

    /**
     * 评测的音频播放地址--小说类型
     * http://iuserspeech.iyuba.cn:9001/voa/wav8/202306/bookworm/20230612/16865748809653924.mp3
     * 接口数据为：wav6/202307/bookworm/20230706/16886219972130768.mp3
     * 适用于类型：小说
     * 适用于模块：句子评测
     */
    public static String fixNovelEvalAudioUrl(String suffix){
        String playPrefix = UrlLibrary.HTTP_IUSERSPEECH+NetHostManager.getInstance().getDomainShort()+UrlLibrary.SUFFIX_9001+"/voa";
        if (!suffix.startsWith("/")){
            suffix = "/"+suffix;
        }
        return playPrefix+suffix;
    }

    /***************视频拼接****************/
    //中小学配音的视频地址
    //http://static0.iyuba.cn/video/voa/313/313002.mp4
    public static String fixJuniorVideoUrl(String suffix){
        String prefix = "http://static0."+NetHostManager.getInstance().getDomainShort();
        if (UserInfoManager.getInstance().isVip()){
            prefix = "http://staticvip."+NetHostManager.getInstance().getDomainShort();
        }

        return prefix+suffix;
    }


    /***********************辅助功能***************/
    //获取首字母
    public static String getFirstChar(String name) {
        String subString;
        for (int i = 0; i < name.length(); i++) {
            subString = name.substring(i, i + 1);

            Pattern p = Pattern.compile("[0-9]*");
            Matcher m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是数字", Toast.LENGTH_SHORT).show();
                return subString;
            }

            p = Pattern.compile("[a-zA-Z]");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                return subString;
            }

            p = Pattern.compile("[\u4e00-\u9fa5]");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是汉字", Toast.LENGTH_SHORT).show();
                return subString;
            }
        }

        return "A";
    }

    //获取用户的头像链接
    public static String getUserHeadPic(String userId){
//        return "http://api."+ Constant.Web.WEB_SUFFIX+"v2/api.iyuba?protocol=10005&uid=" + userId + "&size=big";
        return Constant.Url.getMiddleUserImageUrl(UserInfoManager.getInstance().getUserId(), String.valueOf(TimeUtil.getTimeStamp()));
    }


    /*****************************辅助功能********************************/
    //根据书籍类型返回书籍类型名称
    public static String transBookTypeToStr(String bookType){
        switch (bookType){
            case TypeLibrary.BookType.bookworm:
                return "牛津书虫英语";
            case TypeLibrary.BookType.newCamstory:
                return "剑桥英语小说馆";
            case TypeLibrary.BookType.newCamstoryColor:
                return "剑桥英语小说馆彩绘";
            case TypeLibrary.BookType.conceptFourUS:
                return "新概念英语全四册(美音)";
            case TypeLibrary.BookType.conceptFourUK:
                return "新概念英语全四册(英音)";
            case TypeLibrary.BookType.conceptJunior:
                return "新概念英语青少版";
            case TypeLibrary.BookType.conceptFour:
                return "新概念英语全四册";
            case TypeLibrary.BookType.junior_primary:
                return "小学英语";
            case TypeLibrary.BookType.junior_middle:
                return "初中英语";
        }
        return "专业书籍";
    }

    //根据书籍类型显示topic
    public static String getTopic(String bookType){
        if (TextUtils.isEmpty(bookType)){
            return "";
        }

        switch (bookType){
            //新概念系列
            case TypeLibrary.BookType.conceptFourUS:
            case TypeLibrary.BookType.conceptFourUK:
            case TypeLibrary.BookType.conceptJunior:
                return "concept";
            //小学英语
            case TypeLibrary.BookType.junior_primary:
                return "primaryenglish";
            //初中英语
            case TypeLibrary.BookType.junior_middle:
                return "juniorenglish";
            //书虫
            case TypeLibrary.BookType.bookworm:
                return "bookworm";
            //剑桥小说馆
            case TypeLibrary.BookType.newCamstory:
                return "newCamstory";
            //剑桥小说馆彩绘
            case TypeLibrary.BookType.newCamstoryColor:
                return "newCamstoryColor";
        }
        return bookType;
    }

    //根据书籍类型显示appId
    public static int getAppId(String bookType){
        if (TextUtils.isEmpty(bookType)){
            return 0;
        }

        switch (bookType){
            //新概念系列
            case TypeLibrary.BookType.conceptFourUS:
            case TypeLibrary.BookType.conceptFourUK:
            case TypeLibrary.BookType.conceptJunior:
                return 222;
            //小学英语
            case TypeLibrary.BookType.junior_primary:
                return 260;
            //初中英语
            case TypeLibrary.BookType.junior_middle:
                return 259;
            //书虫
            case TypeLibrary.BookType.bookworm:
                return 285;
            case TypeLibrary.BookType.newCamstory://剑桥小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥小说馆彩绘
                return 227;
        }
        return 0;
    }

    /*******************************筛选功能********************************/
    //将数据中的标点转换为标准格式
    public static String transToStandardText(String showText){
        if (TextUtils.isEmpty(showText)){
            return showText;
        }

        showText = showText.replace('“','"');
        showText = showText.replace('”','"');
        showText = showText.replace('’','\'');
//        showText = showText.replace("。",".");
//        showText = showText.replace(", ",",");
        return showText;
    }
}