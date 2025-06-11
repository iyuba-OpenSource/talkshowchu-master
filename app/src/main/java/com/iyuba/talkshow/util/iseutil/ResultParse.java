package com.iyuba.talkshow.util.iseutil;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.iyuba.talkshow.newdata.EvaluateBean;

import java.util.ArrayList;
import java.util.List;

public class ResultParse {
	private static final String TAG = ResultParse.class.getSimpleName();

	private static int parseIndex;
	private static SpannableStringBuilder spannable;
	private static String sen;

	public static SpannableStringBuilder getSenResultLocal(String[] style, String s) {
		spannable = new SpannableStringBuilder();
		try {
			String[] words = s.trim().split(" ");
			List<String> stringList = new ArrayList<>();
			for (int i = 0; i < words.length; i++) {
				if (!"".equals(words[i])) {
					stringList.add(words[i]);
				}
			}
			Log.e("单词个数", stringList.size() + "--" + words);
			for (int i = 0; i < stringList.size(); i++) {
				EvaluateBean.WordsBean word = new EvaluateBean.WordsBean();
				word.setContent(stringList.get(i));

				if (i >= style.length) {
					word.setScore(0);
				} else {
					word.setScore(Float.parseFloat(style[i]));
				}
				setWordEvaluate(word);
			}
		} catch (Exception e) {
			e.printStackTrace();
			spannable = new SpannableStringBuilder(s);
		}
		return spannable;
	}

	public static SpannableStringBuilder getSenResultEvaluate(List<EvaluateBean.WordsBean> result, String s) {
		spannable = new SpannableStringBuilder();
		Log.e("单词个数", result.size() + "");

		for (EvaluateBean.WordsBean word : result) {
			setWordEvaluate(word);
		}

		return spannable;
	}

	public static void setWordEvaluate(EvaluateBean.WordsBean word) {
		String wordStr = word.getContent();

		if (TextUtils.isEmpty(wordStr)) {
			spannable.append("");
		} else if (word.getScore() < 2.5) {
			spannable.append(getSpannable(wordStr, Color.RED)).append(" ");
		} else if (word.getScore() > 4) {
			spannable.append(getSpannable(wordStr, 0xff079500)).append(" ");
		} else {
			spannable.append(getSpannable(wordStr, Color.BLACK)).append(" ");
		}
	}

	public static SpannableStringBuilder getSpannable(String wordStr, int spanColor) {
//		Log.e("单词分数--word", wordStr);
		SpannableStringBuilder child = new SpannableStringBuilder(wordStr.trim());
		String s_str = String.valueOf(wordStr.charAt(0));
		String e_str = String.valueOf(wordStr.charAt(wordStr.length() - 1));
		String rule = "\\p{P}";
		int start = 0;
		int end = wordStr.length() - 1;
		if (s_str.matches(rule)) {
			start++;
		}
		if (e_str.matches(rule)) {
			end--;
		}
		if (start > end) {
			child.setSpan(new ForegroundColorSpan(Color.BLACK), 0, wordStr.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		} else {
			Log.e(wordStr, start + "jieshu   " + end);
			if (start > 0) {
				child.setSpan(new ForegroundColorSpan(Color.BLACK), 0, start, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			}
			if (end < wordStr.length() - 1) {
				child.setSpan(new ForegroundColorSpan(Color.BLACK), end, wordStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			}
			child.setSpan(new ForegroundColorSpan(spanColor), start, end + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		}

//		Log.e("单词分数", child.toString());
		return child;
	}

	/*public static SpannableStringBuilder getSenResult(Result result, String s) {
		Log.e(TAG, "The total score of the result is " + result.total_score);
		sen = s;
		Log.e(TAG, sen);
		spannable = new SpannableStringBuilder(sen);
		parseIndex = 0;

		for (Sentence sentence : result.sentences) {
			Log.e(TAG, "sentence content is " + sentence.content);
			Log.e(TAG, "sentence total_score is " + sentence.total_score);
			if (!sentence.content.equals("sil")) {
				for (Word word : sentence.words) {
					Log.e(TAG, "Word: " + word.content + " Score: " + word.total_score);
					if (word.total_score != 0) {
						setWord(word);
					}
				}
			}
		}

		return spannable;
	}

	public static void setWord(Word word) {
		String wordStr = word.content;
		Log.e(TAG, "Setting Word : " + wordStr);
		if (word.total_score < 3 || word.total_score > 4) {
			int start = -1;
			int end = -1;
			int i = parseIndex;
			Log.e(TAG, "pareseIndex is : " + parseIndex);
			for (int j = 0; i < sen.length() && j < wordStr.length(); i++) {
				Log.e(TAG, sen.charAt(i) + "___" + wordStr.charAt(j));
				if (start == -1) {
					if (sen.charAt(i) == wordStr.charAt(j)
							|| sen.charAt(i) + 32 == wordStr.charAt(j)) {
						start = i;
						j++;
					}
				} else {
					if (sen.charAt(i) == wordStr.charAt(j)) {
						j++;
					} else {
						return;
					}
				}
			}

			if (start != -1) {
				end = i;
				parseIndex = i;

				Log.e(TAG, "start : " + start + " end : " + end);
				if (word.total_score < 3) {
					setRed(start, end);
				} else if (word.total_score > 4) {
					setGreen(start, end);
				}
			}
		} else {
			parseIndex += wordStr.length() + 1;
		}
		while (parseIndex < sen.length() && !isAlphabeta(sen.charAt(parseIndex))) {
			parseIndex++;
		}
	}*/

	private static boolean isAlphabeta(char c) {
		boolean result = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
        return result;
	}

	public static void setRed(int start, int end) {
		spannable.setSpan(new ForegroundColorSpan(Color.RED), start, end,
				Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
	}

	public static void setGreen(int start, int end) {
		spannable.setSpan(new ForegroundColorSpan(0xff079500), start, end,
				Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
	}

}
