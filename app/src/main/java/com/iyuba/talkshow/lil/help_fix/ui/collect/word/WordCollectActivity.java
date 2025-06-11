package com.iyuba.talkshow.lil.help_fix.ui.collect.word;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.iyuba.talkshow.databinding.LayoutContainerTabTitleBinding;
import com.iyuba.talkshow.lil.help_fix.ui.collect.chapter.ChapterCollectActivity;
import com.iyuba.talkshow.lil.help_mvp.base.BaseViewBindingActivity;

/**
 * @title: 单词收藏界面
 * @date: 2023/7/17 16:37
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordCollectActivity extends BaseViewBindingActivity<LayoutContainerTabTitleBinding> {

    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context, WordCollectActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
