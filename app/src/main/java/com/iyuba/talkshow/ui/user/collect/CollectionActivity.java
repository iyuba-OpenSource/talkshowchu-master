//package com.iyuba.talkshow.ui.user.collect;
//
//import android.os.Bundle;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Toast;
//
//import com.iyuba.talkshow.Constant;
//import com.iyuba.talkshow.R;
//import com.iyuba.talkshow.data.BackgroundManager;
//import com.iyuba.talkshow.data.DataManager;
//import com.iyuba.talkshow.data.model.Collect;
//import com.iyuba.talkshow.data.model.Voa;
//import com.iyuba.talkshow.databinding.ActivityCollectionBinding;
//import com.iyuba.talkshow.newce.study.StudyActivity;
//import com.iyuba.talkshow.newdata.MyIjkPlayer;
//import com.iyuba.talkshow.ui.base.BaseActivity;
//import com.umeng.analytics.MobclickAgent;
//
//import java.util.List;
//
//import javax.inject.Inject;
//
//public class CollectionActivity extends BaseActivity implements CollectionMvpView {
//
//    @Inject
//    CollectionAdapter mAdapter;
//    @Inject
//    CollectionPresenter mPresenter;
//    @Inject
//    DataManager dataManager;
//
//    OnCollectionClickListener mListener = new OnCollectionClickListener() {
//        @Override
//        public void onCollectionClick(Collect collect) {
////            if (collect.getVoa().series()!=0){
////                Intent intent =  SeriesActivity.getIntent(CollectionActivity.this, String.valueOf(collect.getVoa().series()), String.valueOf(collect.getVoa().category()));
////                startActivity(intent);
////            }else {
//            if ((collect == null) || (collect.getVoa() == null)) {
//                showToastShort("请先同步课程原文，才能跳转到相应的页面。");
//                return;
//            }
//            Voa voa = collect.getVoa();
//            //这里和列表中的操作统一起来
//            int unitId = dataManager.getUnitId4Voa(voa);
//            if (unitId>0){
//                voa.UnitId = unitId;
//            }
//            //修改跳转位置(这里根据要求，全部跳转到文章中，因为文章比口语秀多，避免出现问题)
//            startActivity(StudyActivity.buildIntent(mContext, voa, 0, voa.UnitId,true,false,-1));
////            if (voa.hotFlag() < 1) {
////                startActivity(StudyActivity.buildIntent(mContext, voa, 0, 0,true));
////                return;
////            }
////                Intent intent = DetailActivity.buildIntent(CollectionActivity.this, collect.getVoa(), false);
////                startActivity(intent);
////            }
//
//        }
//    };
//     ActivityCollectionBinding binding;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityCollectionBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        activityComponent().inject(this);
//        setSupportActionBar(binding.collectionToolbar);
//        mPresenter.attachView(this);
//        mAdapter.setListener(mListener);
//        binding.collectionRecyclerView.setAdapter(mAdapter);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        binding.collectionRecyclerView.setLayoutManager(layoutManager);
//        showLoadingLayout();
//        mPresenter.getCollection();
//        binding.editTv.setText("还没有收藏文章");
//        binding.editTv.setOnClickListener(v -> onEditClick());
//        binding.deleteBtn.setOnClickListener(v -> onDeleteClick());
//    }
//
////    @OnClick(R.id.edit_tv)
//    public void onEditClick() {
//        if (mAdapter.getMode() == Mode.SHOW) {
//            binding.editTv.setText(getString(R.string.cancel));
//            mAdapter.setMode(Mode.EDIT);
//            mAdapter.notifyDataSetChanged();
//            binding.deleteBtn.setVisibility(View.VISIBLE);
//        } else {
//            binding.editTv.setText(getString(R.string.edit));
//            mAdapter.setMode(Mode.SHOW);
//            mAdapter.notifyDataSetChanged();
//            binding.deleteBtn.setVisibility(View.GONE);
//        }
//    }
//
////    @OnClick(R.id.delete_btn)
//    public void onDeleteClick() {
//        List<String> selectedIds = mAdapter.getSelection();
//        if (selectedIds.size() > 0) {
//            showLoadingLayout();
//            mPresenter.deleteCollection(selectedIds);
//        } else {
//            showToast(R.string.select_nothing);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        MobclickAgent.onPause(this);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        MobclickAgent.onResume(this);
//
//        //这里停止音频播放
//        if (Constant.PlayerService) {
//            if (BackgroundManager.Instace().bindService == null) {
//                return;
//            }
//            MyIjkPlayer myIjkPlayer = BackgroundManager.Instace().bindService.getPlayer();
//            if (myIjkPlayer == null) {
//                return;
//            }
//            if (myIjkPlayer.isPlaying()) {
//                myIjkPlayer.pause();
//            }
//
//            //关闭后台
//            BackgroundManager.Instace().bindService.stopForeground(true);
//        }
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            finish();
//        }
//        return true;
//    }
//
//    @Override
//    public void showLoadingLayout() {
//        binding.loadingLayout.getRoot().setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void dismissLoadingLayout() {
//        binding.loadingLayout.getRoot().setVisibility(View.GONE);
//    }
//
//    @Override
//    public void setAdapterEmpty() {
//        binding.emptyView.getRoot().setVisibility(View.VISIBLE);
//        binding.collectionRecyclerView.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void setAdapterData(List<Collect> data) {
//        if (data != null && data.size()>0) {
//            binding.editTv.setText("编辑");
//        }
//        mAdapter.setData(data);
//        mAdapter.notifyDataSetChanged();
//        binding.emptyView.getRoot().setVisibility(View.GONE);
//        binding.collectionRecyclerView.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void showToast(int resId) {
//        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
//    }
//
//}
