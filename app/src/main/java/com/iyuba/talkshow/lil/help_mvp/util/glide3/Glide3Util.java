package com.iyuba.talkshow.lil.help_mvp.util.glide3;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * @desction:  Glide图片加载
 * @date: 2023/4/5 18:21
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class Glide3Util {

    //加载图片
    public static void loadImg(Context context,String pathOrUrl,int placeId,ImageView imageView){
        Glide.with(context)
                .asBitmap()
                .load(pathOrUrl)
                .placeholder(placeId)
                .error(placeId)
                .into(imageView);
    }

    //加载圆形图片
    public static void loadCircleImg(Context context,String pathOrUrl,int placeId,ImageView imageView){
        Glide.with(context)
                .asBitmap()
                .load(pathOrUrl)
                .placeholder(placeId)
                .error(placeId)
                .apply(new RequestOptions().circleCrop())
                .into(imageView);
    }

    //加载圆形图片
    public static void loadCircleImg(Context context,int pathOrUrl,int placeId,ImageView imageView){
        Glide.with(context)
                .asBitmap()
                .load(pathOrUrl)
                .placeholder(placeId)
                .error(placeId)
                .apply(new RequestOptions().transform(new RoundedCornersTransformation(10,10)))
                .into(imageView);
    }


    //加载圆角图片
    public static void loadRoundImg(Context context, String pathOrUrl, int placeId,int radius,ImageView imageView){
        Glide.with(context)
                .asBitmap()
                .load(pathOrUrl)
                .placeholder(placeId)
                .error(placeId)
                .apply(new RequestOptions().transform(new RoundedCornersTransformation(10,10)))
                .into(imageView);
    }

    //加载gif图片
    public static void loadGif(Context context,String pathOrUrl,int placeId,ImageView imageView){
        Glide.with(context)
                .asGif()
                .load(pathOrUrl)
                .placeholder(placeId)
                .error(placeId)
                .into(imageView);
    }

    //加载gif图片
    public static void loadGif(Context context,int pathOrUrl,int placeId,ImageView imageView){
        Glide.with(context)
                .asGif()
                .load(pathOrUrl)
                .placeholder(placeId)
                .error(placeId)
                .into(imageView);
    }

    //加载svg图片
    /*public static void loadSvg(Context context,String url,ImageView imageView){
        *//*Uri imageUri = Uri.parse(url);
        RequestBuilder<PictureDrawable> requestBuilder = Glide.with(context)
                .as(PictureDrawable.class)
                .transition(withCrossFade())
                .listener(new SvgSoftwareLayerSetter());

        requestBuilder.load(imageUri).into(imageView);*//*

        GlideToVectorYou.justLoadImage((AppCompatActivity)context,Uri.parse(url),imageView);
    }*/

    //加载头像图片
    public static void loadHeadImg(Context context,String pathOrUrl,int placeId,ImageView imageView){
        Glide.with(context).clear(imageView);
        Glide.with(context)
                .setDefaultRequestOptions(new RequestOptions().skipMemoryCache(true))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .load(pathOrUrl)
                .placeholder(placeId)
                .error(placeId)
                .apply(new RequestOptions().circleCrop())
                .into(imageView);
    }
}