package com.iyuba.wordtest.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileOutputStream;

/**
 * 简单的画板工具
 */
public class CanvasView extends View {
    private Bitmap showBitmap;
    private Canvas showCanvas;
    private Paint bitmapPaint, textPaint;
    private Path canvasPath;

    public CanvasView(Context context) {
        super(context);
        init();
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        canvasPath = new Path();

        bitmapPaint = new Paint();
        bitmapPaint.setColor(Color.WHITE);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setAntiAlias(true);
        textPaint.setStrokeWidth(12);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        showBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        showCanvas = new Canvas(showBitmap);
        showCanvas.drawColor(Color.WHITE);

        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        canvas.drawBitmap(showBitmap, 0, 0, bitmapPaint);
        canvas.drawPath(canvasPath, textPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        long touchTime = System.currentTimeMillis();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                canvasPath.moveTo(touchX, touchY);

                if (onHandWriteListener != null) {
                    onHandWriteListener.onDown(touchX, touchY, touchTime);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                canvasPath.lineTo(touchX, touchY);

                if (onHandWriteListener != null) {
                    onHandWriteListener.onMove(touchX, touchY, touchTime);
                }
                break;
            case MotionEvent.ACTION_UP:
                canvasPath.lineTo(touchX, touchY);
                showCanvas.drawPath(canvasPath, textPaint);
                canvasPath.reset();

                if (onHandWriteListener != null) {
                    onHandWriteListener.onUp(touchX, touchY, touchTime);
                }
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void clearCanvas() {
        if (canvasPath != null) {
            canvasPath.reset();
        }

        if (showBitmap != null) {
            onSizeChanged(
                    showBitmap.getWidth(),
                    showBitmap.getHeight(),
                    showBitmap.getWidth(),
                    showBitmap.getHeight()
            );
        }

        if (onHandWriteListener != null) {
            onHandWriteListener.onClear();
        }
    }

    //接口-用于手写识别
    private OnHandWriteListener onHandWriteListener;

    public interface OnHandWriteListener {
        void onDown(float touchX, float touchY, long touchTime);

        void onMove(float touchX, float touchY, long touchTime);

        void onUp(float touchX, float touchY, long touchTime);

        void onClear();
    }

    public void setOnHandWriteListener(OnHandWriteListener onHandWriteListener) {
        this.onHandWriteListener = onHandWriteListener;
    }

    //将图像保存为图片
    public void saveFile(String filePath) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            showBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
