package com.iyuba.wordtest.wxapi;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @desction: word中用于存储app的数据
 * @date: 2023/2/8 18:17
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class WordAppData {

    private static WordAppData instance;
    private Context context;

    public static WordAppData getInstance(Context context){
        if (instance==null){
            synchronized (WordAppData.class){
                if (instance==null){
                    instance = new WordAppData(context);
                }
            }
        }
        return instance;
    }

    public WordAppData(Context context){
        this.context = context;
    }

    //数据信息
    private static final String SP_NAME = "word_app_data";
    private static final String APP_ID = "APP_ID";
    private static final String APP_NAME_CN = "APP_NAME_CN";
    private static final String APP_NAME_EN = "APP_NAME_EN";
    private static final String APP_SHARE_URL = "APP_SHARE_URL";
    private static final String APP_LOGO_URL = "APP_LOGO_URL";

    //课程信息
    private static final String BOOK_NAME = "BOOK_NAME";

    private SharedPreferences preferences;

    private void openEditor(){
        if (preferences==null){
            preferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        }
    }

    public void putAppId(int value){
        openEditor();

        preferences.edit().putInt(APP_ID,value).commit();
    }

    public int getAppId(){
        openEditor();

        return preferences.getInt(APP_ID,0);
    }

    public void putAppNameEn(String value){
        openEditor();

        preferences.edit().putString(APP_NAME_EN,value).commit();
    }

    public String getAppNameEn(){
        openEditor();

        return preferences.getString(APP_NAME_EN,"");
    }

    public void putAppNameCn(String value){
        openEditor();

        preferences.edit().putString(APP_NAME_CN,value).commit();
    }

    public String getAppNameCn(){
        openEditor();

        return preferences.getString(APP_NAME_CN,"");
    }

    public void putAppShareUrl(String value){
        openEditor();

        preferences.edit().putString(APP_SHARE_URL,value).commit();
    }

    public String getAppShareUrl(){
        openEditor();

        return preferences.getString(APP_SHARE_URL,"");
    }

    public void putAppLogoUrl(String value){
        openEditor();

        preferences.edit().putString(APP_LOGO_URL,value).commit();
    }

    public String getAppLogoUrl(){
        openEditor();

        return preferences.getString(APP_LOGO_URL,"");
    }

    public void putBookName(String value){
        openEditor();

        preferences.edit().putString(BOOK_NAME,value).commit();
    }

    public String getBookName(){
        openEditor();

        return preferences.getString(BOOK_NAME,"");
    }
}
