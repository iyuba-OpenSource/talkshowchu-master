package com.iyuba.talkshow.lil.junior.choose;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.fragment.app.FragmentTransaction;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.manager.AbilityControlManager;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.model.AppCheckResponse;
import com.iyuba.talkshow.databinding.ActivityCourseChooseBinding;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.iyuba.talkshow.util.RxUtil;
import com.tencent.vasdolly.helper.ChannelReaderUtil;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 中小学选书界面
 *
 * 这里默认使用课本的书籍id，因为合并之后会同时刷新两个界面，因此使用一个即可
 */
public class JuniorChooseActivity extends BaseActivity {

    @Inject
    DataManager mDataManager;
    @Inject
    ConfigManager configManager ;
    private JuniorChooseFragment fragment;

    //加载弹窗
    private LoadingDialog loadingDialog;

    //布局样式
    private ActivityCourseChooseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCourseChooseBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        activityComponent().inject(this);
        initTopBar();

        openDialog();

        //这里增加人教版处理的接口
        String channel = ChannelReaderUtil.getChannel(this);
        if (ConfigData.renVerifyCheck
                &&AbilityControlManager.getInstance().isLimitPep()){
            verifyCheck(channel);
        }else {
            closeDialog();

            AbilityControlManager.getInstance().setLimitPep(false);
            initFragment();
        }
    }

    private void initTopBar() {
        binding.tvTopCenter.setText(getResources().getString(R.string.coureschoose));
        binding.imgTopLeft.setOnClickListener(v -> finish());
    }

    public static void  start(Context context){
        Intent intent = new Intent( );
        intent.setClass(context, JuniorChooseActivity.class);
        context.startActivity(intent);
    }

    private void initFragment() {
        fragment = JuniorChooseFragment.build(String.valueOf(configManager.getCourseCategory()));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);
        transaction.commit();
    }

//    private void initSpinnner() {
//        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.course_page_drop,android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource
//                (android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                spinner.setSelection(position);
//                if (position == 0){
//                    titleid = titleidRenjiao ;
//                    title = titleRenjiao ;
//                }else {
//                    titleid  =titleidWaiyan ;
//                    title  =titleWaiyan ;
//                }
//                initListener();
//                setViewPager();
//            }

//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }


//    private void initSeries() {
//        titleidRenjiao =getResources().getStringArray(R.array.course_page_catagory_id_renjiao);
//        titleRenjiao =getResources().getStringArray(R.array.course_page_catagory_renjiao);
//        titleidWaiyan =getResources().getStringArray(R.array.course_page_catagory_id_waiyan);
//        titleWaiyan =getResources().getStringArray(R.array.course_page_catagory_waiyan);
//        titleid = titleidRenjiao ;
//        title = titleRenjiao ;
//    }

//    private void initListener() {
//        //TabLayout切换时导航栏图片处理
//        tabs.setupWithViewPager(viewPager);
//
//        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {//选中图片操作
//                        viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {//未选中图片操作
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//    }

//    private void setViewPager() {
//        viewPager.setOffscreenPageLimit(3);
//        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
//            @Override
//            public Fragment getItem(int i) {
//                return ChooseCourseFragment.build(titleid[i],flag);
//            }
//
//            @Override
//            public int getCount() {
//                return title.length;
//            }
//
//            @Nullable
//            @Override
//            public CharSequence getPageTitle(int position) {
//                return title[position];
//            }
//        });
//
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int i, float v, int i1) {
//
//            }
//
//            @Override
//            public void onPageSelected(int i) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int i) {
//
//            }
//        });
//    }

    //审核检查是否需要限制
    private Subscription mVerifyCheckSub;
    public void verifyCheck(String channel){
        int verifyRenId = ConfigData.getRenLimitChannelId(channel);

        RxUtil.unsubscribe(mVerifyCheckSub);
        mVerifyCheckSub = mDataManager.getAppCheckStatus(verifyRenId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AppCheckResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        closeDialog();
                        AbilityControlManager.getInstance().setLimitPep(true);

                        initFragment();
                    }

                    @Override
                    public void onNext(AppCheckResponse response) {
                        closeDialog();
                        if (response.getResult().equals("0")){
                            AbilityControlManager.getInstance().setLimitPep(false);
                        }else {
                            AbilityControlManager.getInstance().setLimitPep(true);
                        }

                        initFragment();
                    }
                });
    }

    //开启加载弹窗
    private void openDialog(){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(this);
            loadingDialog.setMessage("正在查询书本信息");
        }
        loadingDialog.show();
    }

    //关闭加载弹窗
    private void closeDialog(){
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxUtil.unsubscribe(mVerifyCheckSub);
    }
}
