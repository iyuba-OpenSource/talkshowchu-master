package com.iyuba.talkshow.ui.rank;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.ActivityRankBinding;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.main.drawer.Share;
import com.iyuba.talkshow.ui.rank.listen.RankListenFragment;
import com.iyuba.talkshow.ui.rank.oral.RankOralFragment;
import com.iyuba.talkshow.ui.rank.study.RankStudyFragment;
import com.iyuba.talkshow.ui.rank.test.RankTestFragment;
import com.iyuba.talkshow.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carl shen on 2021/7/26
 * New Primary English, new study experience.
 *
 * 排行榜
 */
public class RankActivity extends BaseActivity {

    private List<Fragment> list;
    private String shareUrl = "";

    //布局样式
    private ActivityRankBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRankBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        binding.listToolbar.listToolbar.setTitle(R.string.ranking);
        setSupportActionBar(binding.listToolbar.listToolbar);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initViewPager();
        setBackGround(0);
        initClick();
    }
    private void initViewPager() {
        list = new ArrayList<>();
        list.add(RankListenFragment.newInstance("听力"));
        list.add(RankOralFragment.newInstance("口语"));
        list.add(RankStudyFragment.newInstance("学习"));
        list.add(RankTestFragment.newInstance("测试"));

        RankAdapter rankAdapter = new RankAdapter(this,list);
        binding.viewPager.setAdapter(rankAdapter);
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setBackGround(position);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
    private void initClick() {
        binding.rankListen.setOnClickListener(v -> {
            binding.viewPager.setCurrentItem(0);
            setBackGround(0);
        });
        binding.rankSpeak.setOnClickListener(v -> {
            binding.viewPager.setCurrentItem(1);
            setBackGround(1);
        });
        binding.rankStudy.setOnClickListener(v -> {
            binding.viewPager.setCurrentItem(2);
            setBackGround(2);
        });
        binding.rankTest.setOnClickListener(v -> {
            binding.viewPager.setCurrentItem(3);
            setBackGround(3);
        });

        binding.share.setOnClickListener(v->{
            shareUserRankMsg();
        });
    }
    //设置选择框颜色
    private void setBackGround(int item) {
        binding.rankListen.setSelected(false);
        binding.rankListen.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.rankSpeak.setSelected(false);
        binding.rankSpeak.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.rankStudy.setSelected(false);
        binding.rankStudy.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.rankTest.setSelected(false);
        binding.rankTest.setTextColor(getResources().getColor(R.color.colorPrimary));
        switch (item) {
            case 0:
                binding.rankListen.setSelected(true);
                binding.rankListen.setTextColor(getResources().getColor(R.color.white));
                binding.rankListen.setTextSize(16);
                break;
            case 1:
                binding.rankSpeak.setSelected(true);
                binding.rankSpeak.setTextColor(getResources().getColor(R.color.white));
                binding.rankSpeak.setTextSize(16);
                break;
            case 2:
                binding.rankStudy.setSelected(true);
                binding.rankStudy.setTextColor(getResources().getColor(R.color.white));
                binding.rankStudy.setTextSize(16);
                break;
            case 3:
                binding.rankTest.setSelected(true);
                binding.rankTest.setTextColor(getResources().getColor(R.color.white));
                binding.rankTest.setTextSize(16);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(mContext);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(mContext);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    //获取当前的个人数据信息
    private void shareUserRankMsg(){
        if (list!=null){
            //日期信息
            Fragment curFragment = list.get(binding.viewPager.getCurrentItem());
            String shareMsg  = ((ShareMsgListener)curFragment).getShareMsg();
            String shareUrl = ((ShareMsgListener)curFragment).getShareUrl();
            Share.shareContentAndUrl(this,"我的排名情况",shareMsg,shareUrl);
        }else {
            ToastUtil.show(this,"未获取到排行信息，请重试");
        }
    }
}
