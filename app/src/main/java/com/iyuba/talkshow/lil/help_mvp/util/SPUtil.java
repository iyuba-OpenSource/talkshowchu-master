package com.iyuba.talkshow.lil.help_mvp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Set;

/**
 * 缓存工具类
 */
public class SPUtil {

    //默认数据
    private static final String DEFAULT_STRING = "";
    private static final int DEFAULT_INT = 0;
    private static final float DEFAULT_FLOAT = 0F;
    private static final boolean DEFAULT_BOOLEAN = false;
    private static final long DEFAULT_LONG = 0L;
    private static final Set<String> DEFAULT_SET = null;

    //获取缓存类
    public static SharedPreferences getPreferences(Context context,String name){
        SharedPreferences preferences = context.getSharedPreferences(name,Context.MODE_PRIVATE);
        return preferences;
    }

    public static void putString(SharedPreferences preferences,String key,String value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String loadString(SharedPreferences preferences,String key,String defaultValue){
        if (TextUtils.isEmpty(defaultValue)){
            defaultValue = DEFAULT_STRING;
        }
        return preferences.getString(key,defaultValue);
    }

    public static void putInt(SharedPreferences preferences,String key,int value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int loadInt(SharedPreferences preferences,String key,int defaultValue){
        if (defaultValue == 0){
            defaultValue = DEFAULT_INT;
        }
        return preferences.getInt(key,defaultValue);
    }

    public static void putFloat(SharedPreferences preferences,String key,float value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static float loadFloat(SharedPreferences preferences,String key,float defaultValue){
        if (defaultValue==0){
            defaultValue = DEFAULT_FLOAT;
        }
        return preferences.getFloat(key,defaultValue);
    }

    public static void putBoolean(SharedPreferences preferences,String key,boolean value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean loadBoolean(SharedPreferences preferences,String key,boolean defaultValue){
        return preferences.getBoolean(key,defaultValue);
    }

    public static void putLong(SharedPreferences preferences,String key,long value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long loadLong(SharedPreferences preferences,String key,long defaultValue){
        if (defaultValue == 0){
            defaultValue = DEFAULT_LONG;
        }
        return preferences.getLong(key,defaultValue);
    }

    public static void putSet(SharedPreferences preferences,String key,Set<String> value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(key,value);
        editor.apply();
    }

    public static Set<String> loadSet(SharedPreferences preferences,String key){
        return preferences.getStringSet(key,DEFAULT_SET);
    }
}
