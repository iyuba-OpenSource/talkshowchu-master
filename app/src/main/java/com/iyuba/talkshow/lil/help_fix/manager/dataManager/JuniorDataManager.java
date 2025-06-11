package com.iyuba.talkshow.lil.help_fix.manager.dataManager;//package com.iyuba.talkshow.lil.fix.manager.dataManager;
//
//import com.iyuba.talkshow.constant.App;
//import com.iyuba.talkshow.lil.fix.model.remote.RemoteManager;
//import com.iyuba.talkshow.lil.fix.model.remote.base.BaseBean_data;
//import com.iyuba.talkshow.lil.fix.model.remote.base.BaseBean_data_junior;
//import com.iyuba.talkshow.lil.fix.model.remote.base.BaseBean_data_primary;
//import com.iyuba.talkshow.lil.fix.model.remote.bean.Junior_type;
//import com.iyuba.talkshow.lil.fix.model.remote.newService.JuniorService;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import io.reactivex.Observable;
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//
///**
// * @title: 数据操作-中小学
// * @date: 2023/7/4 16:22
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// * @description:
// */
//public class JuniorDataManager {
//    private static final String TAG = "JuniorDataManager";
//
//    /*******************************出版社******************************/
//    //小学-类型数据
//    public static Observable<BaseBean_data<BaseBean_data_primary<List<Junior_type>>>> getPrimaryType(){
//        int appId = App.APP_ID;
//        String uid = "0";
//        String type = "primary";
//        int version = 3;
//
//        JuniorService juniorService = RemoteManager.getInstance().createJson(JuniorService.class);
//        return juniorService.getPrimaryTypeData(appId,uid,type,version);
//    }
//
//    //初中-类型数据
//    public static Observable<BaseBean_data<BaseBean_data_junior<List<Junior_type>>>> getMiddleType(){
//        int appId = App.APP_ID;
//        String uid = "0";
//        String type = "junior";
//        int version = 3;
//
//        JuniorService juniorService = RemoteManager.getInstance().createJson(JuniorService.class);
//        return juniorService.getMiddleTypeData(appId,uid,type,version);
//    }
//
//    //数据库-保存中小学的书籍数据
//    public static void saveBookToDB(List<BookEntity_junior> list){
//        RoomDB.getInstance().getBookEntityJuniorDao().saveData(list);
//    }
//
//    //数据库-获取类型下的书籍数据
//    public static List<BookEntity_junior> getBookFromDB(String typeId){
//        return RoomDB.getInstance().getBookEntityJuniorDao().getDataByTypeId(typeId);
//    }
//
//    /*******************************书籍*********************************/
//    //中小学-书籍数据
//    public static Observable<BaseBean_data<List<Junior_book>>> getJuniorBook(String typeId){
//        String type = "category";
//        String uid = "0";
//        int appId = Constant.APP_ID;
//        String format = "json";
//        String sign = SignUtil.getJuniorBookSign();
//
//        JuniorService juniorService = RemoteManager.getInstance().createJson(JuniorService.class);
//        return juniorService.getJuniorBookData(type,typeId,uid,appId,sign,format);
//    }
//
//    /*******************************章节************************************/
//    //小学-章节数据
//    public static Observable<BaseBean_data<List<Junior_chapter>>> getPrimaryChapter(String bookId){
//        int version = 1;
//        String type = "title";
//        int appId = Constant.APP_ID;
//        String format = "json";
//        String sign = SignUtil.getJuniorChapterSign();
//        String uid = "0";
//
//        JuniorService juniorService = RemoteManager.getInstance().createJson(JuniorService.class);
//        return juniorService.getPrimaryChapterData(type,bookId,uid,appId,sign,format,version);
//    }
//
//    //初中-章节数据
//    public static Observable<BaseBean_data<List<Junior_chapter>>> getMiddleChapter(String bookId){
//        String type = "title";
//        int appId = Constant.APP_ID;
//        String format = "json";
//        String sign = SignUtil.getJuniorChapterSign();
//        String uid = "0";
//
//        JuniorService juniorService = RemoteManager.getInstance().createJson(JuniorService.class);
//        return juniorService.getMiddleChapterData(type,bookId,uid,appId,sign,format);
//    }
//
//    //数据库-保存中小学的章节数据
//    public static void saveChapterToDB(List<ChapterEntity_junior> list){
//        RoomDB.getInstance().getChapterEntityJuniorDao().saveData(list);
//    }
//
//    //数据库-获取书籍下的章节数据
//    public static List<ChapterEntity_junior> getMultiChapterFromDB(String bookId){
//        return RoomDB.getInstance().getChapterEntityJuniorDao().getDataByBookId(bookId);
//    }
//
//    //数据库-获取单个章节的数据
//    public static ChapterEntity_junior getSingleChapterFromDB(String voaId){
//        return RoomDB.getInstance().getChapterEntityJuniorDao().getDataByVoaId(voaId);
//    }
//
//    /********************************章节详情*********************************/
//    //中小学-章节详情数据
//    public static Observable<BaseBean_voatext<List<Junior_chapter_detail>>> getJuniorChapterDetail(String voaId){
//        String format = "json";
//
//        JuniorService juniorService = RemoteManager.getInstance().createJson(JuniorService.class);
//        return juniorService.getJuniorChapterDetailData(voaId,format);
//    }
//
//    //数据库-保存章节详情数据
//    public static void saveChapterDetailToDB(List<ChapterDetailEntity_junior> list){
//        RoomDB.getInstance().getChapterDetailEntityJuniorDao().saveData(list);
//    }
//
//    //数据库-获取章节下的章节详情数据
//    public static List<ChapterDetailEntity_junior> getMultiChapterDetailFromDB(String voaId){
//        return RoomDB.getInstance().getChapterDetailEntityJuniorDao().getDataByVoaId(voaId);
//    }
//
//    /**********************************单词****************************/
//    //中小学-单词数据
//    public static Observable<BaseBean_data<List<Junior_word>>> getJuniorWordData(String bookId){
//        JuniorService juniorService = RemoteManager.getInstance().createJson(JuniorService.class);
//        return juniorService.getJuniorWordData(bookId);
//    }
//
//    //数据库-查询中小学本书籍下分组的单词数据
//    public static List<WordProgressBean> searchWordByBookIdGroup(String bookId){
//        return RoomDB.getInstance().getWordJuniorDao().searchWordByBookIdGroup(bookId);
//    }
//
//    //数据库-查询中小学本书籍下本单元的单词数据
//    public static List<WordEntity_junior> searchWordByUnitIdFromDB(String bookId, String unitId){
//        return RoomDB.getInstance().getWordJuniorDao().searchWordByUnitId(bookId,unitId);
//    }
//
//    //数据库-保存中小学的单词数据
//    public static void saveWordToDB(List<WordEntity_junior> list){
//        RoomDB.getInstance().getWordJuniorDao().saveData(list);
//    }
//
//    /**********************************pdf******************************/
//    //获取原文pdf下载链接-(双语-0，英文-1)
//    public static Observable<Pdf_url> getLessonPdfUrl(String bookType,String downloadType,String voaId){
//        String topic = FixUtil.getTopic(bookType);
//        String type = "0";
//        if (downloadType.equals(TypeLibrary.PdfFileType.ALL)){
//            type = "0";
//        }else if (downloadType.equals(TypeLibrary.PdfFileType.EN)){
//            type = "1";
//        }
//
//        JuniorService pdfService = RemoteManager.getInstance().createJson(JuniorService.class);
//        return pdfService.getJuniorPdfDownloadUrl(topic,voaId,type);
//    }
//
//    /*************************************评测***************************/
//    //接口-课程详情评测
//    public static Observable<BaseBean_data<Eval_result>> submitLessonSingleEval(String bookType, boolean isSentence, String voaId, String paraId, String indexId, String sentence, String filePath){
//        String flg = "0";
//        int appId = 260;
//        if (bookType.equals(TypeLibrary.BookType.junior_middle)){
//            appId = 259;
//        }
//        int uid = AccountManager.getInstance().getUserId();
//        String wordId = "0";
//        String type = FixUtil.getTopic(bookType);
//        sentence = EncodeUtil.encode(sentence).replaceAll("\\+","%20");
//        int protocol = 60003;
//        String platform = "android";
//
//        File file = new File(filePath);
//        RequestBody fileBody = MultipartBody.create(MediaType.parse("application/octet-stream"),file);
//        MultipartBody multipartBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart(StrLibrary.type, type)
//                .addFormDataPart(StrLibrary.flg,flg)
//
//                .addFormDataPart(StrLibrary.wordId,wordId)
//                .addFormDataPart(StrLibrary.userId,String.valueOf(uid))
//                .addFormDataPart(StrLibrary.appId, String.valueOf(appId))
//
//                .addFormDataPart(StrLibrary.newsId,voaId)
//                .addFormDataPart(StrLibrary.paraId,paraId)
//                .addFormDataPart(StrLibrary.IdIndex,indexId)
//
//                .addFormDataPart(StrLibrary.sentence,sentence)
//                .addFormDataPart(StrLibrary.protocol,String.valueOf(protocol))
//                .addFormDataPart(StrLibrary.platform,platform)
//                .addFormDataPart(StrLibrary.file,file.getName(),fileBody)
//                .build();
//
//
//        JuniorService apiService = RemoteManager.getInstance().createJson(JuniorService.class);
//        return apiService.submitLessonSingleEval(multipartBody);
//    }
//
//    //接口-发布单句评测
//    public static Observable<Publish_eval> publishSingleEval(String bookType, String voaId, String paraId, String idIndex, int score, String content){
//        String topic = FixUtil.getTopic(bookType);
//        String platform = "android";
//        String format = "json";
//        int protocol = 60003;
//        long uid = AccountManager.getInstance().getUserId();
//        String userName = AccountManager.getInstance().userName;
//        int shuoshuotype = 2;
//
//        JuniorService apiService = RemoteManager.getInstance().createJson(JuniorService.class);
//        return apiService.publishSingleEval(topic,voaId,paraId,idIndex,platform,format,protocol,uid,userName,voaId,score,shuoshuotype,content);
//    }
//
//    //接口-合并评测的句子
//    public static Observable<Marge_eval> margeAudioEval(String bookType, String voaId){
//        String type = FixUtil.getTopic(bookType);
//
//        List<EvalEntity_chapter> evalList = CommonDataManager.getEvalChapterByVoaIdFromDB(bookType,voaId);
//
//        StringBuilder audioBuilder = new StringBuilder();
//        for (int i = 0; i < evalList.size(); i++) {
//            audioBuilder.append(evalList.get(i).url);
//
//            if (i!=evalList.size()-1){
//                audioBuilder.append(",");
//            }
//        }
//
//        String audios = audioBuilder.toString();
//
//        MultipartBody body = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart(StrLibrary.type,type)
//                .addFormDataPart(StrLibrary.audios,audios)
//                .build();
//
//        JuniorService apiService = RemoteManager.getInstance().createJson(JuniorService.class);
//        return apiService.margeAudioEval(body);
//    }
//
//    //接口-发布合成评测的数据
//    public static Observable<Publish_eval> publishMargeEval(String bookType,String voaId, String content){
//        String topic  = FixUtil.getTopic(bookType);
//        String platform = "android";
//        String format = "json";
//        int protocol = 60003;
//        long uid = Long.parseLong(AccountManager.getInstance().userId);
//        String userName = AccountManager.getInstance().userName;
//        int shuoshuotype = 4;
//
//        //合并数据，获取平均分
//        List<EvalEntity_chapter> evalList = CommonDataManager.getEvalChapterByVoaIdFromDB(bookType,voaId);
//        double score = 0;
//        if (evalList!=null&&evalList.size()>0){
//            for (int i = 0; i < evalList.size(); i++) {
//                double totalScore = evalList.get(i).total_score*20;
//                score+=totalScore;
//            }
//        }
//
//        int avgScore = (int) (BigDecimalUtil.trans2Double(score) /evalList.size());
//
//        JuniorService apiService = RemoteManager.getInstance().createJson(JuniorService.class);
//        return apiService.publishMargeAudio(topic,platform,format,protocol,uid,userName,voaId,avgScore,shuoshuotype,content);
//    }
//
//    //接口-单词评测
//    public static Observable<BaseBean_data<Junior_eval>> submitWordEval(String types, boolean isSentence, String sentence, String wordPosition, String bookId, long userId, String filePath){
//        String newsId = "0";
//        String wordId = "0";
//        int appId = Constant.APP_ID;
//
//        String evalType = FixUtil.getTopic(types);
//        String flg = "0";
//        if (!isSentence){
//            flg = "2";
//        }
//
//        //文件包装
//        File file = new File(filePath);
//        RequestBody fileBody = MultipartBody.create(MediaType.parse("application/octet-stream"),file);
//
//        //参数
//        MultipartBody multipartBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart(StrLibrary.sentence,sentence)
//                .addFormDataPart(StrLibrary.flg,flg)
//                .addFormDataPart(StrLibrary.paraId,bookId)
//                .addFormDataPart(StrLibrary.newsId,newsId)
//                .addFormDataPart(StrLibrary.IdIndex,String.valueOf(wordPosition))
//                .addFormDataPart(StrLibrary.wordId,wordId)
//                .addFormDataPart(StrLibrary.appId,String.valueOf(appId))
//                .addFormDataPart(StrLibrary.type,evalType)
//                .addFormDataPart(StrLibrary.userId,String.valueOf(userId))
//                .addFormDataPart(StrLibrary.file,file.getName(),fileBody)
//                .build();
//
//        JuniorService commonService = RemoteManager.getInstance().createJson(JuniorService.class);
//        return commonService.submitWordEval(multipartBody);
//    }
//
//    /********************************************配音***************************************/
//    //数据-发布配音预览
//    public static Observable<Publish_preview> publishTalkPreview(DubbingPreviewSubmitBean submitBean){
//        int protocol = 60002;
//        int userId = AccountManager.getInstance().getUserId();
//        String content = "3";
//
//        //转换成数据
//        String json = GsonUtil.toJson(submitBean);
//        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),json);
//
//        JuniorService commonService = RemoteManager.getInstance().createJson(JuniorService.class);
//        return commonService.publishTalkPreview(protocol,userId,content,body);
//    }
//
//    /********************************************配音排行************************************/
//    //接口-获取口语秀排行内容
//    public static Observable<BaseBean_dubbing_rank<List<Dubbing_rank>>> getDubbingRank(String types, String voaId, int index, int count){
//        String format = "json";
//        String platform = "android";
//        int protocol = 60001;
//        String topic = FixUtil.getTopic(types);
//        int selectType = 3;
//        int sort = 1;
//
//
//        JuniorService commonService = RemoteManager.getInstance().createJson(JuniorService.class);
//        return commonService.dubbingRank(voaId,protocol,index,count,format,topic,selectType,sort,platform);
//    }
//
//    //接口-点赞评测排行的详情内容数据
//    public static Observable<Eval_rank_agree> agreeDubbingRankDetailData(String sentenceId){
//        int protocol = 61001;
//
//        JuniorService apiService = RemoteManager.getInstance().createJson(JuniorService.class);
//        return apiService.agreeDubbingRankDetail(protocol,sentenceId);
//    }
//
//    /************************************************学习报告*********************************/
//    //接口-中小学提交学习报告(听力)
//    //endFlag:播放完成为1，中断或者暂停为0
//    public static Observable<Study_report> uploadListenStudyReportData(String types, String voaId, int uid, long startTime, long endTime, int wordCount, int endFlag){
//        String format = "json";
//        String platform = "android";
//        GetDeviceInfo deviceInfo = new GetDeviceInfo(ConceptApplication.getContext());
//        String device = deviceInfo.getLocalDeviceType();
//        String deviceId = deviceInfo.getLocalMACAddress();
//        String startDate = DateUtil.toDateStr(startTime,DateUtil.YMDHMS);
//        String endDate = DateUtil.toDateStr(endTime,DateUtil.YMDHMS);
//        int testMode = 1;
//        int testNum = 1;
//        String sign = SignUtil.getJuniorListenStudyReportSign(uid,startDate);
//
//        int appId = 260;
//        String appName = "小学英语";
//        String lesson = "primaryEnglish";
//        if (types.equals(TypeLibrary.BookType.junior_middle)){
//            appId = 259;
//            appName = "初中英语";
//            lesson = "juniorEnglish";
//        }
//
//        JuniorService service = RemoteManager.getInstance().createJson(JuniorService.class);
//        return service.updateReadReport(format,appId,appName,lesson,voaId,uid,device,deviceId,startDate,endDate,endFlag,wordCount,testMode,platform,testNum,sign);
//    }
//
//    //接口-中小学提交学习报告(单词)
//    public static Observable<Study_report> uploadWordStudyReportData(String bookType, String bookId, List<WordStudyReportSubmitBean.TestListBean> testList){
//        String format = "json";
//        int mode = 2;
//        int appId = 260;
//        if (bookType.equals(TypeLibrary.BookType.junior_middle)){
//            appId = 259;
//        }
//        int uid = AccountManager.getInstance().getUserId();
//
//        GetDeviceInfo deviceInfo = new GetDeviceInfo(ConceptApplication.getContext());
//        String deviceId = deviceInfo.getLocalMACAddress();
//        String sign = SignUtil.getJuniorWordStudyReportSign(uid,appId,bookId);
//        List<WordStudyReportSubmitBean.Score> scoreList = new ArrayList<>();
//
//        WordStudyReportSubmitBean submitBean = new WordStudyReportSubmitBean(
//                deviceId,appId,format,bookId,mode,sign,String.valueOf(uid),scoreList,testList
//        );
//
//        //转换成数据
//        String json = GsonUtil.toJson(submitBean);
//        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),json);
//
//        JuniorService service = RemoteManager.getInstance().createJson(JuniorService.class);
//        return service.updateWordReport(body);
//    }
//
//
//    /************************************收藏**************************/
//    //接口-收藏/取消收藏文章
//    public static Observable<Collect_chapter> collectArticle(String types, String userId, String voaId, boolean isCollect){
//        String groupName = "Iyuba";
//        String sentenceFlag = "0";
//
//        int appId = FixUtil.getAppId(types);
//        String topic = FixUtil.getTopic(types);
//        String sentenceId = "0";
//        String type = "del";
//        if (isCollect){
//            type = "insert";
//        }
//
//        JuniorService commonService = RemoteManager.getInstance().createXml(JuniorService.class);
//        return commonService.collectArticle(groupName,sentenceFlag,appId,userId,topic,voaId,sentenceId,type);
//    }
//
//    //接口-获取收藏的文章数据
//    public static Observable<BaseBean_data<List<Junior_chapter_collect>>> getArticleCollect(String types, int userId){
//        int appId = FixUtil.getAppId(types);
//        String topic = FixUtil.getTopic(types);
//        int flag = 0;
//        String sign = SignUtil.getJuniorArticleCollectSign(topic,userId,appId);
//
//        JuniorService commonService = RemoteManager.getInstance().createJson(JuniorService.class);
//        return commonService.getArticleCollect(userId,sign,topic,appId,flag);
//    }
//}
