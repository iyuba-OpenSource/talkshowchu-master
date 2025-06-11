package com.iyuba.talkshow.lil.help_fix.view.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.iyuba.talkshow.databinding.DialogLoadingLilBinding;
import com.iyuba.talkshow.lil.help_fix.util.ScreenUtil;

/**
 * @title: 加载弹窗
 * @date: 2023/4/26 18:29
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class LoadingDialog extends AlertDialog {

    private Context context;
    private DialogLoadingLilBinding binding;

    public LoadingDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding = DialogLoadingLilBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());
        binding.progressMsg.setText("正在加载数据中～");
        setCanceledOnTouchOutside(false);
    }

    public void setMsg(String msg){
        binding.progressMsg.setText(msg);
    }

    @Override
    public void show() {
        super.show();

        int size = (int) (ScreenUtil.getScreenW(context)*0.5);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = size;
        lp.height = size;
        getWindow().setAttributes(lp);
    }
}
