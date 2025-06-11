package com.iyuba.talkshow.lil.help_mvp.util;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * @desction: 动画工具
 * @date: 2023/4/5 00:36
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class AnimUtil {

    /**************平移动画**************/
    //左侧进入
    public static Animation leftEnter(long duration){
        Animation animation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_PARENT,-1,
                TranslateAnimation.RELATIVE_TO_PARENT,0,
                TranslateAnimation.RELATIVE_TO_PARENT,0,
                TranslateAnimation.RELATIVE_TO_PARENT,0);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        animation.setRepeatCount(1);
        return animation;
    }

    //左侧退出

    //右侧进入

    //右侧退出

    //上方进入

    //上方退出

    //下方进入

    //下方退出

    /**************渐显渐隐动画**************/
    //逐渐隐藏
    public static Animation hide(long duration){
        Animation animation = new AlphaAnimation(1f,0f);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        animation.setRepeatCount(1);
        return animation;
    }

    //逐渐显示
    public static Animation show(long duration){
        Animation animation = new AlphaAnimation(0f,1f);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        animation.setRepeatCount(1);
        return animation;
    }

    /**************旋转动画**************/



}
