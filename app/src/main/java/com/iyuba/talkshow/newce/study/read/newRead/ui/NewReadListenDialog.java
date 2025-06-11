package com.iyuba.talkshow.newce.study.read.newRead.ui;//package com.iyuba.talkshow.newce.study.read.newRead.ui;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//
//import com.iyuba.talkshow.databinding.FragmentReadStudyBinding;
//
//import org.greenrobot.eventbus.EventBus;
//
///**
// * @title:
// * @date: 2023/12/11 16:51
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// * @description:
// */
//public class NewReadListenDialog extends AlertDialog {
//
//    private FragmentReadStudyBinding binding;
//
//    protected NewReadListenDialog(@NonNull Context context) {
//        super(context);
//
//        binding = FragmentReadStudyBinding.inflate(LayoutInflater.from(context));
//        setContentView(binding.getRoot());
//
//        initData();
//        initClick();
//    }
//
//    private void initData(){
//
//    }
//
//    private void initClick(){
//        binding.imageClose.setOnClickListener(v->{
//            EventBus.getDefault().post(new NewReadListenEvent(NewReadListenEvent.type_closeReport));
//        });
//        binding.studyShare.setOnClickListener(v->{
//
//        });
//    }
//
//    @Override
//    public void show() {
//        super.show();
//
//
//    }
//
//
//    /**********************************其他操作************************/
//
//}
