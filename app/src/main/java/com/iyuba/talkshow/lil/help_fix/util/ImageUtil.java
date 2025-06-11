package com.iyuba.talkshow.lil.help_fix.util;

import android.widget.ImageView;

import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util;
import com.iyuba.talkshow.lil.help_mvp.util.svg.SvgUtil;

/**
 * @desction: 图片工具
 * @date: 2023/4/9 14:04
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class ImageUtil {

    //加载图片
    public static void loadImg(String urlOrPath, int placeId,ImageView imageView){
        Glide3Util.loadImg(ResUtil.getInstance().getContext(),urlOrPath,placeId,imageView);
    }

    //加载圆形图片
    public static void loadCircleImg(String urlOrPath, int placeId,ImageView imageView){
        Glide3Util.loadCircleImg(ResUtil.getInstance().getContext(), urlOrPath,placeId,imageView);
    }

    //加载圆角图片
    public static void loadRoundImg(String urlOrPath, int placeId,ImageView imageView){
        int radius = 10;//圆角默认为10

        Glide3Util.loadRoundImg(ResUtil.getInstance().getContext(), urlOrPath,placeId,radius,imageView);
    }

    //加载gif图片
    public static void loadGif(String urlOrPath, int placeId,ImageView imageView){
        Glide3Util.loadGif(ResUtil.getInstance().getContext(), urlOrPath,placeId,imageView);
    }

    //加载svg图片
    public static void loadSvg(int path, int placeId,ImageView imageView){
        SvgUtil.loadSvg(ResUtil.getInstance().getContext(), path,placeId,imageView);
    }

    public static void loadSvg(String url, int placeId,ImageView imageView){
        SvgUtil.loadSvg(ResUtil.getInstance().getContext(), url,placeId,imageView);
    }
}
