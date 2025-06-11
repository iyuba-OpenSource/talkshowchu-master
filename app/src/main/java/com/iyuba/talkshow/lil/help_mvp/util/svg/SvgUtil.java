package com.iyuba.talkshow.lil.help_mvp.util.svg;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.ahmadrosid.svgloader.SvgLoader;

/**
 * @desction: svg工具类
 * @date: 2023/4/7 11:07
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class SvgUtil {

    //加载svg
    public static void loadSvg(Context context, String urlOrPath, int placeId,ImageView imageView){
        SvgLoader.pluck().with((Activity) context)
                .setPlaceHolder(placeId,placeId)
                .load(urlOrPath,imageView);
    }

    //加载svg
    public static void loadSvg(Context context,int rawId,int placeId,ImageView imageView){
        String prefix = "android.resource://com.ahmadrosid.androidsvgloader/";
        loadSvg(context,prefix+rawId,placeId,imageView);
    }

    //销毁
    public static void destroy(){
        SvgLoader.pluck().close();
    }
}
