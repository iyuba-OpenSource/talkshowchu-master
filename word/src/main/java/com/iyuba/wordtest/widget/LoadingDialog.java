package com.iyuba.wordtest.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.iyuba.wordtest.R;
import com.iyuba.wordtest.databinding.DialogLoadingBinding;


/**
 * 自定义加载进度对话框
 * Created by Administrator on 2016-10-28.
 */

public class LoadingDialog extends Dialog {

    DialogLoadingBinding binding ;
    public LoadingDialog(Context context) {
        super(context, R.style.DialogTheme);
        /**设置对话框背景透明*/
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding = DialogLoadingBinding .inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setCanceledOnTouchOutside(false);

        binding.loadingTv.setText("正在加载~");
    }

    /**
     * 为加载进度个对话框设置不同的提示消息
     *
     * @param message 给用户展示的提示信息
     * @return build模式设计，可以链式调用
     */
    public LoadingDialog setMessage(String message) {
        binding.loadingTv.setText(message);
        return this;
    }


}
