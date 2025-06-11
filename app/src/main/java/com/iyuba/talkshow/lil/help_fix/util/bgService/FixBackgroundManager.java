//package com.iyuba.talkshow.lil.help_fix.util.bgService;
//
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.ServiceConnection;
//import android.os.IBinder;
//
//import com.iyuba.talkshow.TalkShowApplication;
//
//public class FixBackgroundManager {
//	private static FixBackgroundManager instance;
//	public static Context mContext;
//	public FixBackground bindService;
//	public ServiceConnection conn;
//
//	private FixBackgroundManager() {
//		conn = new ServiceConnection() {
//			@Override
//			public void onServiceDisconnected(ComponentName name) {
//			}
//
//			@Override
//			public void onServiceConnected(ComponentName name, IBinder service) {
//				FixBackground.MyBinder binder = (FixBackground.MyBinder) service;
//				bindService = binder.getService();
//				bindService.init(mContext);
//			}
//		};
//	}
//
//	public static synchronized FixBackgroundManager Instace() {
//		mContext = TalkShowApplication.getContext();
//		if (instance == null) {
//			instance = new FixBackgroundManager();
//		}
//		return instance;
//	}
//}
