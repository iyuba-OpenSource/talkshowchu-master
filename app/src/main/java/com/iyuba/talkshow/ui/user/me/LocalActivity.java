package com.iyuba.talkshow.ui.user.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.iyuba.talkshow.databinding.ActivityLocalBinding;
import com.iyuba.talkshow.lil.help_fix.ui.collect.chapter.ChapterCollectActivity;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.base.MvpView;
import com.iyuba.talkshow.ui.deletlesson.LessonDeleteActivity;
import com.iyuba.talkshow.ui.rank.RankActivity;
import com.iyuba.talkshow.ui.user.me.dubbing.MyDubbingActivity;
import com.iyuba.talkshow.ui.words.WordNoteActivity;
import com.umeng.analytics.MobclickAgent;

public class LocalActivity extends BaseActivity implements MvpView {

    //布局样式
    private ActivityLocalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLocalBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar.listToolbar);
        activityComponent().inject(this);
        setClick();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    void clickDownload() {
        Intent intent = new Intent(mContext, RankActivity.class);
        startActivity(intent);
    }

    void clickCollect() {
//        Intent intent = new Intent(mContext, CollectionActivity.class);
//        startActivity(intent);
        ChapterCollectActivity.start(this);
    }

    void clickDubbing() {
        Intent intent = new Intent(mContext, MyDubbingActivity.class);
        startActivity(intent);
    }

    void startMyWors(){
        WordNoteActivity.start(this);
    }

    public void showToast(int resId) {
        Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
    }


    void setClick(){
        binding.meWordsRl.setOnClickListener(v -> startMyWors());
        binding.meDownloadBooksRl.setOnClickListener(v -> startDeleteDownloads());
        binding.meDubbingRl.setOnClickListener(v -> clickDubbing());
        binding.meCollectRl.setOnClickListener(v -> clickCollect());
        binding.meDownloadRl.setOnClickListener(v -> clickDownload());
    }

    private void startDeleteDownloads() {
        LessonDeleteActivity.start(this);
    }
}
