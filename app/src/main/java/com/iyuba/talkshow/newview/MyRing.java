package com.iyuba.talkshow.newview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.iyuba.talkshow.R;


public class MyRing extends View {


    private final Paint mPaint = new Paint();
    private float mViewCenterX, mViewCenterY;

    private final int strokeWidth = 3;

    private final int defaultColor = Color.parseColor("#eae5df");

    private Drawable src;
    private Paint ringColorPaint;

    public MyRing(Context context) {
        super(context);
    }

    public MyRing(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // 获取属性集合 TypedArray
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyRing);
        src = typedArray.getDrawable(R.styleable.MyRing_src);
        typedArray.recycle();

        ringColorPaint = new Paint(mPaint);
        ringColorPaint.setAntiAlias(true);
        ringColorPaint.setStyle(Paint.Style.STROKE);
        ringColorPaint.setStrokeWidth(strokeWidth);
    }

    public MyRing(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

//    public MyRing(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        //view的宽和高,相对于父布局(用于确定圆心)
        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();
        mViewCenterX = viewWidth / 2;
        mViewCenterY = viewHeight / 2;
    }

    private final int progress = 0;
    private int currProgress = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        @SuppressLint("DrawAllocation") RectF rectf = new RectF(getWidth() /4, getHeight() /4,
                getWidth() / 4 * 3, getHeight() / 4 * 3);
        if (src != null) {
            @SuppressLint("DrawAllocation") Bitmap bitmap = ((BitmapDrawable) src).getBitmap();
            canvas.drawBitmap(bitmap, null, rectf, ringColorPaint);
//            bitmap.recycle();
        }


        @SuppressLint("DrawAllocation") RectF oval = new RectF(strokeWidth, strokeWidth,
                getWidth() - strokeWidth, getHeight() - strokeWidth);
        //逆时针旋转90度
        canvas.rotate(-45, mViewCenterX, mViewCenterY);
        ringColorPaint.setColor(defaultColor);
        canvas.drawArc(oval, 0, 360, false, ringColorPaint);
        ringColorPaint.setColor(getResources().getColor(R.color.colorPrimary));
        canvas.drawArc(oval, 0, currProgress, false, ringColorPaint);
        ringColorPaint.setShader(null);

//        invalidate();
    }


    public void setCurrProgress(int currProgress, int resourceId) {
        this.currProgress = currProgress;
        src = getResources().getDrawable(resourceId);
        invalidate();
    }


}
