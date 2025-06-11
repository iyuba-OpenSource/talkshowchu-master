package com.iyuba.talkshow.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class DubbingProgressBar extends ProgressBar {

    public int position ;

    private int xPosition ;
    public String perfectTime;
    public String addingTime;
    Paint dividerPaint ;
    Paint textPaint ;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        invalidate();
    }

    public String getPerfectTime() {
        return perfectTime;
    }

    public void setPerfectTime(String perfectTime) {
        this.perfectTime = perfectTime;
    }

    public String getAddingTime() {
        return addingTime;
    }

    public void setAddingTime(String addingTime) {
        this.addingTime = addingTime;
    }

    public DubbingProgressBar(Context context) {
        this(context,null);
    }

    public DubbingProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DubbingProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        dividerPaint = new Paint();
        dividerPaint.setColor(Color.RED);
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(24);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        xPosition = position*getWidth()/100;
        drawDivider(canvas);
        drawPerfect(canvas);
        drawAddingTime(canvas);
        invalidate();
    }

    private void drawAddingTime(Canvas canvas) {
        canvas.drawText(addingTime,(xPosition+getWidth())/2 , getHeight()/4*3 ,textPaint);

    }

    private void drawPerfect(Canvas canvas) {
        canvas.drawText(perfectTime,(xPosition/2) , getHeight()/4 * 3,textPaint);
    }

    private void drawDivider(Canvas canvas) {
        canvas.drawRect(xPosition-2, 0 , xPosition+2 ,getHeight() , dividerPaint);
    }
}
