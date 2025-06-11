package com.iyuba.talkshow.ui.widget;

import androidx.viewpager.widget.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class AbstractViewPagerAdapter<T> extends PagerAdapter {
    private final List<T> mData;
    private final SparseArray<View> mViews;

    public AbstractViewPagerAdapter(List<T> data) {
        mData = data;
        mViews = new SparseArray<>(data.size());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViews.get(position);
        if (view == null) {
            view = newView(container, position);
            mViews.put(position, view);
        }
        container.addView(view);
        return view;
    }

    public abstract View newView(ViewGroup parent, int position);

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
    }

    public T getItem(int position) {
        return mData.get(position);
    }

}
