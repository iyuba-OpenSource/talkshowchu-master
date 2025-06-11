package com.iyuba.wordtest.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    public NetworkUtil() {
    }

    public static boolean isConnected(Context context) {
        if (context == null) {
            return false;
        } else {
            int var1 = context.checkCallingOrSelfPermission("android.permission.INTERNET");
            if (var1 == -1) {
                return false;
            } else {
                int var2 = context.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE");
                if (var2 == -1) {
                    return true;
                } else {
                    try {
                        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo info = manager.getActiveNetworkInfo();
                        return info.isConnected();
                    } catch (NullPointerException var5) {
                        return false;
                    }
                }
            }
        }
    }
}
