package com.iyuba.wordtest.utils;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import com.iyuba.wordtest.R;
import com.iyuba.wordtest.bean.SendEvaluateResponse;

import java.util.List;

public class SentenceSpanUtils {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static SpannableStringBuilder getSpanned(Context mContext, String sentence, List<SendEvaluateResponse.DataBean.WordsBean> words, String keyword) {
        SpannableStringBuilder builder = new SpannableStringBuilder(sentence);
        if (words != null && words.size() > 0) {
            String[] splictSentence = sentence.split(" ");
            for (int i = 0; i < words.size(); i++) {
                SendEvaluateResponse.DataBean.WordsBean wordsBean = words.get(i);
                if (Float.parseFloat(String.valueOf(wordsBean.getScore())) < 2.5) {
                    int beginPosition = sentence.toLowerCase().indexOf(splictSentence[i].toLowerCase());
                    if (i > 0) {
                        beginPosition = sentence.toLowerCase().indexOf(" " + splictSentence[i].toLowerCase());
                    }
                    builder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.red)), beginPosition, beginPosition + splictSentence[i].length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else if (Float.parseFloat(String.valueOf(wordsBean.getScore())) > 4) {
                    int beginPosition = sentence.toLowerCase().indexOf(splictSentence[i].toLowerCase());
                    if (i > 0) {
                        beginPosition = sentence.toLowerCase().indexOf(" " + splictSentence[i].toLowerCase());
                    }
                    builder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.green)), beginPosition, beginPosition + splictSentence[i].length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

        builder.setSpan(new RelativeSizeSpan(1.0f), sentence.toLowerCase().indexOf(keyword.toLowerCase()),
                sentence.toLowerCase().indexOf(keyword.toLowerCase()) + keyword.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        return builder;
    }

    public static SpannableStringBuilder getSpanned(Context mContext, String sentence, List<Integer> good , List<Integer> bad) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String[] splictSentence = sentence.split(" ");
        int beginPosition ;
        for (int i = 0; i < splictSentence.length; i++) {
            String words = splictSentence[i]+" ";
            beginPosition = builder.length() ;
            builder.append(words);
            if (bad.contains(i) ) {
                builder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.red)), beginPosition, beginPosition + words.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (good.contains(i)) {
                builder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.green)), beginPosition, beginPosition + words.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {

            }
        }
        return builder;
    }
}
