package com.iyuba.talkshow.lil.help_fix.view.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.iyuba.talkshow.databinding.DialogSingleButtonBinding;
import com.iyuba.talkshow.lil.help_fix.util.ScreenUtil;

/**
 * @title: 单按钮弹窗
 * @date: 2023/5/14 19:39
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class SingleButtonDialog extends AlertDialog {

    private Context context;
    private DialogSingleButtonBinding binding;

    public SingleButtonDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding = DialogSingleButtonBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());
        setCancelable(false);
    }

    @Override
    public void show() {
        super.show();

        int width = ScreenUtil.getScreenW(context);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (width*0.8);
        getWindow().setAttributes(lp);
    }

    //设置标题
    public void setTitle(String title){
        binding.title.setText(title);
    }

    //设置按钮
    public void setButton(String text,OnSingleClickListener listener){
        binding.agree.setText(text);
        binding.agree.setOnClickListener(v->{
            dismiss();

            if (listener!=null){
                listener.onClick();
            }
        });
    }

    //设置信息
    public void setMsg(String content){
        binding.content.setText(content);
    }

    //设置回调
    public interface OnSingleClickListener{
        void onClick();
    }
}
