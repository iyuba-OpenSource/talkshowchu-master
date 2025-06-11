package com.iyuba.talkshow.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.DialogDownloadingBinding;

public class DownloadDialog extends Dialog {

    DialogDownloadingBinding binding ;
    CallBack callBack;


    public DownloadDialog(@NonNull Context context) {
        super(context);
    }

    public DownloadDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId );
    }

    protected DownloadDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogDownloadingBinding.inflate(getLayoutInflater());
        setCancelable(false);
        setContentView(binding.getRoot());
        setDialogStyle();
    }

    public void setProgress(int progress){
        binding.progress.setProgress(progress);
        binding.progresstv.setText(("正在下载资源文件,进度"+progress+"%"));
        binding.cancel.setOnClickListener(v -> callBack.onCancel());
    }

    private void setDialogStyle() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = getContext().getResources().getDisplayMetrics().widthPixels -100; // 宽度
        params.height = getContext().getResources().getDisplayMetrics().heightPixels /2; // 高度
        //lp.width = 650;
        getWindow().setAttributes(params);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void setCallback(CallBack callback) {
        this.callBack = callback;
    }

    @Override
    public void show() {
        super.show();
        setProgress(0);
    }

    public interface  CallBack{
        void onCancel ();
    }
}
