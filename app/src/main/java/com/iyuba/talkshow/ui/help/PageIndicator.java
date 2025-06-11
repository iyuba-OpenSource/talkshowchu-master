package com.iyuba.talkshow.ui.help;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.iyuba.talkshow.R;

import java.util.ArrayList;
import java.util.List;

public class PageIndicator extends LinearLayout {

    private Context mContext;
    private final List<ImageView> indicators = new ArrayList<>();
    private int currPage = 0;

    public PageIndicator(Context context) {
        super(context);
        initPageIndicator(context);
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPageIndicator(context);
    }

    public void initPageIndicator(Context context) {
        this.mContext = context;
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL
                | Gravity.CENTER_VERTICAL);
        refPageIndicator();
    }

    public void refPageIndicator() {
        if (indicators != null && indicators.size() != 0) {
            for (int i = 0; i < indicators.size(); i++) {
                ImageView ivTemp = indicators.get(i);
                addView(ivTemp);
            }
        } else {
            ImageView ivTemp = new ImageView(mContext);
            ivTemp.setPadding(5, 5, 5, 5);
            ivTemp.setImageResource(R.drawable.shape_orange_circle);
            indicators.add(ivTemp);
            addView(ivTemp);
        }
    }

    public void setIndicator(int pageNum) {
        indicators.clear();
        removeAllViews();
        for (int i = 0; i < pageNum; i++) {
            ImageView ivTemp = new ImageView(mContext);
            ivTemp.setPadding(5, 5, 5, 5);
            ivTemp.setImageResource(R.drawable.shape_white_circle);
            indicators.add(ivTemp);
        }
        refPageIndicator();
        setCurrIndicator(currPage);
    }

    public void setCurrIndicator(int currPage) {
        this.currPage = currPage;
        for (int i = 0; i < indicators.size(); i++) {
            ImageView ivTemp = indicators.get(i);
            if (i == this.currPage) {
                ivTemp.setImageResource(R.drawable.shape_orange_circle);
            } else {
                ivTemp.setImageResource(R.drawable.shape_white_circle);
            }
        }
    }

}
