package com.iyuba.talkshow.util;

import android.app.Activity;
import android.content.Context;
import android.view.Display;

/**
 * Created by holybible on 16/6/11.
 */
public class ScreenUtils {

    /**
     *
     * @param activity
     * @return int[0] => width, int[1] => height
     */
    public static int[] getScreenSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        return new int[]{display.getWidth(), display.getHeight()};
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
