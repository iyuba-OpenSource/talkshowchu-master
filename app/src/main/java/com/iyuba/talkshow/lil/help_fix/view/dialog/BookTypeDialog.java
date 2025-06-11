package com.iyuba.talkshow.lil.help_fix.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.talkshow.databinding.DialogMsgListBinding;
import com.iyuba.talkshow.lil.help_fix.data.listener.OnSimpleClickListener;
import com.iyuba.talkshow.lil.help_fix.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @title:  书籍类型弹窗
 * @date: 2023/5/9 09:22
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class BookTypeDialog extends AlertDialog {

    private Context context;
    private DialogMsgListBinding binding;

    private BookTypeAdapter msgAdapter;

    public BookTypeDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogMsgListBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());
        binding.bottomLayout.setVisibility(View.GONE);

        msgAdapter = new BookTypeAdapter(context,new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(context);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(msgAdapter);
        msgAdapter.setListener(new OnSimpleClickListener<Pair<String, String>>() {
            @Override
            public void onClick(Pair<String, String> pair) {
                dismiss();

                if (listener!=null){
                    listener.onClick(pair);
                }
            }
        });
    }

    //刷新标题
    public void setTitle(String title){
        binding.title.setText(title);
    }

    //刷新内容
    public void setData(String bookType,List<Pair<String,String>> list){
        msgAdapter.refreshData(bookType,list);
    }

    //刷新内容
    public void setType(String bookType){
        msgAdapter.refreshType(bookType);
    }

    @Override
    public void show() {
        super.show();

        int width = ScreenUtil.getScreenW(context);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (width*0.8);
        getWindow().setAttributes(lp);
    }

    //回调
    private OnSimpleClickListener<Pair<String,String>> listener;

    public void setListener(OnSimpleClickListener<Pair<String,String>> listener) {
        this.listener = listener;
    }
}
