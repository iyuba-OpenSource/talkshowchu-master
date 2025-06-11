package com.iyuba.talkshow.ui.preview;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.data.model.WavListItem;
import com.iyuba.talkshow.util.RxUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

/**
 * PreviewInfoBean
 *
 * @author wayne
 * @date 2018/2/9
 */
public class PreviewInfoBean implements Serializable {

    private int sentenceCount;
    /**
     * key 句子，value 分数
     */
    private final Map<Integer, Integer> scoreArray;
    private final Map<Integer, Integer> fluentArray;
    private final Map<Integer, String> audioArray;
    /**
     * 单词数
     */
    private final List<String> sentenceList;

    private final List<Float> timeList;

    private List<Integer> indexList;

    public PreviewInfoBean() {
        scoreArray = new HashMap<>();
        audioArray = new HashMap<>();
        fluentArray = new HashMap<>();
        timeList = new ArrayList<>();
        sentenceList = new ArrayList<>();
    }

    /**
     * 完整度
     */
    public int getFluence() {
        int count = 0;

        for (Integer integer : indexList) {
            try {
                count += fluentArray.get(integer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (indexList.size() == 0) {
            return 0;
        }
        int value = (int) (count * 1.0 / indexList.size());
        Log.e("PreviewInfoBean", "getAverageScore  " + value);
        return value;
    }

    /**
     * 完整度
     */
    public int getCompleteness() {
        int value = (int) (indexList.size() * 1.0 / sentenceCount * 100);
        Log.e("PreviewInfoBean", "getCompleteness  " + value);
        return value;
    }
    public int getCompleteness(int count) {
        int value = count * 100 / sentenceCount;
        Log.e("PreviewInfoBean", "getCompleteness  " + value);
        return value;
    }

    /**
     * 准确度
     */
    public int getAverageScore() {
        int count = 0;
        if (indexList == null || indexList.size() < 1) {
            return count;
        }
        for (Integer integer : indexList) {
            try {
                count += scoreArray.get(integer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (indexList.size() == 0) {
            return 0;
        }
        int value = (int) (count * 1.0 / indexList.size());
        Log.e("PreviewInfoBean", "getAverageScore  " + value);
        return value;
    }

    /**
     * 录音时间
     */
    public String getRecordTime() {
        float count = 0;
        for (Float value : timeList) {
            try {
                count += value;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String result = String.format(Locale.CHINA, "%.1f", count);
        Log.e("PreviewInfoBean", "getRecordTime  " + result);
        return result;
    }

    /**
     * 单词数
     */
    public int getWordCount() {
        int count = 0;
        for (String value : sentenceList) {
            try {
                count += value.split(" ").length;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.e("PreviewInfoBean", "getWordCount  " + count);
        return count;
    }

    /**
     * 根据记录的分数，得到录音的索引
     */
    public void initIndexList() {
        indexList = new ArrayList<>();
        Integer[] keys = scoreArray.keySet().toArray(new Integer[scoreArray.size()]);

        for (int i = 0; i < keys.length; i++) {
            Integer value = keys[i];
            Log.e("PreviewInfoBean", "initIndexList  " + value);
            indexList.add(keys[i]);
        }
    }

    /**
     * 从数据库取出草稿记录，然后设置分数
     */
    public void initIndexList(Record record, final View.OnClickListener listener) {
        String score = record.score();
        String url  = record.audio();
        if (TextUtils.isEmpty(score)) {
            return;
        }
        java.lang.reflect.Type type = new TypeToken<HashMap<Integer, Integer>>() {
        }.getType();
        java.lang.reflect.Type type1 = new TypeToken<HashMap<Integer, String>>() {
        }.getType();
        Map<Integer, Integer> map = new Gson().fromJson(score, type);
        Map<Integer, String> map2 = new Gson().fromJson(url, type1);

        Observable.from(map.entrySet())
                .compose(RxUtil.io2main())
                .subscribe(new Subscriber<Map.Entry<Integer, Integer>>() {
                    @Override
                    public void onCompleted() {
                        initIndexList();
                        listener.onClick(null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();

                    }

                    @Override
                    public void onNext(Map.Entry<Integer, Integer> entry) {
                        Log.e("PreviewInfoBean", "initIndexList  " + new Gson().toJson(entry));
                        setSentenceScore(entry.getKey(), entry.getValue());
                    }
                });
        Observable.from(map2.entrySet())
                .compose(RxUtil.io2main())
                .subscribe(new Subscriber<Map.Entry<Integer, String>>() {
                    @Override
                    public void onCompleted() {
                        initIndexList();
                        listener.onClick(null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Map.Entry<Integer, String> entry) {
                        Log.e("PreviewInfoBean", "initIndexList  " + new Gson().toJson(entry));
                        setSentenceUrl(entry.getKey(), entry.getValue());
                    }
                });


    }

    public void setSentenceScore(int key, int score) {
        Log.e("PreviewInfoBean", "setSentenceScore ::: key ::  " + key + " :: value :: " + score);
        scoreArray.put(key, score);
    }

    public void setSentenceFluent(int key, int score) {
        Log.e("PreviewInfoBean", "setSentenceScore ::: key ::  " + key + " :: value :: " + score);
        fluentArray.put(key, score);
    }

    public void setSentenceUrl(int pos, String url) {
        Log.e("PreviewInfoBean", "setSentenceScore ::: pos ::  " + pos + " :: url :: " + url);
        audioArray.put(pos, url);
    }

    public void setVoaTexts(List<VoaText> voaTextList) {
        if (voaTextList == null || voaTextList.size() < 1) {
            return;
        }
        sentenceCount = voaTextList.size();
        timeList.clear();
        sentenceList.clear();
        for (VoaText voaText : voaTextList) {
            timeList.add(voaText.endTiming() - voaText.timing());
            sentenceList.add(voaText.sentence());
        }
    }

    public String getAllScore() {
        if (scoreArray.size() > 0) {
            String score = new Gson().toJson(scoreArray);
            Log.e("PreviewInfoBean", "getAllScore ::: score ::  " + score);
            return score;
        }
        return "";
    }
    public String getAllAudioUrl() {
        if (audioArray.size() > 0) {
            String audio = new Gson().toJson(audioArray);
            Log.e("PreviewInfoBean", "getAllAudioUrl ::: audio ::  " + audio);
            return audio;
        }
        return "";
    }

    /**
     * 设置从草稿取到的分数
     */
    public void setVoaTextScore(final List<VoaText> mVoaTextList, final View.OnClickListener listener) {
        if (scoreArray.size() > 0) {
            Observable.from(scoreArray.entrySet())
                    .compose(RxUtil.io2main())
                    .subscribe(new Subscriber<Map.Entry<Integer, Integer>>() {
                        @Override
                        public void onCompleted() {
                            listener.onClick(null);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Map.Entry<Integer, Integer> entry) {
                            Log.e("PreviewInfoBean", "setVoaTextScore  " + new Gson().toJson(entry));
                            mVoaTextList.get(entry.getKey()).setScore(entry.getValue());
                            mVoaTextList.get(entry.getKey()).setIscore(true);
                        }
                    });
        }
    }



}
