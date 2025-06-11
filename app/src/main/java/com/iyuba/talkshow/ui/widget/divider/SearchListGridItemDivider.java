package com.iyuba.talkshow.ui.widget.divider;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.R;

public class SearchListGridItemDivider extends GridItemDivider {
    public SearchListGridItemDivider(Context context) {
        super(context);
        mDivider = context.getResources().getDrawable(R.drawable.voa_activity_divider);
    }

    @Override
    boolean isLastColumn(RecyclerView parent, int pos, int spanCount, int childCount) {
        return pos % 2 == 1;
    }

    @Override
    boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        return false;
    }

}
