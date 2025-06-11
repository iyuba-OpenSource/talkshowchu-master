//package com.iyuba.talkshow.lil.help_mvp.util.glide3;
//
//import android.content.Context;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapShader;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.RectF;
//import android.graphics.Shader;
//
//import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
//import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
//
///**
// * @desction: 圆角图片转换
// * @date: 2023/4/5 20:32
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// */
//public class GlideRoundTransformation extends BitmapTransformation {
//
//    //圆角半径
//    private static float radius = 0f;
//
//    public GlideRoundTransformation(Context context) {
//        this(context,0);
//    }
//
//    public GlideRoundTransformation(Context context,int dp){
//        super(context);
//        this.radius = Resources.getSystem().getDisplayMetrics().density*dp;
//    }
//
//    @Override
//    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
//        return roundCrop(pool,toTransform);
//    }
//
//    private static Bitmap roundCrop(BitmapPool pool,Bitmap source){
//        if (source==null){
//            return null;
//        }
//
//        Bitmap result = pool.get(source.getWidth(),source.getHeight(), Bitmap.Config.ARGB_8888);
//        if (result==null){
//            result = Bitmap.createBitmap(source.getWidth(),source.getHeight(), Bitmap.Config.ARGB_8888);
//        }
//
//        Canvas canvas = new Canvas(result);
//        Paint paint = new Paint();
//        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
//        paint.setAntiAlias(true);
//        RectF rectF = new RectF(0f,0f,source.getWidth(),source.getHeight());
//        canvas.drawRoundRect(rectF,radius,radius,paint);
//        return result;
//    }
//
//    @Override
//    public String getId() {
//        return getClass().getName()+Math.round(radius);
//    }
//}
