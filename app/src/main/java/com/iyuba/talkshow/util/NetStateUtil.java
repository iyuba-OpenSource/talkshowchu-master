package com.iyuba.talkshow.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.iyuba.talkshow.TalkShowApplication;

/**
 * Created by Administrator on 2016/12/14/014.
 */

public class NetStateUtil {
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            return cm != null && cm.getActiveNetworkInfo() != null
                    && cm.getActiveNetworkInfo().isAvailable();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * APN type: 0 #=> no network or unknown
     *           1 #=> mobile network
     *           2 #=> wifi network
     */
    public static int getAPNType() {
        int netType = 0;
        ConnectivityManager connMgr = (ConnectivityManager) TalkShowApplication
                .getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            netType = 1;
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = 2;
        }
        return netType;
    }

}