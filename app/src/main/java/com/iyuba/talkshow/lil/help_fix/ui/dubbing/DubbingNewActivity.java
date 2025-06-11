package com.iyuba.talkshow.lil.help_fix.ui.dubbing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.LayoutContainerBinding;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_mvp.base.BaseViewBindingActivity;
import com.iyuba.talkshow.newce.study.dubbingNew.DubbingNewFragment;
import com.iyuba.talkshow.ui.base.BaseActivity;

/**
 * 新的配音界面-容器界面
 */
public class DubbingNewActivity extends BaseActivity {

    public static void start(Context context,Voa voa){
        Intent intent = new Intent();
        intent.setClass(context,DubbingNewActivity.class);
        intent.putExtra(StrLibrary.data,voa);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_container);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initToolbar();
        initFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**************************初始化***********************/
    private void initToolbar(){

    }

    private void initFragment(){
        Voa tempVoa = getIntent().getParcelableExtra(StrLibrary.data);
        DubbingNewFragment newFragment = DubbingNewFragment.newInstance(tempVoa,true);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container,newFragment).show(newFragment).commitNowAllowingStateLoss();
    }
}
