package com.iyuba.talkshow.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.ActivityPrewiewBookBinding;
import com.iyuba.talkshow.ui.base.BaseActivity;

/**
 * @desction:
 * @date: 2023/2/17 14:26
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class PreviewBookActivity extends BaseActivity {

    public static final String TITLE = "title";
    public static final String IMAGE = "image";

    //布局样式
    private ActivityPrewiewBookBinding binding;

    public static void start(Context context,String title,int resId){
        Intent intent = new Intent();
        intent.setClass(context,PreviewBookActivity.class);
        intent.putExtra(TITLE,title);
        intent.putExtra(IMAGE,resId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPrewiewBookBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        activityComponent().inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        String title = getIntent().getStringExtra(TITLE);
        int imageId = getIntent().getIntExtra(IMAGE,0);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        binding.title.setText(title);

        binding.image.setImageResource(imageId);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
