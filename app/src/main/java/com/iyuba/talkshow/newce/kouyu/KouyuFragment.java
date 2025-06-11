package com.iyuba.talkshow.newce.kouyu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.imooclib.ui.mobclass.MobClassActivity;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.manager.AbilityControlManager;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.model.AdNativeResponse;
import com.iyuba.talkshow.data.model.CategoryFooter;
import com.iyuba.talkshow.data.model.LoopItem;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.FragmentKouyuBinding;
import com.iyuba.talkshow.event.KouBookEvent;
import com.iyuba.talkshow.injection.PerFragment;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.courses.coursechoose.CourseKouActivity;
import com.iyuba.talkshow.ui.courses.coursechoose.OpenFlag;
import com.iyuba.talkshow.ui.detail.DetailActivity;
import com.iyuba.talkshow.ui.sign.SignActivity;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.iyuba.talkshow.ui.widget.divider.MainGridItemDivider;
import com.iyuba.talkshow.util.AdInfoFlowUtil;
import com.iyuba.talkshow.util.DialogFactory;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.wordtest.ui.WordStepActivity;
import com.mob.secverify.SecVerify;
import com.umeng.analytics.MobclickAgent;
import com.youdao.sdk.nativeads.NativeResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import personal.iyuba.personalhomelibrary.event.UserNameChangeEvent;

/**
 * Created by carl shen on 2020/7/30
 * New Primary English, new study experience.
 */
@PerFragment
public class KouyuFragment extends BaseFragment implements KouyuMvpView {
    private static final String IS_BACK_TO_HOME = "back_home";
    private static final String SHOW_ITEM = "show_item";
    public static final String TAG = "KouyuFragment";
    public static final int SPAN_COUNT = 2;
    private boolean ifresh = false;
    FragmentKouyuBinding binding ;

    int pageNum = 1;

    AdInfoFlowUtil adInfoFlowUtil;

    @Inject
    KouyuPresenter mContainerPresenter;
    @Inject
    KouyuAdapter mVoaListAdapter;
    @Inject
    ConfigManager configManager;
    @Inject
    DataManager dataManager;
    private final int defaultUi = 1;
    private long starttime;
    KouyuAdapter.VoaCallback voaCallback = new KouyuAdapter.VoaCallback() {
        @Override
        public void onVoaClick(Voa voa) {
            if (!UserInfoManager.getInstance().isLogin()) {
                Boolean isVerifySupport = SecVerify.isVerifySupport();
                startLogin();
                return;
            }
            if (!UserInfoManager.getInstance().isVip()&&voa.series()>204&&!dataManager.isTrial(voa)){
                ToastUtil.showToast(mContext, "该课程为VIP专属，请开通VIP后重试");
            }else {
                startDetailActivity(voa);
            }
        }
    };
    KouyuAdapter.LoopCallback loopCallback = new KouyuAdapter.LoopCallback() {
        @Override
        public void onLoopClick(int voaId) {
            mContainerPresenter.getVoaById(voaId);
        }
    };

    KouyuAdapter.WordBookCallBack wordBookCallBack = new KouyuAdapter.WordBookCallBack() {
        @Override
        public void onBookClick() {
            if (!UserInfoManager.getInstance().isLogin()) {
                ToastUtil.showToast(mContext, "请登录使用");
                return;
            }
            mContext.startActivity(WordStepActivity.buildIntent(mContext, configManager.getKouId(), configManager.getKouTitle()));
        }
    };

