package com.iyuba.talkshow.newdata;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;

/**
 * Created by iyuba on 2019/2/21.
 */

public class GlideUtil {

    public static void setCornerImage(String imageUrl, Context mContext, Transformation transformation, int placeImage, ImageView imageView) {

        Glide.with(mContext).asBitmap().load(imageUrl)
                .skipMemoryCache(true)
                .placeholder(placeImage)
                .error(placeImage)
                .transform(transformation)
                .into(imageView);

    }

    public static void setImage(String imageUrl, Context mContext,int placeImage, ImageView imageView){
        Glide.with(mContext).asBitmap().load(imageUrl)
                .placeholder(placeImage)
                .error(placeImage)
                .dontAnimate()  //防止加载网络图片变形
                .into(imageView);
    }

    //专为加载个人头像使用
    public static void setHeadImage(String imageUrl, Context mContext,int placeImage, ImageView imageView){
//        String time = LoginConfig.Instance(mContext).getHeadUpdateTime();
        Glide.with(mContext).asBitmap().load(imageUrl)
//                .signature(new StringSignature(time)) //加载同一url
                .placeholder(placeImage)
                .error(placeImage)
                .dontAnimate()  //防止加载网络图片变形
                .into(imageView);
    }

    //专为加载个人头像使用
    public static void setCornerHeadImage(String imageUrl, Context mContext,int placeImage, Transformation transformation,ImageView imageView){
//        String time = LoginConfig.Instance(mContext).getHeadUpdateTime();
        Glide.with(mContext).asBitmap().load(imageUrl)
//                .signature(new StringSignature(time)) //加载同一url
                .placeholder(placeImage)
                .error(placeImage)
                .dontAnimate()  //防止加载网络图片变形
                .transform(transformation)
                .into(imageView);
    }


}
