package com.iyuba.talkshow.newdata;

import android.content.Context;

/**
 * Created by carl shen on 2020/8/3
 * New Primary English, new study experience.
 */
public class ResourceUtil {


    public static String getString(Context context, int id) {
        String str = "";
        str = context.getResources().getString(id);
        return str;
    }


}
