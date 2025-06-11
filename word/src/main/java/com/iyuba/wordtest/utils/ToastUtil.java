package com.iyuba.wordtest.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by carl shen on 2021/5/20
 * New Primary English, new study experience.
 */
public class ToastUtil {
    private static Toast toast;

    public static void show(Context context, String text) {
        if (toast == null) {
            //创建一个空的吐司
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        //给吐司的内容设置自己想要的值
        toast.setText(text);

        toast.show();//弹出吐司

    }
    /*
     * 单例安全的吐司
     */
    public static void showToast(Context context, String text) {

        try {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showLongToast(Context context, String text) {

        try {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

