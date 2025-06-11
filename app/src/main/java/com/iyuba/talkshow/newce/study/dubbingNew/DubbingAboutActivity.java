package com.iyuba.talkshow.newce.study.dubbingNew;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.ActivityDubbingAboutBinding;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.detail.ranking.RankingFragment;
import com.iyuba.talkshow.ui.detail.recommend.RecommendFragment;

/**
 * @desction:
 * @date: 2023/2/15 19:15
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class DubbingAboutActivity extends BaseActivity {

    private static final String TYPE = "type";
    private static final String VOA = "voa";
    private String type;
    private Voa mVoa;

    public static final String DUBBING_RANK = "配音排行";
    public static final String DUBBING_ALBUM = "相关专辑";

    //布局样式
    private ActivityDubbingAboutBinding binding;

    public static void start(Context context,String type,Voa voa){
        Intent intent = new Intent();
        intent.setClass(context,DubbingAboutActivity.class);
        intent.putExtra(TYPE,type);
        intent.putExtra(VOA,voa);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDubbingAboutBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        activityComponent().inject(this);
        type = getIntent().getStringExtra(TYPE);
        mVoa = getIntent().getParcelableExtra(VOA);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initToolbar();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        BaseFragment baseFragment = null;
        if (type.equals(DUBBING_RANK)){
            baseFragment = RankingFragment.newInstance(mVoa);
        }else if (type.equals(DUBBING_ALBUM)){
            baseFragment = RecommendFragment.newInstance(mVoa.voaId(),mVoa.category());
        }
        transaction.add(R.id.container,baseFragment).show(baseFragment).commitNow();
    }

    private void initToolbar(){
        binding.toolbar.tvTopCenter.setText(type);
        binding.toolbar.imgTopRight.setVisibility(View.INVISIBLE);
        binding.toolbar.imgTopLeft.setOnClickListener(v->{
            finish();
        });
    }
}
