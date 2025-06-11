package com.iyuba.talkshow.ui.courses.coursechoose;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.fragment.app.FragmentTransaction;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.databinding.ActivityCourseChooseBinding;
import com.iyuba.talkshow.ui.base.BaseActivity;

import javax.inject.Inject;


public class CourseKouActivity extends BaseActivity {

    public  static final String FLAG = "FLAG";

    //布局样式
    private ActivityCourseChooseBinding binding;

    @Inject
    ConfigManager configManager ;
//    private String[] titleid;
//    private String[] title;
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCourseChooseBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        activityComponent().inject(this);
        flag = getIntent().getIntExtra(FLAG,300);
        initTopBar();
        initFragment();
    }

    private void initTopBar() {
        binding.tvTopCenter.setText(getResources().getString(R.string.coureschoose));
        binding.imgTopLeft.setOnClickListener(v -> finish());
    }

    public static void  start(Context context , int flag ){
        Intent intent = new Intent( );
        intent.setClass(context, CourseKouActivity.class);
        intent.putExtra(FLAG,flag);
        context.startActivity(intent);
    }

    private void initFragment() {
        ChooseKouFragment fragment = ChooseKouFragment.build("" + configManager.getKouCategory(), flag);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);
        transaction.commit();
    }

}
