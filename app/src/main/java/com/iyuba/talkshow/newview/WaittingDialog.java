package com.iyuba.talkshow.newview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.newview.CustomDialog;

/**
 * 自定义等待窗口
 * 
 * @author ct
 * 
 */
public class WaittingDialog {
	/**
	 * 等待窗口
	 */
	public static CustomDialog showDialog(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View layout = inflater.inflate(R.layout.waitting_dialog, null,false);
		CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
		CustomDialog cDialog = customBuilder.setContentView(layout).create();
		
		cDialog.setCanceledOnTouchOutside(false);
		return cDialog;
	}






}
