package com.iyuba.talkshow.ui.deletlesson;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.model.SeriesData;
import com.iyuba.talkshow.databinding.ActivityLessonDeleteBinding;
import com.iyuba.talkshow.event.MyBookEvent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.inject.Inject;

public class LessonDeleteActivity extends BaseActivity implements LessonDeleteMVPView{

    @Inject
    LessonDeletePresenter deletePresenter ;
    LessonDeleteAdapter adapter ;

    //布局样式
    private ActivityLessonDeleteBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLessonDeleteBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar.listToolbar);
        activityComponent().inject(this);
        deletePresenter .attachView(this);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        adapter = new LessonDeleteAdapter();
        adapter.setCourseCallback((SeriesData data) -> {
            deletePresenter.putCourseId(Integer.parseInt(data.getId()), data.getSeriesName());
            TalkShowApplication.getSubHandler().post(() -> {
                EventBus.getDefault().post(new MyBookEvent(Integer.parseInt(data.getId()), 0));
            });
            finish();
        });
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(adapter);
        setAnimation();
        deletePresenter.getDownloadedClass();
    }

    public static void start(Context context ){
        Intent intent = new Intent();
        intent.setClass(context,LessonDeleteActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lesson_delete, menu);
        return true;
    }

    private void setAnimation(){
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDuration(1000);
        defaultItemAnimator.setRemoveDuration(8000);
        binding.recycler.setItemAnimator(defaultItemAnimator);
    }

    @Override
    public void showDeleteMessage(String message) {
        ToastUtil.showToast(mContext, message);
    }

    @Override
    public void showBookList(List<SeriesData> seriesData) {
        adapter.setBookList(seriesData);
    }

    private void delete(List<String> checkList){

        for (String bookId : checkList){
            deletePresenter.deleteLessons(Integer.parseInt(bookId), String.valueOf(UserInfoManager.getInstance().getUserId()));
        }
        deletePresenter.getDownloadedClass();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }else if (item.getItemId() == R.id.delete){
            if (adapter.getCheckList().size()>0){
                delete(adapter.getCheckList());
            }
        } else if (item.getItemId() == R.id.refresh){
            deletePresenter.getDownloadedClass();
        }
        //增加说明信息
        else if (item.getItemId() == R.id.attention){
            new AlertDialog.Builder(mContext)
                    .setMessage("当前页面显示的为已下载的课程数据，如一年级上(新起点)，课程内容需要完全下载才能展示。\n长按选中的课程，点击删除按钮即可删除相应的课程。")
                    .setNegativeButton("确定",null)
                    .show();
        }
        return true;
    }


}
