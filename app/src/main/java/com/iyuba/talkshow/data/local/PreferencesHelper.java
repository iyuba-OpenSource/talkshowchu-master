package com.iyuba.talkshow.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iyuba.talkshow.injection.ApplicationContext;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PreferencesHelper {

    private static final String PREF_FILE_NAME = "kouyu_show_file";

    private static final String PREF_KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN";

    private final SharedPreferences mPref;
    private final Gson mGson;

    @Inject
    PreferencesHelper(@ApplicationContext Context context) {
        mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        mGson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz")
                .create();
    }

    public void clear() {
        mPref.edit().clear().apply();
    }

    public void putAccessToken(String accessToken) {
        mPref.edit().putString(PREF_KEY_ACCESS_TOKEN, accessToken).apply();
    }

    @Nullable
    public String getAccessToken() {
        return mPref.getString(PREF_KEY_ACCESS_TOKEN, null);
    }

    public void putBoolean(String key, boolean value) {
        mPref.edit().putBoolean(key, value).apply();
    }

    public boolean loadBoolean(String key, boolean defValue) {
        return mPref.getBoolean(key, defValue);
    }

    public void putInt(String key, int value) {
        mPref.edit().putInt(key, value).apply();
    }

    public int loadInt(String key, int defValue) {
        return mPref.getInt(key, defValue);
    }

    public void putFloat(String key, float value) {
        mPref.edit().putFloat(key, value).apply();
    }

    public float loadFloat(String key, float defValue) {
        return mPref.getFloat(key, defValue);
    }

    public void putLong(String key, long value) {
        mPref.edit().putLong(key, value).apply();
    }

    public long loadLong(String key, long defValue) {
        return mPref.getLong(key, defValue);
    }

    public void putString(String key, String value) {
        mPref.edit().putString(key, value).apply();
    }

    public String loadString(String key, String defValue) {
        return mPref.getString(key, defValue);
    }

    public void putObject(String name, Object value) throws IOException {
        // 把值对象以流的形式转化为字符串。
        if(value != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            String productBase64 = new String(Base64.encodeBase64(baos.toByteArray()));
            putString(name, productBase64);
            oos.close();
        }
    }

    public Object loadObject(String key) throws IOException, ClassNotFoundException {
        String objBase64String = loadString(key, null);
        Object obj = null;
        if(objBase64String != null) {
            byte[] b = Base64.decodeBase64(objBase64String.getBytes());
            InputStream bis = new ByteArrayInputStream(b);
            ObjectInputStream ois = new ObjectInputStream(bis); // something wrong
            obj = ois.readObject();
            ois.close();
        }
        return obj;
    }

    public void remove(String key) {
        mPref.edit().remove(key).apply();
    }

    public boolean isShowTip() { //播放文章播放器速度
        return loadBoolean("showtip", false);
    }

    public void setShowTip(boolean showtip) {
        putBoolean("showtip", showtip);
    }

}
