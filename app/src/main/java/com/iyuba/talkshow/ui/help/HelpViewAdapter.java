package com.iyuba.talkshow.ui.help;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.ui.widget.AbstractViewPagerAdapter;

import java.util.List;

public class HelpViewAdapter extends AbstractViewPagerAdapter<Integer> {
    private final Options options = new BitmapFactory.Options();

    public HelpViewAdapter(List<Integer> data) {
        super(data);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
    }

    @Override
    public View newView(ViewGroup parent, int position) {
        View view = View.inflate(parent.getContext(), R.layout.item_help, null);
        ImageView imageview = view.findViewById(R.id.intro_pic);
        Bitmap bm = BitmapFactory.decodeResource(parent.getContext().getResources(),
                getItem(position), options);
        imageview.setImageBitmap(bm);
        Button startView = view.findViewById(R.id.button_start);
        if (position < 3) {
            startView.setVisibility(View.INVISIBLE);
        } else {
            startView.setVisibility(View.VISIBLE);
            startView.setOnClickListener(v -> {
                if (mStartListener != null) {
                    mStartListener.onStart();
                }
            });
        }
        return view;
    }

    private OnStartListener mStartListener;
    public void setOnStartListener(OnStartListener l) {
        mStartListener = l;
    }
    interface OnStartListener {
        void onStart();
    }
}
