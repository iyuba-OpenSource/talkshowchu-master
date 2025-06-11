package com.iyuba.talkshow.lil.help_fix.manager;

import android.content.SharedPreferences;

import com.iyuba.talkshow.lil.help_fix.data.AppConfig;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.lil.help_mvp.util.SPUtil;

/**
 * @title: 小说的书籍管理
 * @date: 2023/4/27 09:47
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NovelBookChooseManager {
    private static final String TAG = "NovelBookChooseManager";

    private static NovelBookChooseManager instance;

    public static NovelBookChooseManager getInstance(){
        if (instance==null){
            synchronized (NovelBookChooseManager.class){
                if (instance==null){
                    instance = new NovelBookChooseManager();
                }
            }
        }
        return instance;
    }

    //存储的信息
    private static final String SP_NAME = TAG;
    private static final String SP_BOOK_TYPE = "bookType";
    private static final String SP_BOOK_LEVEL = "bookLevel";
    private static final String SP_BOOK_ID = "bookId";
    private static final String SP_BOOK_NAME = "bookName";

    private SharedPreferences preferences;

    private SharedPreferences getPreference(){
        if (preferences==null){
            preferences = SPUtil.getPreferences(ResUtil.getInstance().getContext(), SP_NAME);
        }
        return preferences;
    }

    public String getBookType(){
        return SPUtil.loadString(getPreference(),SP_BOOK_TYPE, AppConfig.novel_type);
    }

    public void setBookType(String bookType){
        SPUtil.putString(getPreference(),SP_BOOK_TYPE,bookType);
    }

    public int getBookLevel(){
        return SPUtil.loadInt(getPreference(),SP_BOOK_LEVEL,AppConfig.novel_level);
    }

    public void setBookLevel(int bookLevel){
        SPUtil.putInt(getPreference(),SP_BOOK_LEVEL,bookLevel);
    }

    public String getBookId(){
        return SPUtil.loadString(getPreference(),SP_BOOK_ID,AppConfig.novel_id);
    }

    public void setBookId(String bookId){
        SPUtil.putString(getPreference(),SP_BOOK_ID,bookId);
    }

    public String getBookName(){
        return SPUtil.loadString(getPreference(),SP_BOOK_NAME,AppConfig.novel_name);
    }

    public void setBookName(String bookName){
        SPUtil.putString(getPreference(),SP_BOOK_NAME,bookName);
    }
}
