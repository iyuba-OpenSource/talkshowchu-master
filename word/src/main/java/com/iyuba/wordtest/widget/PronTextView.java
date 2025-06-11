package com.iyuba.wordtest.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

public class PronTextView extends AppCompatTextView {

    Typeface type ;

    Context context  ;

    public PronTextView(Context context) {
        super(context);
        this.context = context;
    }

    public PronTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public PronTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        type = Typeface.createFromAsset(context.getAssets(), "font/SEGOEUI.TTF");
        setTypeface(type);
        super.onDraw(canvas);
    }
}
