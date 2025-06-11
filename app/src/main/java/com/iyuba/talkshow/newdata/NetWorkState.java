package com.iyuba.talkshow.newdata;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.iyuba.talkshow.TalkShowApplication;


public class NetWorkState {

	public static boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) TalkShowApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}
		return false;
	}

	public static int getAPNType() {
		int netType = 0;
		ConnectivityManager connMgr = (ConnectivityManager) TalkShowApplication.getInstance()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			if (nType == TelephonyManager.NETWORK_TYPE_GPRS
					|| nType == TelephonyManager.NETWORK_TYPE_CDMA
					|| nType == TelephonyManager.NETWORK_TYPE_EDGE) {// 2G
				netType = 1;
			} else {
				netType = 2;
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = 2;
		}
		return netType;
	}
}
