package com.iyuba.talkshow.newdata;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.iyuba.talkshow.TalkShowApplication;


/**
 * Created by carl shen on 2020/8/3
 * New Primary English, new study experience.
 */
public class SPconfig {
    private volatile static SPconfig instance;

    public static final String CONFIG_NAME = "config";

    private final Context context;

    private SharedPreferences.Editor editor;

    private SharedPreferences preferences;

    public static SPconfig Instance() {
        if (instance == null) {
            synchronized (SPconfig.class) {
                if (instance == null) {
                    instance = new SPconfig();
                }
            }
        }
        return instance;
    }

    private SPconfig() {
        this.context = TalkShowApplication.getInstance();
        openEditor();
    }

    // 创建或修改配置文件
    public void openEditor() {
        int mode = Activity.MODE_PRIVATE;
        preferences = context.getSharedPreferences(CONFIG_NAME, mode);
        editor = preferences.edit();
    }



    public void putBoolean(String name, boolean value) {

        editor.putBoolean(name, value);
        editor.commit();
    }

    public void putFloat(String name, float value) {

        editor.putFloat(name, value);
        editor.commit();
    }

    public void putInt(String name, int value) {
        editor.putInt(name, value);
        editor.commit();
    }

    public void putLong(String name, long value) {
        editor.putLong(name, value);
        editor.commit();
    }

    public void putString(String name, String value) {
        editor.putString(name, value);
        editor.commit();
    }

    public boolean loadBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public boolean loadBoolean(String key, boolean defaultBool) {
        return preferences.getBoolean(key, defaultBool);
    }

    public float loadFloat(String key) {
        return preferences.getFloat(key, 0);
    }

    public float loadFloat(String key, float defaultfloat) {
        return preferences.getFloat(key, defaultfloat);
    }

    public int loadInt(String key) {
        return preferences.getInt(key, 0);
    }

    public int loadInt(String key, int defaultInt) {
        return preferences.getInt(key, defaultInt);
    }

    public long loadLong(String key) {
        return preferences.getLong(key, 0);
    }

    public String loadString(String key) {
        return preferences.getString(key, "");
    }

    public String loadString(String key, String defaultString) {
        return preferences.getString(key, defaultString);
    }

    public void removeKey(String key) {
        editor.remove(key);
        editor.commit();
    }
}
