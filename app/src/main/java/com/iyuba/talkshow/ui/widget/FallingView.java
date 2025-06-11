package com.iyuba.talkshow.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.iyuba.talkshow.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class FallingView extends SurfaceView implements SurfaceHolder.Callback , Runnable {


    private boolean mFlag  = true ;
    private final SurfaceHolder surfaceHolder;
    private final Bitmap bitmap ;
    private final int bitmapHeight ;
    private final int bitmapWidth ;
    Canvas canvas ;
    private int curGenerateCount;
    private int curIndex;
    private int lastStartX;
    private final Random random = new Random();
    private final int maxCount = 20 ;
    private int mCanvasWidth;
    private int mCanvasHeight;
    private final LinkedList<FallingItem> fallingItems = new LinkedList();
    private FallingItem item;
    private final Matrix mMatrix = new Matrix();
    private final Paint paint = new Paint();
    private int onceTime;
    private long startTime;

    public FallingView(Context context) {
        this(context , null);
    }

    public FallingView(Context context, AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public FallingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint.setAntiAlias(true);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.agree);
        bitmapHeight  = bitmap.getHeight() ;
        bitmapWidth  = bitmap.getWidth() ;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setZOrderOnTop(true);
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mFlag = false ;
    }

    @Override
    public void run() {
        while (mFlag){
            startTime = System.currentTimeMillis();
            canvas = surfaceHolder.lockCanvas() ;
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mCanvasHeight = canvas.getHeight() ;
            mCanvasWidth = canvas.getWidth() ;
            for (int i = 0; i < fallingItems.size(); i++) {
                item = fallingItems.get(i);
                mMatrix.setRotate(item.rotate, (float) bitmapWidth / 2, (float) bitmapHeight / 2);
                mMatrix.postTranslate(item.startX, item.startY);
                canvas.drawBitmap(bitmap, mMatrix, paint);
                item.setStartY(item.getStartY() + item.speed);
            }

            surfaceHolder.unlockCanvasAndPost(canvas);

            //添加坠落对象
            addItem();

            if (fallingItems.size() > 50) {
                fallingItems.remove(0);
            }
            onceTime = (int)(System.currentTimeMillis() - startTime);
        }

    }

    private void addItem() {
        if(curGenerateCount >= maxCount) {
            return;
        }
        curIndex ++;
        if(curIndex % 10 != 0) {
            return;
        }
        FallingItem item = new FallingItem();
        int startInLeft = 0;
        if(lastStartX > bitmapWidth) {
            startInLeft = random.nextInt(lastStartX - bitmapWidth);
        }
        int startInRight = 0;
        if(lastStartX + bitmapWidth - 1 < mCanvasWidth){
            startInRight = random.nextInt(mCanvasWidth - lastStartX - bitmapWidth + 1) + lastStartX;
        }
        if(startInLeft > 0 && startInRight > 0){
            item.startX = random.nextBoolean() ? startInLeft : startInRight;
        }else{
            if(startInLeft == 0){
                item.startX = startInRight;
            }
            if(startInRight == 0){
                item.startX = startInLeft;
            }
        }
        //int startInRight = random.nextInt(mCanvasWidth - bitmapWidth - lastStartX) + lastStartX + bitmapWidth;
        if(item.startX > mCanvasWidth - bitmapWidth){
            item.startX = mCanvasWidth - bitmapWidth;
        }
        item.startY = -60;
        item.speed = (random.nextInt(3)+2)*10;
        item.rotate = random.nextInt(360);
        lastStartX = item.startX;
        //添加到集合
        fallingItems.add(item);
        curGenerateCount++;
    }

    class FallingItem {


        int startX ;
        int startY ;
        int  rotate ;
        int  speed ;

        public int getStartX() {
            return startX;
        }

        public void setStartX(int startX) {
            this.startX = startX;
        }

        public int getStartY() {
            return startY;
        }

        public void setStartY(int startY) {
            this.startY = startY;
        }

        public int getRotate() {
            return rotate;
        }

        public void setRotate(int rotate) {
            this.rotate = rotate;
        }

        public int getSppeed() {
            return speed;
        }

        public void setSppeed(int sppeed) {
            this.speed = sppeed;
        }

    }
}
