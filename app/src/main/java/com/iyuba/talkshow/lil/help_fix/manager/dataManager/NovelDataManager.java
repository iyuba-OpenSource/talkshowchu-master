package com.iyuba.talkshow.lil.help_fix.manager.dataManager;

import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.model.local.RoomDB;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterDetailEntity_novel;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterEntity_novel;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.EvalEntity_chapter;
import com.iyuba.talkshow.lil.help_fix.model.remote.RemoteManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_bookInfo_texts;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_data;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_novelChapter;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Collect_chapter;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Eval_result;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Marge_eval;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Novel_book;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Novel_chapter;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Novel_chapter_collect;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Novel_chapter_detail;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Pdf_url;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Publish_eval;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Study_report;
import com.iyuba.talkshow.lil.help_fix.model.remote.newService.NovelService;
import com.iyuba.talkshow.lil.help_fix.util.FixUtil;
import com.iyuba.talkshow.lil.help_fix.util.SignUtil;
import com.iyuba.talkshow.lil.help_mvp.util.BigDecimalUtil;
import com.iyuba.talkshow.lil.help_mvp.util.DateUtil;
import com.iyuba.talkshow.lil.help_mvp.util.EncodeUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @title: 数据操作-小说
 * @date: 2023/7/4 16:24
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NovelDataManager {

    /****************书籍*********************/
    //小说-书籍数据
    public static Observable<BaseBean_data<List<Novel_book>>> getBookData(int level, String from){
        String types = "home";

        NovelService novelService = RemoteManager.getInstance().createJson(NovelService.class);
        return novelService.getBookData(types,level,from);
    }

    /****************章节*********************/
    //小说-章节数据
    public static Observable<BaseBean_novelChapter<Novel_book,List<Novel_chapter>>> getChapterData(String level, String orderNumber, String from){
        String types = "book";

        NovelService novelService = RemoteManager.getInstance().createJson(NovelService.class);
        return novelService.getChapterData(types,level,orderNumber,from);
    }

    //数据库-查询小说的本书籍章节数据
    public static List<ChapterEntity_novel> searchMultiChapterFromDB(String types, String level, String bookId){
        return RoomDB.getInstance().getChapterNovelDao().searchChapterDataByBookId(types, level, bookId);
    }

    //数据库-保存小说的本书籍章节数据
    public static void saveChapterToDB(List<ChapterEntity_novel> list){
        RoomDB.getInstance().getChapterNovelDao().saveMultiData(list);
    }

    //数据库-查询小说的本章节数据
    public static ChapterEntity_novel searchSingleChapterFromDB(String types,String voaId){
        return RoomDB.getInstance().getChapterNovelDao().searchSingleChapterDataByBookId(types, voaId);
    }

    /******************章节详情****************/
    //小说-章节详情数据
    public static Observable<BaseBean_bookInfo_texts<Novel_book,List<Novel_chapter_detail>>> getChapterDetailData(String from, String voaId){
        String types = "detail";

        NovelService novelService = RemoteManager.getInstance().createJson(NovelService.class);
        return novelService.getNovelChapterDetailData(types,from,voaId);
    }

    //数据库-查询小说的本课程的章节详情数据
    public static List<ChapterDetailEntity_novel> searchMultiChapterDetailFromDB(String types, String voaId){
        return RoomDB.getInstance().getChapterDetailNovelDao().searchMultiDataByVoaId(types, voaId);
    }

    //数据库-保存小说的本课程的章节详情数据
    public static void saveChapterDetailToDB(List<ChapterDetailEntity_novel> list){
        RoomDB.getInstance().getChapterDetailNovelDao().saveMultiData(list);
    }

    /*******************pdf******************/
    //获取原文的pdf下载链接
    public static Observable<Pdf_url> getLessonPdfUrl(String types, String voaId, String downType){
        String language = "en";
        if (downType.equals(TypeLibrary.PdfFileType.ALL)){
            language = "cn";
        }

        String topic = FixUtil.getTopic(types);

        NovelService novelService = RemoteManager.getInstance().createJson(NovelService.class);
        return novelService.getNovelPdfDownloadUrl(topic,voaId,language);
    }

    /*******************评测*****************/
    //接口-小说的课程详情评测
    public static Observable<BaseBean_data<Eval_result>> submitLessonSingleEval(String bookType, String voaId, String paraId, String indexId, String sentence, String filePath){
        String flg = "0";
        int appId = FixUtil.getAppId(bookType);
        String wordId = "0";
        String type = FixUtil.getTopic(bookType);
        sentence = EncodeUtil.encode(sentence).replaceAll("\\+","%20");

        File file = new File(filePath);
        RequestBody fileBody = MultipartBody.create(MediaType.parse("application/octet-stream"),file);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(StrLibrary.type, type)
                .addFormDataPart(StrLibrary.flg,flg)

                .addFormDataPart(StrLibrary.wordId,wordId)
                .addFormDataPart(StrLibrary.userId,String.valueOf(UserInfoManager.getInstance().getUserId()))
                .addFormDataPart(StrLibrary.appId, String.valueOf(appId))

                .addFormDataPart(StrLibrary.newsId,voaId)
                .addFormDataPart(StrLibrary.paraId,paraId)
                .addFormDataPart(StrLibrary.IdIndex,indexId)

                .addFormDataPart(StrLibrary.sentence,sentence)
                .addFormDataPart(StrLibrary.file,file.getName(),fileBody)
                .build();

        NovelService apiService = RemoteManager.getInstance().createJson(NovelService.class);
        return apiService.submitLessonSingleEval(multipartBody);
    }

    //接口-小说的课程评测发布接口
    public static Observable<Publish_eval> publishSingleEval(String bookType, String voaId, String paraId, String idIndex, int score, String content){
        //http://voa.iyuba.cn/voa/UnicomApi?topic=bookworm&platform=android&protocol=60002&format=json&userid=14243581&voaid=10101&username=iyuppoojhg&shuoshuotype=2&paraid=2&idIndex=1&score=33&content=wav8%2F202307%2Fbookworm%2F20230705%2F16885207937304418.mp3

        String topic = FixUtil.getTopic(bookType);
        String platform = "android";
        String format = "json";
        int protocol = 60002;
        int shuoshuotype = 2;

        NovelService novelService = RemoteManager.getInstance().createJson(NovelService.class);
        return novelService.publishSingleEval(topic,platform,protocol,format,UserInfoManager.getInstance().getUserId(), voaId,UserInfoManager.getInstance().getUserName(), shuoshuotype,paraId,idIndex,score,content);
    }

    //接口-小说的课程评测合并
    public static Observable<Marge_eval> margeAudioEval(String bookType, String voaId){
        String type = FixUtil.getTopic(bookType);

        List<EvalEntity_chapter> evalList = CommonDataManager.getEvalChapterByVoaIdFromDB(bookType,voaId);

        StringBuilder audioBuilder = new StringBuilder();
        for (int i = 0; i < evalList.size(); i++) {
            audioBuilder.append(evalList.get(i).url);

            if (i!=evalList.size()-1){
                audioBuilder.append(",");
            }
        }

        String audios = audioBuilder.toString();

        NovelService novelService = RemoteManager.getInstance().createJson(NovelService.class);
        return novelService.margeAudioEval(audios,type);
    }

    //接口-小说的课程合并后的音频发布
    public static Observable<Publish_eval> publishMargeEval(String bookType,String voaId, String content){
        String topic  = FixUtil.getTopic(bookType);
        String platform = "android";
        String format = "json";
        int protocol = 60003;
        int shuoshuotype = 4;
        int appId = App.APP_ID;

        //设置奖励机制
        int rewardVersion = 1;

        //合并数据，获取平均分
        List<EvalEntity_chapter> evalList = CommonDataManager.getEvalChapterByVoaIdFromDB(bookType,voaId);
        double score = 0;
        if (evalList!=null&&evalList.size()>0){
            for (int i = 0; i < evalList.size(); i++) {
                double totalScore = evalList.get(i).total_score*20;
                score+=totalScore;
            }
        }

        int avgScore = (int) (BigDecimalUtil.trans2Double(score) /evalList.size());

        NovelService novelService = RemoteManager.getInstance().createJson(NovelService.class);
        return novelService.publishMargeAudio(topic,platform,protocol,format,UserInfoManager.getInstance().getUserId(), voaId,shuoshuotype,avgScore,content,appId,rewardVersion);
    }

    /*******************************************学习报告**********************************/
    //接口-小说提交学习报告(听力)
    //endFlag:播放完成为1，中断或者暂停为0
    public static Observable<Study_report> uploadListenStudyReportData(String types, String voaId, int uid, long startTime, long endTime, int wordCount, int endFlag) {
        String format = "json";
        String platform = "android";

//        GetDeviceInfo deviceInfo = new GetDeviceInfo(ResUtil.getInstance().getContext());
//        String device = deviceInfo.getLocalDeviceType();
//        String deviceId = deviceInfo.getLocalMACAddress();

        //去掉获取mac地址的操作
        String device = "";
        String deviceId = "";

        String startDate = DateUtil.toDateStr(startTime, DateUtil.YMDHMS);
        String endDate = DateUtil.toDateStr(endTime, DateUtil.YMDHMS);
        int testMode = 1;
        int testNum = 1;
        String sign = SignUtil.getJuniorListenStudyReportSign(uid, startDate);
        int score = 0;
        String userAnswer = "";

        //增加奖励机制
        int rewardVersion=1;

        int appId = FixUtil.getAppId(types);
        String lesson = FixUtil.getTopic(types);

        NovelService novelService = RemoteManager.getInstance().createJson(NovelService.class);
        return novelService.updateReadReport(format,lesson,platform,appId,startDate,endDate,endFlag,
                voaId,testNum,wordCount,testMode,userAnswer,score,deviceId,uid,sign,rewardVersion);
    }

    /**************************************收藏*********************************/
    //接口-收藏/取消收藏文章
    public static Observable<Collect_chapter> collectArticle(String types, String userId, String voaId, boolean isCollect){
        String groupName = "Iyuba";
        String sentenceFlag = "0";

        int appId = FixUtil.getAppId(types);
        String topic = FixUtil.getTopic(types);
        String type = "del";
        if (isCollect){
            type = "insert";
        }

        NovelService commonService = RemoteManager.getInstance().createXml(NovelService.class);
        return commonService.collectArticle(groupName,sentenceFlag,appId,userId,topic,voaId,type);
    }

    //接口-获取收藏的文章数据
    public static Observable<BaseBean_data<List<Novel_chapter_collect>>> getArticleCollect(String types, int userId){
        int appId = FixUtil.getAppId(types);
        String topic = FixUtil.getTopic(types);
        int flag = 0;
        String sign = SignUtil.getJuniorArticleCollectSign(topic,userId,appId);
        String format = "json";

        NovelService commonService = RemoteManager.getInstance().createJson(NovelService.class);
        return commonService.getArticleCollect(userId,sign,topic,appId,format,flag);
    }
}
