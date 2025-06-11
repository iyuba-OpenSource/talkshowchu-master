package com.iyuba.talkshow.lil.help_fix.manager;

import android.content.SharedPreferences;

import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.lil.help_mvp.util.SPUtil;

/**
 * @title: 学习管理-通用类型数据
 * @date: 2023/5/23 10:37
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class StudySettingManager {
    private static final String TAG = "StudySettingManager";

    private static StudySettingManager instance;

    public static StudySettingManager getInstance(){
        if (instance==null){
            synchronized (StudySettingManager.class){
                if (instance==null){
                    instance = new StudySettingManager();
                }
            }
        }
        return instance;
    }

    //存储的信息
    private static final String SP_NAME = TAG;

    private SharedPreferences preferences;

    private SharedPreferences getPreference(){
        if (preferences==null){
            preferences = SPUtil.getPreferences(ResUtil.getInstance().getContext(), SP_NAME);
        }
        return preferences;
    }


    /**************原文*************/
    private static final String SP_TEXT_TYPE = "textType";//文本显示类型
    private static final String SP_SYNC_MODE = "syncMode";//播放模式
    private static final String SP_ROLL_ENABLE = "rollEnable";//滚动是否开启

    public String getTextType(){
        return SPUtil.loadString(getPreference(),SP_TEXT_TYPE, TypeLibrary.TextShowType.ALL);
    }

    public void setTextType(String textType){
        SPUtil.putString(getPreference(),SP_TEXT_TYPE,textType);
    }

    public String getSyncMode(){
        return SPUtil.loadString(getPreference(),SP_SYNC_MODE,TypeLibrary.PlayModeType.ORDER_PLAY);
    }

    public void setSyncMode(String syncMode){
        SPUtil.putString(getPreference(),SP_SYNC_MODE,syncMode);
    }

    public boolean getRollOpen(){
        return SPUtil.loadBoolean(getPreference(),SP_ROLL_ENABLE,true);
    }

    public void setRollOpen(boolean isRoll){
        SPUtil.putBoolean(getPreference(),SP_ROLL_ENABLE,isRoll);
    }

    /********************阅读**********************/
    private static final String SP_READ_SHOW_LANGUAGE = "read_showLanguage";

    public String getReadShowLanguage(){
        return SPUtil.loadString(getPreference(),SP_READ_SHOW_LANGUAGE,TypeLibrary.TextShowType.EN);
    }

    public void setReadShowLanguage(String showLanguage){
        SPUtil.putString(getPreference(),SP_READ_SHOW_LANGUAGE,showLanguage);
    }
}
