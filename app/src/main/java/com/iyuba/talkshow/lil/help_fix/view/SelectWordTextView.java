package com.iyuba.talkshow.lil.help_fix.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.ColorInt;
import androidx.appcompat.widget.AppCompatTextView;


import com.iyuba.talkshow.R;

import java.util.regex.Pattern;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/04/08
 * 邮箱：jxfengmtx@gmail.com
 */
public class SelectWordTextView extends AppCompatTextView {
    private static final String TAG = "SelectWordTextVIew";
    private OnClickWordListener mOnClickWordListener;
    private int mTouchSlop;
    private int mSelectedColor;

    private int[] mTempPosition = new int[]{-1, -1};
    private CharSequence text;

    public SelectWordTextView(Context context) {
        super(context);
        init(context, null);
    }

    public SelectWordTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SelectWordTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SelectWordTextView);
        mSelectedColor = array.getColor(R.styleable.SelectWordTextView_selectColor, getResources().getColor(R.color.textColorVice));
        array.recycle();
    }

    float downX = 0;
    float downY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mOnClickWordListener != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    downY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(event.getX() - downX) < mTouchSlop
                            && Math.abs(event.getY() - downY) < mTouchSlop) {
                        if (TextUtils.isEmpty(text)) {   //单选 ，去掉即为多选
                            text = getText();
                            }
                        String word = handleClickWord(text, getOffsetForPosition(event.getX(), event.getY()));
                        //  Log.e(TAG, "onTouchEvent: click word = " + word);
                        //暂时不进行筛选处理
//                        if (word != null) {
//                            String regEx = "\\p{P}|\\p{S}|\\s+";
//                            word = Pattern.compile(regEx).matcher(word).replaceAll("").trim();
//                        }
                        if (!TextUtils.isEmpty(word)) {
                            mOnClickWordListener.onClickWord(word);
                        }
                    }
                    break;
                default:
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private int[] getWordPosition(String text, int offsetPosition) {
        //   Log.e(TAG, "getWord offsetPosition =: " + offsetPosition);
        if (offsetPosition >= text.length() || offsetPosition < 0
                || !isLetter(text.charAt(offsetPosition))) {
            return null;
        }
        //单词的结束位置
        int endPositionOfWord = 0;
        //单词的开始位置
        int startPositionOfWord = 0;

        //这里获取当前点击单词的结束位置
        for (int i = offsetPosition; i < text.length(); i++) {
            if (i == text.length() - 1) {
                endPositionOfWord = i;
                break;
            }
            if (isEdge(text.charAt(i))) {
                endPositionOfWord = i;
                break;
            }

        }

        //这里获取当前点击单词的开始位置
        for (int i = offsetPosition; i >= 0; i--) {
            if (i == 0) {
                startPositionOfWord = i;
                break;
            }
            if (isEdge(text.charAt(i))) {
                startPositionOfWord = i + 1;
                break;
            }

        }
        Log.e(TAG, "getWord: startPositionOfWord = " + startPositionOfWord + " endPositionOfWord =" + endPositionOfWord);
        return new int[]{startPositionOfWord, endPositionOfWord};

    }

    private String handleClickWord(CharSequence text, int offsetPosition) {
        //取消
        if (offsetPosition >= mTempPosition[0] && offsetPosition < mTempPosition[1]) {
            mTempPosition[0] = -1;
            mTempPosition[1] = -1;
            setText(text);
            return null;
        }
        int[] wordPosition = getWordPosition(text.toString(), offsetPosition);
        if (wordPosition == null) {
            return null;
        }
        int start = wordPosition[0];
        int end = wordPosition[1];

        mTempPosition[0] = start;
        mTempPosition[1] = end;

        //这里是设置背景颜色的，暂时去掉
        /*SpannableString stringBuilder = new SpannableString(text);
        stringBuilder.setSpan(new BackgroundColorSpan(mSelectedColor),
                start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        setText(stringBuilder);*/
        return text.toString().substring(start, end);
    }

    private boolean isEdge(char curr) {
        return curr == ' ' || curr == '\n'
                || (!isLetter(curr) && curr != '\'');
    }


    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z');
    }

    public void setOnClickWordListener(OnClickWordListener onClickWordListener) {
        mOnClickWordListener = onClickWordListener;
    }

    public interface OnClickWordListener {
        void onClickWord(String word);
    }

    public void setSelectedColor(@ColorInt int selectedColor) {
        mSelectedColor = selectedColor;
    }

    public int[] getmTempPosition() {
        return mTempPosition;
    }

    public void setmTempPosition(int[] mTempPosition) {
        this.mTempPosition = mTempPosition;
    }
}
