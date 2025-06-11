package com.iyuba.talkshow.lil.help_fix.model.remote.util;

import android.text.TextUtils;

import com.iyuba.talkshow.lil.help_fix.data.bean.EvalRankDetailBean;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.NovelDataManager;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.AgreeEntity;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterCollectEntity;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterDetailEntity_novel;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterEntity_novel;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.EvalEntity_chapter;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Eval_rank_detail;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Eval_result;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Novel_chapter;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Novel_chapter_collect;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Novel_chapter_detail;
import com.iyuba.talkshow.lil.help_mvp.util.gson.GsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 远程数据转换
 * @date: 2023/5/19 17:41
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class RemoteTransUtil {

    /**********************转换为数据库类*********************/
    /************中小学**********/
    //中小学-转换章节数据
//    public static List<ChapterEntity_junior> transJuniorChapterData(String types, List<Junior_chapter> list){
//        List<ChapterEntity_junior> temp = new ArrayList<>();
//
//        if (list!=null&&list.size()>0){
//            for (int i = 0; i < list.size(); i++) {
//                Junior_chapter chapter = list.get(i);
//                temp.add(new ChapterEntity_junior(
//                        chapter.getCreatTime(),
//                        chapter.getListenPercentage(),
//                        chapter.getCategory(),
//                        chapter.getHavePractice(),
//                        chapter.getPackageid(),
//                        chapter.getTexts(),
//                        chapter.getVideo(),
//                        chapter.getPagetitle(),
//                        chapter.getUrl(),
//                        chapter.getPrice(),
//                        chapter.getPercentage(),
//                        chapter.getPublishTime(),
//                        chapter.getHotFlg(),
//                        chapter.getCategoryid(),
//                        chapter.getClickRead(),
//                        chapter.getIntroDesc(),
//                        chapter.getKeyword(),
//                        chapter.getTotalTime(),
//                        chapter.getTitle(),
//                        chapter.getSound(),
//                        chapter.getPic(),
//                        chapter.getOwnerid(),
//                        chapter.getFlag(),
//                        chapter.getDescCn(),
//                        chapter.getClassid(),
//                        chapter.getOutlineid(),
//                        chapter.getTitle_cn(),
//                        TextUtils.isEmpty(chapter.getSeries())?0:Integer.parseInt(chapter.getSeries()),
//                        chapter.getName(),
//                        chapter.getWordNum(),
//                        chapter.getCategoryName(),
//                        TextUtils.isEmpty(chapter.getId())?0:Long.parseLong(chapter.getId()),
//                        chapter.getReadCount(),
//                        chapter.getDesc(),
//                        types
//                ));
//            }
//        }
//        return temp;
//    }
//
//    //中小学-将收藏数据转换为章节数据
//    public static List<ChapterEntity_junior> transJuniorCollectChapterData(String types,List<Junior_chapter_collect> list){
//        List<ChapterEntity_junior> temp = new ArrayList<>();
//
//        for (int i = 0; i < list.size(); i++) {
//            Junior_chapter_collect collect = list.get(i);
//            //这里先判断是否存在，存在则不处理
//            ChapterEntity_junior chapter = JuniorDataManager.getSingleChapterFromDB(collect.getVoaid());
//            if (chapter!=null){
//                continue;
//            }
//
//            int bookId = TextUtils.isEmpty(collect.getSeries())?0:Integer.parseInt(collect.getSeries());
//            long voaId = TextUtils.isEmpty(collect.getVoaid())?0:Long.parseLong(collect.getVoaid());
//
//            temp.add(new ChapterEntity_junior(
//                    collect.getCreateTime(),
//                    "0",
//                    collect.getCategory(),
//                    "",
//                    "",
//                    "",
//                    collect.getVideo(),
//                    "",
//                    "",
//                    "",
//                    "",
//                    "",
//                    collect.getHotflg(),
//                    "0",
//                    "",
//                    "",
//                    "",
//                    "",
//                    collect.getTitle(),
//                    collect.getSound(),
//                    collect.getPic(),
//                    "0",
//                    collect.getFlag(),
//                    collect.getDescCn(),
//                    "0",
//                    "1",
//                    collect.getTitle_cn(),
//                    bookId,
//                    "",
//                    0,
//                    collect.getCategoryName(),
//                    voaId,
//                    collect.getReadCount(),
//                    collect.getDescCn(),
//                    types
//            ));
//        }
//
//        return temp;
//    }
//
//    //中小学-转换书籍数据
//    public static List<BookEntity_junior> transJuniorBookData(String types, List<Junior_book> list){
//        List<BookEntity_junior> temp = new ArrayList<>();
//
//        if (list!=null&&list.size()>0){
//            for (int i = 0; i < list.size(); i++) {
//                Junior_book book = list.get(i);
//                temp.add(new BookEntity_junior(
//                        book.getCategory(),
//                        book.getCreateTime(),
//                        book.getIsVideo(),
//                        book.getPic(),
//                        book.getKeyWords(),
//                        book.getVersion(),
//                        book.getDescCn(),
//                        book.getSeriesCount(),
//                        book.getSeriesName(),
//                        book.getUpdateTime(),
//                        book.getHotFlg(),
//                        book.getHaveMicro(),
//                        book.getId(),
//                        types
//                ));
//            }
//        }
//        return temp;
//    }
//
//    //中小学-转换章节详情数据
//    public static List<ChapterDetailEntity_junior> transJuniorChapterDetailData(String types, String bookId, String voaId, List<Junior_chapter_detail> list){
//        List<ChapterDetailEntity_junior> temp = new ArrayList<>();
//
//        if (list!=null&&list.size()>0){
//            for (int i = 0; i < list.size(); i++) {
//                Junior_chapter_detail detail = list.get(i);
//
//                temp.add(new ChapterDetailEntity_junior(
//                        detail.getImgPath(),
//                        detail.getEndTiming(),
//                        TextUtils.isEmpty(detail.getParaId())?0:Integer.parseInt(detail.getParaId()),
//                        TextUtils.isEmpty(detail.getIdIndex())?0:Integer.parseInt(detail.getIdIndex()),
//                        detail.getSentence_cn(),
//                        detail.getImgWords(),
//                        detail.getStart_x(),
//                        detail.getEnd_y(),
//                        detail.getTiming(),
//                        detail.getEnd_x(),
//                        detail.getSentence(),
//                        detail.getStart_y(),
//                        types,
//                        bookId,voaId
//                ));
//            }
//        }
//        return temp;
//    }
//
//    //中小学-转换收藏数据
//    public static List<ChapterCollectEntity> transJuniorChapterCollectData(String types, int userId,List<Junior_chapter_collect> list){
//        List<ChapterCollectEntity> temp = new ArrayList<>();
//        if (list!=null&&list.size()>0){
//            for (int i = 0; i < list.size(); i++) {
//                Junior_chapter_collect collect = list.get(i);
//                temp.add(new ChapterCollectEntity(
//                        types,
//                        collect.getVoaid(),
//                        String.valueOf(userId),
//                        collect.getSeries(),
//                        collect.getPic(),
//                        collect.getTitle_cn(),
//                        collect.getTitle()
//                ));
//            }
//        }
//        return temp;
//    }

    /*************小说*************/
    //小说-转化书籍数据
    /*public static List<BookEntity_novel> transNovelBookData(List<Novel_book> list){
        List<BookEntity_novel> temp = new ArrayList<>();

        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                temp.add(transNovelBookSingleData(list.get(i)));
            }
        }
        return temp;
    }

    public static BookEntity_novel transNovelBookSingleData(Novel_book book){
        if (book==null){
            return null;
        }

        return new BookEntity_novel(
                book.getTypes(),
                book.getOrderNumber(),
                book.getLevel(),
                book.getBookname_en(),
                book.getAuthor(),
                book.getAbout_book(),
                book.getBookname_cn(),
                book.getAbout_interpreter(),
                book.getWordcounts(),
                book.getInterpreter(),
                book.getPic(),
                book.getAbout_author()
        );
    }*/

    /**小说**/
    //将章节数据-小说转换为数据库数据
    public static List<ChapterEntity_novel> transNovelChapterToDB(String types, List<Novel_chapter> list){
        List<ChapterEntity_novel> temp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Novel_chapter novel = list.get(i);

            temp.add(new ChapterEntity_novel(
                    novel.getVoaid(),
                    novel.getOrderNumber(),
                    novel.getLevel(),
                    novel.getChapterOrder(),
                    novel.getSound(),
                    novel.getShow(),
                    TextUtils.isEmpty(novel.getCname_cn())?"":novel.getCname_cn().trim(),
                    TextUtils.isEmpty(novel.getCname_en())?"":novel.getCname_en().trim(),
                    types
            ));
        }

        return temp;
    }

    //将收藏数据-小说转换为数据库数据
    public static List<ChapterEntity_novel> transNovelCollectChapterToDB(String types,List<Novel_chapter_collect> list){
        List<ChapterEntity_novel> temp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Novel_chapter_collect collect = list.get(i);
            //这里先判断数据是否存在，存在则不进行处理
            ChapterEntity_novel chapter = NovelDataManager.searchSingleChapterFromDB(types, collect.getVoaid());
            if (chapter!=null){
                continue;
            }

            int show = TextUtils.isEmpty(collect.getShow())?0:Integer.parseInt(collect.getShow());

            temp.add(new ChapterEntity_novel(
                    collect.getVoaid(),
                    collect.getOrderNumber(),
                    collect.getLevel(),
                    collect.getChapterOrder(),
                    collect.getSound(),
                    show,
                    collect.getBookname_cn(),
                    collect.getBookname_en(),
                    types
            ));
        }
        return temp;
    }

    //将章节详情数据-小说转换为数据库数据
    public static List<ChapterDetailEntity_novel> transNovelChapterDetailToDB(String types, List<Novel_chapter_detail> list){
        List<ChapterDetailEntity_novel> temp = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Novel_chapter_detail detail = list.get(i);

            temp.add(new ChapterDetailEntity_novel(
                    detail.getBeginTiming(),
                    TextUtils.isEmpty(detail.getVoaid())?0:Long.parseLong(detail.getVoaid()),
                    detail.getChapter_order(),
                    TextUtils.isEmpty(detail.getParaid())?0:Integer.parseInt(detail.getParaid()),
                    detail.getEndTiming(),
                    detail.getImage(),
                    detail.getOrderNumber(),
                    detail.getSentence_audio(),
                    TextUtils.isEmpty(detail.getLevel())?0:Integer.parseInt(detail.getLevel()),
                    TextUtils.isEmpty(detail.getIndex())?0:Integer.parseInt(detail.getIndex()),
                    TextUtils.isEmpty(detail.getTextCH())?"":detail.getTextCH().trim(),
                    TextUtils.isEmpty(detail.getTextEN())?"":detail.getTextEN().trim(),
                    types
            ));
        }

        return temp;
    }

    //小说-转换收藏数据
    public static List<ChapterCollectEntity> transNovelChapterCollectData(String types, int userId, List<Novel_chapter_collect> list){
        List<ChapterCollectEntity> temp = new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                Novel_chapter_collect collect = list.get(i);
                temp.add(new ChapterCollectEntity(
                        types,
                        collect.getVoaid(),
                        String.valueOf(userId),
                        collect.getSeries(),
                        "",
                        collect.getBookname_cn(),
                        collect.getBookname_en()
                ));
            }
        }
        return temp;
    }

    /*****************其他数据****************/
    //评测数据
    public static EvalEntity_chapter transSingleEvalChapterData(String types, String voaId, String paraId, String idIndex, String filePath, Eval_result result){
        if (result!=null){
            String words = GsonUtil.toJson(result.getWords());

            return new EvalEntity_chapter(
                    result.getSentence(),
                    result.getScores(),
                    result.getTotal_score(),
                    filePath,
                    result.getUrl(),
                    words,
                    types,
                    voaId,
                    paraId,
                    idIndex
            );
        }
        return null;
    }

    //点赞数据
    public static AgreeEntity transSingleAgreeEvalData(String userId, String agreeId, String types, String voaId, String evalSentenceId){
        return new AgreeEntity(
                userId,
                agreeId,
                types,
                voaId,
                evalSentenceId
        );
    }

    //将单词数据-中小学转换为数据库数据
    /*public static List<WordEntity_junior> transJuniorWordToDB(List<Junior_word> list){
        List<WordEntity_junior> temp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Junior_word word = list.get(i);

            temp.add(new WordEntity_junior(
                    word.getDef(),
                    word.getUpdateTime(),
                    word.getBook_id(),
                    word.getVersion(),
                    word.getExamples(),
                    word.getVideoUrl(),
                    word.getPron(),
                    TextUtils.isEmpty(word.getVoa_id())?0:Long.parseLong(word.getVoa_id()),
                    TextUtils.isEmpty(word.getIdindex())?0:Integer.parseInt(word.getIdindex()),
                    word.getAudio(),
                    TextUtils.isEmpty(word.getPosition())?0:Integer.parseInt(word.getPosition()),
                    word.getSentence_cn(),
                    word.getPic_url(),
                    TextUtils.isEmpty(word.getUnit_id())?0:Integer.parseInt(word.getUnit_id()),
                    transWordToEntityData(word.getWord()),
                    transWordToEntityData(word.getSentence()),
                    word.getSentence_audio()
            ));
        }
        return temp;
    }

    //将评测单词数据-新概念转换为数据库数据
    public static EvalEntity_word transJuniorEvalWordToDB(String types, String bookId, String voaId, String id, String position, String localPath, Junior_eval bean){
        if (bean==null){
            return null;
        }

        String words = null;
        if (bean.getWords()!=null&&bean.getWords().size()>0){
            words = GsonUtil.toJson(bean.getWords());
        }

        return new EvalEntity_word(
                types,
                bookId,
                voaId,
                position,
                transWordToEntityData(bean.getSentence()),
                String.valueOf(bean.getTotal_score()),
                bean.getUrl(),
                words,
                id,
                localPath
        );
    }

    //将单词闯关进度数据转换为数据库类
    public static WordBreakPassEntity transWordBreakProgressToDB(String types, String bookId, String id, long userId){
        return new WordBreakPassEntity(
                types,
                bookId,
                id,
                userId
        );
    }

    //将单词闯关数据转为数据库类
    public static List<WordBreakEntity> transWordBreakToDB(long userId, Map<WordBean,WordBean> map){
        List<WordBreakEntity> temp = new ArrayList<>();

        for (WordBean key:map.keySet()){
            WordBean result = map.get(key);

            temp.add(new WordBreakEntity(
                    key.getTypes(),
                    key.getBookId(),
                    key.getVoaId(),
                    key.getId(),
                    TextUtils.isEmpty(key.getPosition())?0:Integer.parseInt(key.getPosition()),
                    key.getWord(),
                    result.getWord(),
                    key.getWord(),
                    userId
            ));
        }
        return temp;
    }*/
    /***********************转换为通用类***********************/
    //中小学-转换类型数据
    /*public static List<Pair<String,List<Pair<String,String>>>> transJuniorTypeData(List<Junior_type> list){
        List<Pair<String,List<Pair<String,String>>>> temp = new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                Junior_type type = list.get(i);

                List<Pair<String,String>> typeList = new ArrayList<>();
                if (type.getSeriesData()!=null&&type.getSeriesData().size()>0){
                    for (int j = 0; j < type.getSeriesData().size(); j++) {
                        Junior_type.SeriesDataBean bean = type.getSeriesData().get(j);
                        typeList.add(new Pair<>(bean.getCategory(), bean.getSeriesName()));
                    }
                }

                temp.add(new Pair<>(type.getSourceType(), typeList));
            }
        }
        return temp;
    }*/

    //评测详情-转换类型数据
    public static List<EvalRankDetailBean> transEvalRankDetailData(String types, String voaId, List<Eval_rank_detail> list){
        List<EvalRankDetailBean> temp = new ArrayList<>();

        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                Eval_rank_detail detail = list.get(i);

                temp.add(new EvalRankDetailBean(
                        detail.getParaid(),
                        detail.getScore(),
                        detail.getShuoshuotype(),
                        detail.getAgainstCount(),
                        detail.getAgreeCount(),
                        detail.getTopicId(),
                        detail.getShuoShuo(),
                        detail.getId(),
                        detail.getIdIndex(),
                        detail.getCreateDate(),
                        types,
                        voaId
                ));
            }
        }
        return temp;
    }

    /*********************辅助方法*****************/
    //将单词转为数据库存储的数据
    public static String transWordToEntityData(String word){
        if (TextUtils.isEmpty(word)){
            return word;
        }

        //因为部分特殊字符需要处理，这里进行转换处理
        if (word.contains("'")){
            word = word.replace("'","‘");
        }

        return word;
    }
}
