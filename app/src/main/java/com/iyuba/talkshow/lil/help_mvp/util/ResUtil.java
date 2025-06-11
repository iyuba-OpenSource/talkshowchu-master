package com.iyuba.talkshow.lil.help_mvp.util;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

import java.io.InputStream;

/**
 * @desction: 资源类
 * @date: 2023/3/22 09:23
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class ResUtil {
    private static final String TAG = "ResUtil";

    private static ResUtil instance;

    public static ResUtil getInstance(){
        if (instance==null){
            synchronized (ResUtil.class){
                if (instance==null){
                    instance = new ResUtil();
                }
            }
        }
        return instance;
    }


    private Application application;

    //保存application
    public void setApplication(Application application){
        this.application = application;
    }

    //获取application
    public Application getApplication(){
        return application;
    }

    //获取上下文
    public Context getContext(){
        return application.getApplicationContext();
    }

    //获取图片
    public Drawable getDrawable(int resId){
        return ResourcesCompat.getDrawable(getContext().getResources(),resId,null);
    }

    //获取文本
    public String getString(int resId){
        return getContext().getResources().getString(resId);
    }

    //获取颜色
    public int getColor(int resId){
        return ResourcesCompat.getColor(getContext().getResources(),resId,null);
    }

    //获取数组
    public String[] getStrArray(int resId){
        return getContext().getResources().getStringArray(resId);
    }

    //获取assets中的json文件
    public String getAssetsFile(String jsonFileName){
        String jsonStr = null;

        try {
            InputStream inputStream = getContext().getResources().getAssets().open(jsonFileName);
            jsonStr = FileHelpUtil.convertSteamToString(inputStream);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return jsonStr;
        }
    }

    //获取raw中的文件

    //获取anim中的文件

    //获取xml中的文件
}
