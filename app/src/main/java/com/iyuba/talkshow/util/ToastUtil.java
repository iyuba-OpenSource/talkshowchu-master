package com.iyuba.talkshow.util;

import android.content.Context;
import android.widget.Toast;

/**
 * @author yq QQ:1032006226
 * @name talkshow
 * @class name：com.iyuba.talkshow.util
 * @class describe
 * @time 2018/6/7 18:56
 * @change
 * @chang time
 * @class describe
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

