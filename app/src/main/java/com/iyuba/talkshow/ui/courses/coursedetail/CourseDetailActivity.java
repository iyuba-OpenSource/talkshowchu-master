package com.iyuba.talkshow.ui.courses.coursedetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.ActivityCourseDetailBinding;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.courses.coursechoose.CourseChooseActivity;
import com.iyuba.talkshow.ui.courses.coursechoose.OpenFlag;

import java.util.List;

import javax.inject.Inject;


public class CourseDetailActivity extends BaseActivity implements CourseDetailMVPView{

    @Inject
    CourseDetailPresenter mPresenter ;

    CourseDetailAdapter adapter ;
    public static final String SERIES = "series";
    public static final String TITLE = "TITLE";

    private int series ;
    private String  title ;
    @Inject
    ConfigManager configManager;
    ActivityCourseDetailBinding binding ;

//    @OnClick(R.id.change_book)
    public void changeBook(){
        finish();
        configManager.putCourseId(0);
        configManager.putCourseTitle("");
        CourseChooseActivity.start(mContext, OpenFlag.TO_DETAIL, OpenFlag.TO_DETAIL);
    }

    public static Intent buildIntent(Context context , int series,String title){
        Intent intent = new Intent( context , CourseDetailActivity.class);
        intent.putExtra(SERIES,series);
        intent.putExtra(TITLE,title);
        return intent ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCourseDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activityComponent().inject(this);
        setSupportActionBar(binding.toolbar);
        series = getIntent().getIntExtra(SERIES,212);
        title = getIntent().getStringExtra(TITLE);
        binding.toolbar.setTitle("");
        binding.title.setText(title);
        mPresenter.attachView(this);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CourseDetailAdapter();
        binding.recycler.setAdapter(adapter);
        mPresenter.getVoas(series);
        binding.changeBook.setOnClickListener(v -> changeBook());
    }


    @Override
    public void showCourses(List<Voa> voas) {
        adapter.setVoas(voas);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change:
                finish();
                configManager.putCourseId(0);
                configManager.putCourseTitle("");
                startActivity(new Intent(mContext, CourseChooseActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