    KouyuAdapter.DataChangeCallback adapterDataRefreshCallback = new KouyuAdapter.DataChangeCallback() {
        @Override
        public void onClick(View v, CategoryFooter category, int limit, String ids) {
            mContainerPresenter.loadMoreVoas(category, limit, ids);
        }
    };
    View.OnClickListener dailyBonusCallback = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (UserInfoManager.getInstance().isLogin()) {
                Intent intent = new Intent(mContext, SignActivity.class);
                startActivity(intent);
            } else {
                startLogin();
            }
        }
    };
    private LoadingDialog mLoadingDialog;
    private boolean isAtFront;
    private String curTitle;

    public KouyuFragment() {
    }

    public static KouyuFragment build(int showItem, boolean isBackToHome) {
        KouyuFragment fragment  = new KouyuFragment() ;
        Bundle bundle  = new Bundle( );
        bundle.putBoolean(IS_BACK_TO_HOME, isBackToHome);
        bundle.putInt(SHOW_ITEM, showItem);
        fragment.setArguments(bundle);
        return fragment ;
    }

    @Override
    public void onStart() {
        super.onStart();
        ifresh = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
        mContainerPresenter.attachView(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentKouyuBinding.inflate(getLayoutInflater(),container,false);
        return binding.getRoot();
    }

    private void initAdFlow() {
        adInfoFlowUtil = new AdInfoFlowUtil(mContext, UserInfoManager.getInstance().isVip(), ads -> {
            try {
                AdNativeResponse nativeResponse = new AdNativeResponse();
                nativeResponse.setNativeResponse((NativeResponse) ads.get(0));
                if (!UserInfoManager.getInstance().isVip()) {
                    mVoaListAdapter.setAd(nativeResponse);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).setAdRequestSize(1);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLoadingDialog();
        EventBus.getDefault().register(this);
        try {
            //限制广告
            if (!AdBlocker.getInstance().shouldBlockAd()){
                initAdFlow();
            }
        } catch (Exception var2) { }
//        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
//        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(mContext));
//        mContainerPresenter.getAllWords();
//        mContainerPresenter.getSeriesses();
        mContainerPresenter.loadVoas(configManager.getKouId());
//        mContainerPresenter.loadLoop();
        mVoaListAdapter.setVoaCallback(voaCallback);
        mVoaListAdapter.setLoopCallback(loopCallback);
        mVoaListAdapter.setmWordBookCallback(wordBookCallBack);
        mVoaListAdapter.setDataChangeCallback(adapterDataRefreshCallback);
        mVoaListAdapter.setDailyBonusCallback(dailyBonusCallback);
        mVoaListAdapter.setVip(UserInfoManager.getInstance().isVip());

        //人教版审核限制
        if (AbilityControlManager.getInstance().isLimitPep()){
            binding.refreshWord.setVisibility(View.INVISIBLE);
        }else {
            binding.refreshWord.setVisibility(View.VISIBLE);
        }

        binding.refreshLayout.setOnRefreshListener(refreshLayout -> refreshData());
        binding.recyclerView.setAdapter(mVoaListAdapter);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 2;
            }
        });
        binding.recyclerView.addItemDecoration(new MainGridItemDivider(getContext()));
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。

        binding.refreshWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseKouActivity.start(getActivity(), OpenFlag.FINISH);
            }
        });
        if (App.APP_MOC_BOTTOM) {
            binding.titleMoc.setVisibility(View.GONE);
        } else {
            binding.titleMoc.setVisibility(View.VISIBLE);
            binding.titleMoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<Integer> typeIdFilter = new ArrayList<>();
//                    typeIdFilter.add(-2);//全部
//                    typeIdFilter.add(-1);//最新
//                    typeIdFilter.add(2);//四级
//                    typeIdFilter.add(3);//VOA
//                    typeIdFilter.add(4);//六级
//                    typeIdFilter.add(7);//托福
//                    typeIdFilter.add(8);//考研
//                    typeIdFilter.add(9);//BBC
//                    typeIdFilter.add(21);//新概念
//                    typeIdFilter.add(22);//走遍美国
//                    typeIdFilter.add(28);//学位
//                    typeIdFilter.add(52);//考研二
//                    typeIdFilter.add(61);//雅思
//                    typeIdFilter.add(91);//中职
                    typeIdFilter.add(Constant.PRODUCT_ID);//初中
//                    typeIdFilter.add(25);//小学
                    startActivity(MobClassActivity.buildIntent(mContext, Constant.PRODUCT_ID, true, typeIdFilter));
                }
            });
        }

        //设置标题
        setTitleText();
    }

    private void setTitleText() {
        String courseTitle = configManager.getKouTitle();
        if (TextUtils.isEmpty(courseTitle)) {
            curTitle = App.getBookDefaultShowData().getBookName();
            binding.titleWord.setText(curTitle);
        } else {
            curTitle = courseTitle;
            binding.titleWord.setText(curTitle);
        }
        if (curTitle.contains("(")) {
            curTitle = curTitle.substring(0, curTitle.indexOf("("));
        } else if (curTitle.contains("（")) {
            curTitle = curTitle.substring(0, curTitle.indexOf("（"));
        }
        mVoaListAdapter.setBookTitle(curTitle);
        mVoaListAdapter.notifyDataSetChanged();
    }

    private void initLoadingDialog() {
        mLoadingDialog = new LoadingDialog(mContext);
    }

    private void refreshData() {
        pageNum = 1;
        if (adInfoFlowUtil != null) {
            adInfoFlowUtil.reset();
        }
        Log.e("KouyuFragment", "refreshData configManager.getKouId() " + configManager.getKouId());
        mContainerPresenter.getVoaSeries(configManager.getKouId());
        ifresh = true;
        binding.refreshLayout.finishRefresh(2000);
    }
    public void onClickReload() {
        mContainerPresenter.loadVoas(configManager.getKouId());
//        mContainerPresenter.loadLoop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshBookWords(KouBookEvent event) {
        Log.e("KouyuFragment", "refreshBookWords currBookId " + event.bookId);
        if (event.sync) {
            refreshData();
        } else {
            mContainerPresenter.loadVoas(event.bookId);
        }
        setTitleText();
    }

    @Override
    public void onPause() {
        super.onPause();
        isAtFront = false;
        MobclickAgent.onPause(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        isAtFront = true;
        mVoaListAdapter.setVip(UserInfoManager.getInstance().isVip());
        MobclickAgent.onResume(getContext());
    }

    @Subscribe
    public void resetUserNickName(UserNameChangeEvent event) {
        UserInfoManager.getInstance().getRemoteUserInfo(UserInfoManager.getInstance().getUserId(), null);
    }

    @Override
    public void onStop() {
        super.onStop();
        ifresh = true;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mContainerPresenter.detachView();

        super.onDestroy();
    }


    @Override
    public void showVoas(List<Voa> voas) {
        if ((voas == null) || (voas.size() < 1)) {
            Log.e("KouyuFragment", "showVoas need reload voas. ");
//            onClickReload();
//            return;
        }
        mVoaListAdapter.setVoas(voas);
        mVoaListAdapter.notifyDataSetChanged();

        if (!UserInfoManager.getInstance().isVip()){
            if (adInfoFlowUtil != null) {
                adInfoFlowUtil.refreshAd();
            }
        }
    }

    @Override
    public void showVoasByCategory(List<Voa> voas, CategoryFooter category) {
        mVoaListAdapter.setVoasByCategory(voas, category);
    }

    @Override
    public void showMoreVoas(List<Voa> voas) {
        if ((voas == null) || (voas.size() < 1)) {
            Log.e("KouyuFragment", "showMoreVoas is null. ");
//            return;
        }
        mVoaListAdapter.setVoas(voas);
        mVoaListAdapter.notifyDataSetChanged();
        if (!UserInfoManager.getInstance().isVip()){
            if (adInfoFlowUtil != null) {
                adInfoFlowUtil.refreshAd();
            }
        }
    }

    @Override
    public void showLoadingDialog() {
        mLoadingDialog.show();
    }

    @Override
    public void dismissLoadingDialog() {
        mLoadingDialog.dismiss();
    }

    @Override
    public void showVoasEmpty() {
        mVoaListAdapter.setVoas(Collections.emptyList());
        mVoaListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(getContext(), getString(R.string.error_loading)).show();
    }

    @Override
    public void setBanner(List<LoopItem> loopItemList) {
        mVoaListAdapter.setBanner(loopItemList);
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startDetailActivity(Voa voa) {
        if (voa != null) {
            if (!ifresh) {
                startActivity(DetailActivity.buildIntent(getContext(), voa, true));
            }
            ifresh = false;
        }
    }

    @Override
    public void dismissRefreshingView() {
        binding.refreshLayout.finishRefresh();
        binding.refreshLayout.finishLoadMore();
    }

    //跳转到登陆界面
    private void startLogin(){
        NewLoginUtil.startToLogin(getActivity());
    }
}
