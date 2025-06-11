package com.iyuba.talkshow.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.youth.banner.loader.ImageLoader;

public class GlideImageLoader extends ImageLoader {
    private static final String PIC_BASE = Constant.Url.AD_PIC;

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        /**
          常用的图片加载库：
          Universal Image Loader：一个强大的图片加载库，包含各种各样的配置，最老牌，使用也最广泛。      
          Picasso: Square出品，必属精品。和OkHttp搭配起来更配呦！          
          Volley ImageLoader：Google官方出品，可惜不能加载本地图片~          
          Fresco：Facebook出的，天生骄傲！不是一般的强大。         
          Glide：Google推荐的图片加载库，专注于流畅的滚动。
         */

        //Glide 加载图片简单用法
        Glide.with(context)
                .load(PIC_BASE + path)
                .placeholder(R.drawable.default_pic)
                .into(imageView);

        //Picasso 加载图片简单用法
//        Picasso.with(context).load(path).into(imageView);

        //用fresco加载图片简单用法
//        Uri uri = Uri.parse((String) path);
//        imageView.setImageURI(uri);
    }
    //提供createImageView 方法，如果不用,可以不重写这个方法，方便fresco自定义ImageView
//    @Override
//    public ImageView createImageView(Context context) {
//        SimpleDraweeView simpleDraweeView=new SimpleDraweeView(context);
//        return simpleDraweeView;
//    }
}