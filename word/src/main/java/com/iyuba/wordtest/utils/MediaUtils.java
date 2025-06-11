package com.iyuba.wordtest.utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class MediaUtils {
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            return (cm != null) && (cm.getActiveNetworkInfo() != null)
                    && cm.getActiveNetworkInfo().isAvailable();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
