package com.iyuba.talkshow.lil.help_fix.ui.study.word.wordTrain;

import android.util.Log;
import android.util.Pair;

import com.iyuba.talkshow.lil.help_fix.data.bean.WordBean;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_mvp.mvp.BasePresenter;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.entity.TalkShowWords;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @title:
 * @date: 2023/5/26 09:21
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordTrainPresenter extends BasePresenter<WordTrainView> {

    @Override
    public void detachView() {
        super.detachView();
    }

    //获取单词数据
    private List<WordBean> getWordData(String types, String bookId, int unitId,int voaId){
        List<WordBean> list = new ArrayList<>();

        switch (types){
            case TypeLibrary.BookType.junior_primary:
            case TypeLibrary.BookType.junior_middle:
                //中小学
                List<TalkShowWords> juniorTempList = new ArrayList<>();
                if (unitId>0){
                    juniorTempList = WordDataBase.getInstance(ResUtil.getInstance().getContext()).getTalkShowWordsDao().getUnitWords(Integer.parseInt(bookId),unitId);
                }else {
                    juniorTempList = WordDataBase.getInstance(ResUtil.getInstance().getContext()).getTalkShowWordsDao().getUnitByVoa(Integer.parseInt(bookId),voaId);
                }
                return transJuniorDataToShow(types,bookId,unitId,voaId,juniorTempList);
//            case TypeLibrary.BookType.conceptFour:
//                //新概念全四册
//                List<WordEntity_conceptFour> conceptFourList = ConceptDataManager.searchConceptFourWordByVoaIdFromDB(id);
//                if (conceptFourList!=null&&conceptFourList.size()>0){
//                    return DBTransUtil.conceptFourWordToWordData(types,conceptFourList);
//                }
//                break;
//            case TypeLibrary.BookType.conceptJunior:
//                //新概念青少版
//                List<WordEntity_conceptJunior> conceptJuniorList = ConceptDataManager.searchConceptJuniorWordByUnitIdFromDB(id);
//                if (conceptJuniorList!=null&&conceptJuniorList.size()>0){
//                    return DBTransUtil.conceptJuniorWordToWordData(types,conceptJuniorList);
//                }
//                break;
        }

        return list;
    }

    //根据单词数据进行处理
    public List<Pair<WordBean,List<WordBean>>> getRandomWordShowData(String types,String bookId,int unitId,int voaId){
        // TODO: 2025/2/14 群里测试出存在问题：这里是之前的逻辑，看起来针对3个及以下的单词情况会存在处理错误的问题，使用下边的逻辑处理
        /*//本课程的单词数据
        List<WordBean> oldList = getWordData(types, bookId,unitId,voaId);
        //本课程的单词内容
        List<Pair<Integer,WordBean>> oldPairList = new ArrayList<>();
        //复制上边的数据，进行答案处理
        List<Pair<Integer,WordBean>> oldPairCloneList = new ArrayList<>();

        //新的随机数据
        List<Pair<WordBean,List<WordBean>>> randomList = new ArrayList<>();

        if (oldList==null||oldList.size()==0){
            return randomList;
        }

        //1.将单词数据转为map数据
        for (int i = 0; i < oldList.size(); i++) {
            oldPairList.add(new Pair<>(i,oldList.get(i)));
            oldPairCloneList.add(new Pair<>(i,oldList.get(i)));
        }

        //2.将数据转换为随机数据
        while (oldPairList.size()>0){
            //获取随机数据
            int randomInt = (int) (Math.random()*oldPairList.size());
            Pair<Integer,WordBean> randomPair = oldPairList.get(randomInt);

            //将原来的数据中删除选中的数据
            oldPairList.remove(randomPair);

            //获取答案数据(获取不重复的3个数据，然后将标准答案放在随机的位置)
            List<WordBean> answerList = new ArrayList<>();
            Map<String,WordBean> answerMap = new HashMap<>();
            int answerCount = Math.min(oldPairCloneList.size(), 3);
            while (answerMap.keySet().size()<answerCount){
                //这里使用拷贝的数据，因为oldPairList数据逐渐被删除，后面会导致数据不足
                int answerInt = (int) (Math.random()*oldPairCloneList.size());
                Pair<Integer,WordBean> answerPair = oldPairCloneList.get(answerInt);
                //当前选中的单词相关数据
                WordBean selectBean = randomPair.second;

                //去掉同一个数据、已经存在的数据和相同释义的数据
                if (!answerPair.first.equals(randomPair.first)
                        && !answerPair.second.getDef().trim().equals(selectBean.getDef().trim())
                        && answerMap.get(answerPair.first)==null){
                    answerMap.put(answerPair.second.getWord(), answerPair.second);
                }
            }

            for (String key:answerMap.keySet()){
                answerList.add(answerMap.get(key));
            }

            //增加正确的答案
            answerList.add(randomPair.second);
            //将所有的答案随机，这样就不会出现只有前三个中才有正确答案了
            Collections.shuffle(answerList);

            //组合数据显示
            randomList.add(new Pair<>(randomPair.second,answerList));
            //将数据删除
            answerMap.clear();
        }

        //返回随机操作
        return randomList;*/

        /***********************************新的操作逻辑*********************************/
        //先获取当前课程的单词数据
        //然后每次都从本书籍中获取100个随机数据
        //之后随机获取几个数据作为答案数据处理(需要判断是否单词相同)
        //最后随机混合数据

        //需要展示的数据
        List<Pair<WordBean, List<WordBean>>> showList = new ArrayList<>();
        //当前课程的单词数据
        List<WordBean> curWordList = getWordData(types, bookId, unitId, voaId);
        //处理显示数据内容
        for (int i = 0; i < curWordList.size(); i++) {
            //当前单词数据
            WordBean showWordData = curWordList.get(i);
            //随机获取的单词数据
            List<WordBean> randomWordList = transJuniorDataToShow(types, bookId, unitId, voaId, WordDataBase.getInstance(ResUtil.getInstance().getContext()).getTalkShowWordsDao().getBookWordsLimit100(Integer.parseInt(bookId)));

            //随机获取三个答案数据，并且将正确数据加入，顺便随机混淆
            int randomInt = 0;
            List<WordBean> answerList = new ArrayList<>();

            answerTag:
            for (int j = 0; j < randomWordList.size(); j++) {
                //当前的随机数据
                WordBean curRandomData = randomWordList.get(j);

                //与单词数据做比较
                if (!showWordData.getWord().equals(curRandomData.getWord())){
                    answerList.add(curRandomData);
                    randomInt++;
                }

                if (randomInt>=3){
                    break answerTag;
                }
            }

            //组成显示内容
            answerList.add(showWordData);
            //随机处理
            Collections.shuffle(answerList);
            //保存在数据中
            showList.add(new Pair<>(showWordData,answerList));
        }

        return showList;
    }

    //转换中小学数据为标准数据
    private List<WordBean> transJuniorDataToShow(String types,String bookId,int unitId,int voaId,List<TalkShowWords> list){
        List<WordBean> tempList = new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                TalkShowWords words = list.get(i);
                tempList.add(new WordBean(
                        types,
                        bookId,
                        String.valueOf(voaId),
                        String.valueOf(unitId),
                        words.word,
                        words.pron,
                        words.def,
                        String.valueOf(words.position),
                        words.Sentence,
                        words.Sentence_cn,
                        words.pic_url,
                        words.videoUrl,
                        words.audio,
                        words.Sentence_audio
                        ));
            }
        }
        return tempList;
    }
}
