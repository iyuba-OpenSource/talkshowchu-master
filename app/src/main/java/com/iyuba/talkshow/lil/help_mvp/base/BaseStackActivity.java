package com.iyuba.talkshow.lil.help_mvp.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.iyuba.talkshow.lil.help_mvp.util.StackUtil;

/**
 * @title: 基础的堆栈activity
 * @date: 2023/11/29 11:12
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class BaseStackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StackUtil.getInstance().add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        StackUtil.getInstance().remove(this);
    }
}
