package com.iyuba.talkshow.ui.web;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.databinding.ActivityOfficialBinding;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.main.drawer.Share;
import com.iyuba.talkshow.ui.widget.divider.LinearItemDivider;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.wordtest.db.OfficialAccount;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by carl shen on 2021/6/7
 * New Primary English, new study experience.
 */
public class OfficialActivity extends BaseActivity implements OfficialMvpView {
    private static int PAGE_NUM = 1;
    private static final int PAGE_SIZE = 10;
    private int categoryId = 0;
    private boolean mIsNetLoad = false;
    @Inject
    OfficialAdapter mAdapter;
    @Inject
    OfficialPresenter mPresenter;
    ActivityOfficialBinding binding;

    OnOfficialClickListener mListener = new OnOfficialClickListener() {
        @Override
        public void onOfficialClick(OfficialAccount collect) {
            if (collect == null) {
                showToastShort("请检查内容是否正确。");
                return;
            }
            if (!ConfigData.openWxSmallShare) {
                ToastUtil.showToast(mContext, "对不起，分享暂时不支持");
                return;
            }
            if (Share.isWXSmallAvailable(mContext)) {
                IWXAPI api = WXAPIFactory.createWXAPI(mContext, ConfigData.wx_key);
                WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
                req.userName = ConfigData.wx_small_name;
                String minipath = String.format("/pages/gzhDetails/gzhDetails?id=%d", collect.id);
                Log.e("ShareSDK", "OfficialActivity MiniProgram path " + minipath);
                req.path = minipath;
                req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;
                api.sendReq(req);
            } else {
                showToastShort("您的手机暂时不支持跳转到微信小程序，谢谢！");
            }
        }
    };

    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context,OfficialActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOfficialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activityComponent().inject(this);
        setSupportActionBar(binding.officialToolbar);
        mPresenter.attachView(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mAdapter.setListener(mListener);
        binding.officialToolbar.setTitle("公众号列表");
        binding.officialRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.officialRecyclerView.setLayoutManager(layoutManager);
        LinearItemDivider divider = new LinearItemDivider(mContext, LinearItemDivider.VERTICAL_LIST);
        divider.setDivider(getResources().getDrawable(R.drawable.voa_ranking_divider));
        binding.officialRecyclerView.addItemDecoration(divider);
        binding.officialRecyclerView.setHasFixedSize(true);
        binding.officialRecyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.refreshLayout.setOnRefreshListener(refreshLayout -> refreshData());
        binding.refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                PAGE_NUM++;
                if (NetStateUtil.isConnected(mContext)) {
                    mIsNetLoad = true;
                    mPresenter.getMoreAccount(PAGE_NUM, PAGE_SIZE);
                } else {
                    if ((mAdapter.getItemCount() > 0) && (mAdapter.getItemData(mAdapter.getItemCount() -1) != null)) {
                        categoryId = mAdapter.getItemData(mAdapter.getItemCount() -1).id;
                    } else {
                        categoryId = 0;
                    }
                    Log.e("OfficialActivity", "getLocalAccount categoryId " + categoryId);
                    mPresenter.getLocalAccount(categoryId, PAGE_NUM, PAGE_SIZE);
                }
                refreshLayout.finishLoadMore(2000);
            }
        });
        showLoadingLayout();
        refreshData();
    }

    private void refreshData() {
        PAGE_NUM = 1;
        if (NetStateUtil.isConnected(mContext)) {
            mIsNetLoad = true;
            mPresenter.getOfficialAccount(PAGE_NUM, PAGE_SIZE);
            binding.refreshLayout.finishRefresh(2000);
        } else {
            if ((mAdapter.getItemCount() > 0) && (mAdapter.getItemData(mAdapter.getItemCount() -1) != null)) {
                categoryId = mAdapter.getItemData(mAdapter.getItemCount() -1).id;
            } else {
                categoryId = 0;
            }
            Log.e("OfficialActivity", "getLocalAccount categoryId " + categoryId);
            mPresenter.getLocalAccount(categoryId, PAGE_NUM, PAGE_SIZE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void showLoadingLayout() {
        binding.loadingLayout.getRoot().setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissLoadingLayout() {
        binding.loadingLayout.getRoot().setVisibility(View.GONE);
    }

    @Override
    public void setEmptyAccount() {
        binding.emptyView.getRoot().setVisibility(View.VISIBLE);
        binding.refreshLayout.setVisibility(View.GONE);
    }

    @Override
    public void setDataAccount(List<OfficialAccount> data) {
        Log.e("OfficialPresenter", " setDataAccount ");
        mAdapter.setData(data);
        mAdapter.notifyDataSetChanged();
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.refreshLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void setMoreAccount(List<OfficialAccount> data) {
        mAdapter.addData(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

}
