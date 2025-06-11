package com.iyuba.talkshow.lil.help_fix.util;

import com.iyuba.talkshow.lil.help_mvp.util.DateUtil;
import com.iyuba.talkshow.lil.help_mvp.util.EncodeUtil;

/**
 * @desction: 签名工具
 * @date: 2023/4/20 09:29
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class SignUtil {

    //登录-账号
    public static String getLoginAccountSign(int protocol,String userName,String password){
        String signStr = protocol+userName+ EncodeUtil.md5(password)+"iyubaV2";
        return EncodeUtil.md5(signStr);
    }

    //用户信息
    public static String userInfoSign(int protocol,long searchUid){
        String signStr = protocol+""+searchUid+"iyubaV2";
        return EncodeUtil.md5(signStr);
    }

    //用户信息-注销账号
    public static String loginOutSign(int protocol,String name,String password){
        String signStr = protocol+name+EncodeUtil.md5(password)+"iyubaV2";
        return EncodeUtil.md5(signStr);
    }

    //用户信息-手机号注册
    public static String phoneRegisterSign(int protocol,String name,String password){
        String signStr = protocol+name+EncodeUtil.md5(password)+"iyubaV2";
        return EncodeUtil.md5(signStr);
    }

    //评测-排行榜
    public static String getEvalRankSign(String types,long uid,String voaId,int start,int total){
        String time = DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMD);
        String topic = FixUtil.getTopic(types);
        String topicId = voaId;

        String signStr = uid+topic+topicId+start+total+time;
        return EncodeUtil.md5(signStr);
    }

    //评测-排行榜详情
    public static String getEvalRankDetailSign(String uid){
        String time = DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMD);
        String signStr = uid+"getWorksByUserId"+time;
        return EncodeUtil.md5(signStr);
    }

    //支付-支付宝支付
    public static String getAliPaySign(long uid){
        String time = DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMD);
        String signStr = uid+"iyuba"+time;
        return EncodeUtil.md5(signStr);
    }

    //支付-微信支付
    public static String getWxPaySign(long uid,int appId,String price,String amount){
        String date = DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMD2);
        String signStr = appId +String.valueOf(uid)+price+amount+date;
        return EncodeUtil.md5(signStr);
    }

    /************新概念************/
    //新概念-青少版的书籍签名
    public static String getConceptJuniorBookSign(){
        //这里获取东八区的时间
        long curTime = System.currentTimeMillis()+8*60*60*1000;
        //转换成day显示
        long dayInt = curTime/(24*60*60*1000);

        String sign = "iyuba"+dayInt+"series";
        return EncodeUtil.md5(sign);
    }

    //新概念-青少版的章节签名
    public static String getConceptJuniorChapterSign(){
        //这里获取东八区的时间
        long curTime = System.currentTimeMillis()+8*60*60*1000;
        //转换成day显示
        long dayInt = curTime/(24*60*60*1000);

        String sign = "iyuba"+dayInt+"series";
        return EncodeUtil.md5(sign);
    }

    /***********中小学***************/
    //中小学-章节数据
    public static String getJuniorChapterSign(){
        long time = System.currentTimeMillis()/1000;
        time = time/60/60/24;

        String signStr = "iyuba"+String.valueOf(time)+"series";
        return EncodeUtil.md5(signStr);
    }

    //中小学-书籍数据
    public static String getJuniorBookSign(){
        long time = System.currentTimeMillis()/1000;
        time = time/60/60/24;

        String signStr = "iyuba"+String.valueOf(time)+"series";
        return EncodeUtil.md5(signStr);
    }

    //中小学-文章收藏信息
    public static String getJuniorArticleCollectSign(String topic,int userId,int appId){
        //这里获取东八区的时间
        long curTime = System.currentTimeMillis()+8*60*60*1000;
        //转换成day显示
        long dayInt = curTime/(24*60*60*1000);

        String signStr = "iyuba"+userId+topic+appId+dayInt;
        return EncodeUtil.md5(signStr);
    }

    //中小学-听力学习记录提交
    public static String getJuniorListenStudyReportSign(int uid,String startDate){
        String date = DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMD);
        String sign = uid+startDate+date;
        return EncodeUtil.md5(sign);
    }

    //中小学-单词学习记录提交
    public static String getJuniorWordStudyReportSign(int uid,int appId,String bookId){
        String date = DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMD);
        String sign = String.valueOf(uid)+String.valueOf(appId)+bookId+"iyubaExam"+date;
        return EncodeUtil.md5(sign);
    }

    //奖励的历史记录
    public static String getRewardHistorySign(int uid){
        String data = uid+"iyuba"+DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMD);
        return EncodeUtil.md5(data);
    }

    //广告的点击奖励签名
    public static String getAdClickSign(int uid,int appId,long timestamp){
        String data = String.valueOf(uid)+String.valueOf(appId)+"iyubaV2"+String.valueOf(timestamp);
        return EncodeUtil.md5(data);
    }

    //激励广告的会员获取签名
    public static String getRewardAdVipSign(long time,int userId,int appId){
        String data = String.valueOf(userId)+String.valueOf(appId)+"iyubaV2"+String.valueOf(time);
        return EncodeUtil.md5(data);
    }
}
