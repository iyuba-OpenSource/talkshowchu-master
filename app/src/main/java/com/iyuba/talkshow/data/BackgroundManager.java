//package com.iyuba.talkshow.data;
//
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.ServiceConnection;
//import android.os.IBinder;
//
//import com.iyuba.talkshow.TalkShowApplication;
//
//public class BackgroundManager {
//	private static BackgroundManager instance;
//	public static Context mContext;
//	public Background bindService;
//	public ServiceConnection conn;
//
//	private BackgroundManager() {
//		conn = new ServiceConnection() {
//			@Override
//			public void onServiceDisconnected(ComponentName name) {
//			}
//
//			@Override
//			public void onServiceConnected(ComponentName name, IBinder service) {
//				Background.MyBinder binder = (Background.MyBinder) service;
//				bindService = binder.getService();
//				bindService.init(mContext);
//			}
//		};
//	}
//
//	public static synchronized BackgroundManager Instace() {
//		mContext = TalkShowApplication.getContext();
//		if (instance == null) {
//			instance = new BackgroundManager();
//		}
//		return instance;
//	}
//}
