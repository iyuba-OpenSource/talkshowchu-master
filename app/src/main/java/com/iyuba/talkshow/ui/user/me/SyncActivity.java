package com.iyuba.talkshow.ui.user.me;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.module.commonvar.CommonVars;
import com.iyuba.sdk.other.NetworkUtil;
import com.iyuba.talkshow.BuildConfig;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.model.VoaSoundNew;
import com.iyuba.talkshow.databinding.ActivitySyncBinding;
import com.iyuba.talkshow.event.SyncDataEvent;
import com.iyuba.talkshow.http.Http;
import com.iyuba.talkshow.http.HttpCallback;
import com.iyuba.talkshow.lil.help_fix.manager.NetHostManager;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.newdata.RetrofitUtils;
import com.iyuba.talkshow.newdata.UpdateEvalDataApi;
import com.iyuba.talkshow.newdata.UpdateEvalDataBean;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.deletlesson.LessonDeleteActivity;
import com.iyuba.talkshow.ui.widget.DownloadDialog;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.iyuba.talkshow.util.DensityUtil;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.QRCodeEncoder;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.entity.BookLevels;
import com.iyuba.wordtest.entity.NewBookLevels;
import com.iyuba.wordtest.manager.WordManager;
import com.iyuba.wordtest.utils.PermissionDialogUtil;
import com.umeng.analytics.MobclickAgent;
import com.yd.saas.common.util.MediaUrlUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 设置界面
 * Created by carl shen on 2021/4/8
 * New Primary English, new study experience.
 */
public class SyncActivity extends BaseActivity implements SyncMvpView {

    @Inject
    SyncPresenter mPresenter;
    @Inject
    ConfigManager configManager;
    @Inject
    DataManager mDataManager;
    private DownloadDialog downloadDialog ;
    private static AlertDialog downAlert = null;

    //布局样式
    private ActivitySyncBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySyncBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar.listToolbar);
        activityComponent().inject(this);
        mPresenter.attachView(this);
        EventBus.getDefault().register(this);
        setClick();

        hostSwitch();
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
        mPresenter.detachView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    public void showToast(int resId) {
        Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
    }


    private String courseTitle;
    void setClick() {
        courseTitle = configManager.getCourseTitle();
        if (TextUtils.isEmpty(courseTitle)){
            courseTitle = "";
        }else {
            courseTitle = "["+courseTitle+"]";
        }

        binding.downloadResource.setOnClickListener(v -> clickSyncResource());
        binding.syncEval.setOnClickListener(v -> {
            new AlertDialog.Builder(mContext)
                    .setMessage("该账号下当前课程"+courseTitle+"的评测和配音记录将从云端同步到本地，请确认是否进行同步?")
                    .setNegativeButton("不需要",null)
                    .setPositiveButton("同步", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            clickSyncEval();
                        }
                    }).show();
        });
        binding.syncFavor.setOnClickListener(v -> {
            new AlertDialog.Builder(mContext)
                    .setMessage("该账号下当前课程"+courseTitle+"的课程收藏记录将从云端同步到本地，请确认是否进行同步?")
                    .setNegativeButton("不需要",null)
                    .setPositiveButton("同步", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            clickSyncFavor();
                        }
                    }).show();
        });
        binding.syncStudy.setOnClickListener(v -> {
            new AlertDialog.Builder(mContext)
                    .setMessage("该账号下当前课程"+courseTitle+"的听力学习记录将从云端同步到本地，请确认是否进行同步?")
                    .setNegativeButton("不需要",null)
                    .setPositiveButton("同步", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            clickSyncStudy();
                        }
                    }).show();
        });
        binding.syncText.setOnClickListener(v -> {
            new AlertDialog.Builder(mContext)
                    .setMessage("当前课程"+courseTitle+"的所有课程原文将进行更新，请确认是否进行同步?")
                    .setNegativeButton("不需要",null)
                    .setPositiveButton("同步", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            clickSyncText();
                        }
                    }).show();
        });
        //隐藏二维码
        if (BuildConfig.DEBUG) {
            binding.ivChinese.setVisibility(View.GONE);
            binding.tvCreate.setVisibility(View.GONE);
        } else {
            binding.ivChinese.setVisibility(View.GONE);
            binding.tvCreate.setVisibility(View.GONE);
        }
        binding.tvCreate.setOnClickListener(v -> clickCreateQrcode());
        binding.meDownloadBooks.setOnClickListener(v -> LessonDeleteActivity.start(mContext));
        binding.reportCheckbox.setChecked(configManager.isStudyReport());
        binding.reportCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                configManager.setStudyReport(isChecked);
            }
        });
        if (App.APP_CHECK_PERMISSION) {
            binding.setPermission.setVisibility(View.VISIBLE);
            binding.setPermission.setOnClickListener(v -> {
                Log.e("SyncActivity", "clickPermission " + "package:" + mContext.getPackageName());
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                mContext.startActivity(intent);
            });
        } else {
            binding.setPermission.setVisibility(View.GONE);
        }
        if (App.APP_CHECK_AGREE) {
            binding.setAgree.setVisibility(View.VISIBLE);
            binding.agreeCheckbox.setChecked(configManager.isCheckAgree());
            binding.agreeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        configManager.setCheckAgree(true);
                        showToastShort("您已经同意使用协议及隐私政策，请正常使用本应用。");
                        TalkShowApplication.initUMMob();
                    } else {
                        new AlertDialog.Builder(mContext).setTitle(getString(R.string.alert_title))
                                .setMessage("如果您不同意使用协议及隐私政策，本应用使用功能受限。")
                                .setPositiveButton(getString(R.string.permission_cancel),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                configManager.setCheckAgree(false);
                                                binding.agreeCheckbox.setChecked(configManager.isCheckAgree());
                                            }
                                        })
                                .setNeutralButton(getString(R.string.permission_ok),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                binding.agreeCheckbox.setChecked(configManager.isCheckAgree());
                                            }})
                                .setCancelable(false)
                                .show();
                    }
                }
            });
        } else {
            binding.setAgree.setVisibility(View.GONE);
        }

        //增加域名更新
        binding.hostUpdate.setOnClickListener(v->{clickHostUpdate();});
    }

    private void clickCreateQrcode() {
        TalkShowApplication.getSubHandler().
                post(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = QRCodeEncoder.syncEncodeQRCode("Wang visits here.", DensityUtil.dp2px(mContext, 150), Color.BLACK, Color.WHITE, null);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.ivChinese.setImageBitmap(bitmap);
                            }
                        });
                    }
                }
        );
    }

    void clickSyncResource() {
        if (UserInfoManager.getInstance().isLogin()) {
            if (UserInfoManager.getInstance().isVip()) {
                //显示下载弹窗
                List<Pair<String, Pair<String,String>>> pairList = new ArrayList<>();
                pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE,new Pair<>("存储权限","用于下载并保存当前课程的音视频内容")));

                PermissionDialogUtil.getInstance().showMsgDialog(this, pairList, new PermissionDialogUtil.OnPermissionResultListener() {
                    @Override
                    public void onGranted(boolean isSuccess) {
                        if (isSuccess){
                            showDownloadDialog(configManager.getCourseId());
                        }
                    }
                });
            } else {
                showToastShort("只有VIP用户才能下载全部课程资源。");
            }
        } else {
            showToastShort("想下载课程资源，请先注册登录。");
        }
    }
    @Override
    public void startDownload(int bookId) {
        downloadDialog = new DownloadDialog(SyncActivity.this);
        downloadDialog.setCallback(new DownloadDialog.CallBack() {
            @Override
            public void onCancel() {
                mPresenter.cancelDownload();
                downloadDialog.dismiss();
                showToastShort("下载任务已取消");
            }
        });
        downloadDialog.show();

        mPresenter.SyncVoaTextStudyRecord4Book(false, false);
        TalkShowApplication.getSubHandler().post(new Runnable() {
            @Override
            public void run() {
                mPresenter.getWordsById(bookId);
            }
        });
    }
    /**
     * @param downloadEvent 接受下载事件并处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadFinish(SyncDataEvent downloadEvent) {
        switch (downloadEvent.status) {
            case SyncDataEvent.Status.FINISH:
                /* 全部下载完成 */
                if (downloadEvent.downloadId == 0) {
                    updateBookDb();
                    showToastShort("下载完成！");
                    if (downloadDialog != null) {
                        downloadDialog.dismiss();
                    }
                } else {
                    showToastShort("您的课程资源已经下载完成，无需重复下载。");
                    if (downloadDialog != null) {
                        downloadDialog.dismiss();
                    }
                }
                break;
            case SyncDataEvent.Status.DOWNLOADING:
                if (downloadEvent.downloadId == 1000){
                    if (downloadDialog != null) {
                        downloadDialog.setProgress(Integer.parseInt(downloadEvent.msg));
                    }
                }
                break;
            case SyncDataEvent.Status.ERROR:
                mPresenter.cancelDownload();
                showToastShort("您已经下载部分资源，由于系统繁忙，请稍后再继续下载！");
                if (downloadDialog != null) {
                    downloadDialog.dismiss();
                }
                break;
            case SyncDataEvent.Status.START:
                startDownload(downloadEvent.downloadId);
                break;
            default:
                break;
        }
    }
    private void showDownloadDialog(int bookId) {
        if (!NetStateUtil.isConnected(TalkShowApplication.getInstance())) {
            ToastUtil.showToast(mContext, "下载课程视频资源，需要打开网络数据连接");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("是否需要下载首页课程的课文，音频，视频及单词全部课程资源？该过程可能需要耗费一些流量及时间，最好打开Wifi下载。");
        builder.setNegativeButton("不需要", (dialog, which) -> { dialog.dismiss(); downAlert = null; });
        builder.setPositiveButton("下载", (dialog, which) -> { dialog.dismiss(); downAlert = null;
//            mPresenter.startFileDownload(bookId, false);
            TalkShowApplication.getSubHandler().post(new Runnable() {
                @Override
                public void run() {
                    mPresenter.startFileDownload(bookId, false);
                }
            });
//            mPresenter.SyncVoaTextStudyRecord4Book(false, false);
//            mPresenter.getWordsById(bookId);
        }).setCancelable(false);
        if (downAlert != null) {
            Log.e("MainFragment", "showDownloadDialog bookId = " + bookId);
            return;
        }
        downAlert = builder.create();
        downAlert.show();
    }
    private void updateBookDb() {
        TalkShowApplication.getSubHandler().post(new Runnable() {
            @Override
            public void run() {
//                WordDataBase.getInstance(TalkShowApplication.getInstance()).getBookLevelDao().updateBookDownload(bookId, 1);
                int bookId = configManager.getCourseId();
                Log.e("MeFragment", "updateBookDb bookId = " + bookId);
                if (WordManager.WordDataVersion == 2) {
                    NewBookLevels levels = WordDataBase.getInstance(TalkShowApplication.getInstance()).getNewBookLevelDao().getBookLevel(bookId, String.valueOf(UserInfoManager.getInstance().getUserId()));
                    if (levels == null) {
                        levels = new NewBookLevels(bookId, 0, 0, 0, String.valueOf(UserInfoManager.getInstance().getUserId()));
                        levels.download = 1;
                        WordDataBase.getInstance(TalkShowApplication.getInstance()).getNewBookLevelDao().saveBookLevel(levels);
                    } else {
                        levels.download = 1;
                        WordDataBase.getInstance(TalkShowApplication.getInstance()).getNewBookLevelDao().updateBookLevel(levels);
                    }
                    return;
                }
                BookLevels levels = WordDataBase.getInstance(TalkShowApplication.getInstance()).getBookLevelDao().getBookLevel(bookId);
                if (levels == null) {
                    levels = new BookLevels(bookId, 0, 0, 0);
                    levels.download = 1;
                    WordDataBase.getInstance(TalkShowApplication.getInstance()).getBookLevelDao().saveBookLevel(levels);
                } else {
                    levels.download = 1;
                    WordDataBase.getInstance(TalkShowApplication.getInstance()).getBookLevelDao().updateBookLevel(levels);
                }
            }
        });
    }
    void clickSyncStudy() {
        if (!UserInfoManager.getInstance().isLogin()) {
            NewLoginUtil.startToLogin(mContext);
            showToastShort("想同步学习记录，请先注册登录。");
            return;
        }
        if (!NetStateUtil.isConnected(TalkShowApplication.getInstance())) {
            ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.please_check_network));
            return;
        }
        if (binding.syncStudy.isClickable()) {
            binding.syncStudy.setClickable(false);
        } else {
            ToastUtil.showToast(mContext, "不要重复点击，正在同步中。");
            return;
        }
        showLoadingDialog();
        mPresenter.SyncVoaTextStudyRecord4Book(true, false);
        mPresenter.SyncMicroStudyRecord();
    }
    void clickSyncEval() {
        if (!UserInfoManager.getInstance().isLogin()) {
            NewLoginUtil.startToLogin(mContext);
            showToastShort("想同步学习记录，请先注册登录。");
            return;
        }
        if (!NetStateUtil.isConnected(TalkShowApplication.getInstance())) {
            ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.please_check_network));
            return;
        }
        if (binding.syncEval.isClickable()) {
            binding.syncEval.setClickable(false);
        } else {
            ToastUtil.showToast(mContext, "不要重复点击，正在同步中。");
            return;
        }
        showLoadingDialog();
        UpdateEvalDataApi updateEvalData = RetrofitUtils.getInstance().getApiService(UpdateEvalDataApi.BASEURL, UpdateEvalDataApi.class);
        updateEvalData.getVoaTestRecord(UpdateEvalDataApi.url, String.valueOf(UserInfoManager.getInstance().getUserId()), Constant.EVAL_TYPE).enqueue(new Callback<UpdateEvalDataBean>() {
            @Override
            public void onResponse(Call<UpdateEvalDataBean> call, Response<UpdateEvalDataBean> response) {
                dismissLoadingDialog();
                showToastShort("同步成功！");
                if (response == null) {
                    Log.e("MeFragment", "onResponse is null.");
                    return;
                }
                Log.e("MeFragment", "onResponse " + response);
                TalkShowApplication.getSubHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        UpdateEvalDataBean bean = response.body();
                        if ((bean == null) || (bean.getData() == null)) {
                            return;
                        }
                        Log.e("MeFragment", "onResponse size: " + bean.getSize());
                        for (UpdateEvalDataBean.DataBean child : bean.getData()) {
//                            Log.e("MeFragment", "onResponse child = " + child.toString());
                            VoaSoundNew voaSound = VoaSoundNew.builder()
                                    .setItemid(Long.parseLong(child.getNewsid() + "" + child.getParaid()+""+child.getIdindex()))
                                    .setUid(child.getUserid())
                                    .setVoa_id(child.getNewsid())
                                    .setTotalscore((int)(Float.parseFloat(child.getScore()) * 20.0))
                                    .setWordscore("")
                                    .setFilepath("")
                                    .setTime("" + System.currentTimeMillis())
                                    .setSound_url(child.getUrl())
                                    .setWords("")
                                    .setRvc("")
                                    .build();
                            boolean result = mDataManager.saveVoaSoundNew(voaSound);
                            Log.e("MeFragment", "onResponse save = " + result);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<UpdateEvalDataBean> call, Throwable t) {
                dismissLoadingDialog();
                Log.e("MeFragment", "评测数据更新失败" + t.getMessage());
            }
        });
    }
    void clickSyncFavor() {
        if (!UserInfoManager.getInstance().isLogin()) {
            NewLoginUtil.startToLogin(mContext);
            showToastShort("想同步学习记录，请先注册登录。");
            return;
        }
        if (!NetStateUtil.isConnected(TalkShowApplication.getInstance())) {
            ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.please_check_network));
            return;
        }
        if (binding.syncFavor.isClickable()) {
            binding.syncFavor.setClickable(false);
        } else {
            ToastUtil.showToast(mContext, "不要重复点击，正在同步中。");
            return;
        }
        showLoadingDialog();
        mPresenter.SyncCollection();
    }
    void clickSyncText() {
        if (!NetStateUtil.isConnected(TalkShowApplication.getInstance())) {
            ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.please_check_network));
            return;
        }
        if (binding.syncText.isClickable()) {
            binding.syncText.setClickable(false);
        } else {
            ToastUtil.showToast(mContext, "不要重复点击，正在同步中。");
            return;
        }
        showLoadingDialog();
//        mPresenter.SyncVoaBook();
        mPresenter.SyncVoaTextStudyRecord4Book(false, true);
    }

    void clickHostUpdate(){
        if (!NetworkUtil.isConnected(mContext)){
            ToastUtil.showToast(mContext,"请链接网络重试");
            return;
        }

        showLoadingDialog();

        /*String url = "https://raw.githubusercontent.com/OldManLi-1996/OldManLi-1996/main/" + App.APP_ID;
        Http.get(url, new HttpCallback() {
            @Override
            public void onSucceed(okhttp3.Call call, String response) {
                try {
                    Log.e("WelcomeActivity", "github response = " + response);
                    DomainBean bean = new Gson().fromJson(response, DomainBean.class);
                    if (!TextUtils.isEmpty(bean.domain) && !bean.domain.equalsIgnoreCase(configManager.getDomain())) {
                        configManager.setDomain(bean.domain);
                    }
                    if (!TextUtils.isEmpty(bean.short1) && !bean.short1.equalsIgnoreCase(configManager.getDomainShort())) {
                        configManager.setDomainShort(bean.short1);
                        CommonVars.domain = bean.short1;
                        Constant.Web.WEB_SUFFIX = bean.short1 + "/";
                        Log.e("WelcomeActivity", "github response update domain = " + Constant.Web.WEB_SUFFIX);
                        setDomainShort();
                    }
                    if (!TextUtils.isEmpty(bean.short2) && !bean.short2.equalsIgnoreCase(configManager.getDomainLong())) {
                        configManager.setDomainLong(bean.short2);
                        CommonVars.domainLong = bean.short2;
                        Constant.Web.WEB2_SUFFIX = bean.short2 + "/";
                        Log.e("WelcomeActivity", "github response update domainLong = " + CommonVars.domainLong);
                        setDomainLong();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoadingDialog();
                            ToastUtil.showToast(mContext,"更新服务成功");
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoadingDialog();
                            ToastUtil.showToast(mContext,"更新服务失败");
                        }
                    });

                    if (e != null) {
                        Log.e("WelcomeActivity", "github response Exception = " + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(okhttp3.Call call, Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                        ToastUtil.showToast(mContext,"更新域名失败");
                    }
                });

                if (e != null) {
                    Log.e("WelcomeActivity", "github onError = " + e.getMessage());
                }
            }
        });*/

        String url = "http://111.198.52.105:8085/api/getDomain.jsp?appId="+ App.APP_ID+"&short1="+configManager.getDomainShort()+"&short2="+configManager.getDomainLong();
        Http.get(url, new HttpCallback() {
            @Override
            public void onSucceed(okhttp3.Call call, String response) {
                try {
                    HostUpdate hostUpdate = new Gson().fromJson(response, HostUpdate.class);
                    if ((hostUpdate.result == 201 && hostUpdate.updateflg==1)
                            ||(hostUpdate.result == 200 && hostUpdate.updateflg == 0)){
                        if (!TextUtils.isEmpty(hostUpdate.short1) && !hostUpdate.short1.equalsIgnoreCase(configManager.getDomainShort())){
                            configManager.setDomainShort(hostUpdate.short1);
                            CommonVars.domain = hostUpdate.short1;
                            Constant.Web.WEB_SUFFIX = hostUpdate.short1 + "/";
                            setDomainShort();

                            //设置广告域名
                            MediaUrlUtils.setBaseUrl("http://"+NetHostManager.getInstance().getDomainShort());
                        }

                        if (!TextUtils.isEmpty(hostUpdate.short2) && !hostUpdate.short2.equalsIgnoreCase(configManager.getDomainLong())){
                            configManager.setDomainLong(hostUpdate.short2);
                            CommonVars.domainLong = hostUpdate.short2;
                            Constant.Web.WEB2_SUFFIX = hostUpdate.short2 + "/";
                            setDomainLong();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToastShort("服务更新成功");
                                dismissLoadingDialog();

                                //更新共通模块
                                IHeadline.resetMseUrl();
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToastShort("服务更新失败--"+hostUpdate.result+"--"+hostUpdate.updateflg);
                                dismissLoadingDialog();
                            }
                        });
                    }
                }catch (Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToastShort("服务更新失败");
                            dismissLoadingDialog();
                        }
                    });
                }
            }

            @Override
            public void onError(okhttp3.Call call, Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToastShort("服务更新失败");
                        dismissLoadingDialog();
                    }
                });
            }
        });
    }

    private LoadingDialog mLoadingDialog;
    @Override
    public void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(mContext);
        }
        mLoadingDialog.show();
    }

    @Override
    public void dismissLoadingDialog() {
        if (!binding.syncStudy.isClickable()) {
            binding.syncStudy.setClickable(true);
        }
        if (!binding.syncEval.isClickable()) {
            binding.syncEval.setClickable(true);
        }
        if (!binding.syncFavor.isClickable()) {
            binding.syncFavor.setClickable(true);
        }
        if (!binding.syncText.isClickable()) {
            binding.syncText.setClickable(true);
        }
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    class HostUpdate{
        private int result;
        private int updateflg;
        private String short1;
        private String short2;
    }
    class DomainBean {
        public String domain;
        public String short1;
        public String short2;
    }
    private void setDomainShort() {
        // set domain for update
        RetrofitUrlManager.getInstance().putDomain(Constant.DOMAIN_STATIC, Constant.HTTP_STATIC + CommonVars.domain);
        RetrofitUrlManager.getInstance().putDomain(Constant.DOMAIN_STATIC2, Constant.HTTP_STATIC2 + CommonVars.domain);
        RetrofitUrlManager.getInstance().putDomain(Constant.DOMAIN_AI, Constant.HTTP_AI + CommonVars.domain.replace("/","")+":9001/");
        RetrofitUrlManager.getInstance().putDomain(Constant.DOMAINS_AI, Constant.HTTPS_AI + CommonVars.domain.replace("/","")+":9001/");
        RetrofitUrlManager.getInstance().putDomain(Constant.DOMAIN_USERSPEECH, Constant.HTTP_USERSPEECH + CommonVars.domain.replace("/","")+":9001/");
        RetrofitUrlManager.getInstance().putDomain(Constant.DOMAIN_WORD, Constant.HTTP_WORD + CommonVars.domain);
        RetrofitUrlManager.getInstance().putDomain(Constant.DOMAIN_M, Constant.HTTP_M + CommonVars.domain);
        RetrofitUrlManager.getInstance().putDomain(Constant.DOMAIN_STATICVIP, Constant.HTTP_STATICVIP + CommonVars.domain);
        RetrofitUrlManager.getInstance().putDomain(Constant.DOMAIN_APP,  Constant.HTTP_APP + CommonVars.domain);
        RetrofitUrlManager.getInstance().putDomain(Constant.DOMAIN_APPS, Constant.HTTP_APPS + CommonVars.domain);
        RetrofitUrlManager.getInstance().putDomain(Constant.DOMAIN_VOA, Constant.HTTP_VOA + CommonVars.domain);
        RetrofitUrlManager.getInstance().putDomain(Constant.DOMAIN_DEV, Constant.HTTP_DEV + CommonVars.domain);
        RetrofitUrlManager.getInstance().putDomain(Constant.DOMAIN_CMS, Constant.HTTP_CMS + CommonVars.domain);
        RetrofitUrlManager.getInstance().putDomain(Constant.DOMAIN_API,  Constant.HTTP_API + CommonVars.domain);
        RetrofitUrlManager.getInstance().putDomain(Constant.DOMAIN_DAXUE, Constant.HTTP_DAXUE + CommonVars.domain);
        RetrofitUrlManager.getInstance().putDomain(Constant.DOMAIN_VIP, Constant.HTTP_VIP + CommonVars.domain);
        // set the new domain for web
        Constant.Web.wordUrl = "http://static2." + Constant.Web.WEB_SUFFIX + "aiciaudio/primary_audio.zip";
        Constant.Web.EVALUATE_URL_CORRECT = "http://iuserspeech." + Constant.Web.WEB_SUFFIX.replace("/","") + ":9001/test/ai/";
        Constant.Web.EVALUATE_URL_NEW = "http://iuserspeech." + Constant.Web.WEB_SUFFIX.replace("/","") + ":9001/test/eval/";
        Constant.Web.EVAL_PREFIX = "http://iuserspeech." + Constant.Web.WEB_SUFFIX.replace("/","") + ":9001/voa/";
        Constant.Web.sound_vip = "http://static2." + Constant.Web.WEB_SUFFIX + "newconcept/";
        Constant.Web.sound_voa = "http://staticvip." + Constant.Web.WEB_SUFFIX + "sounds/voa/sentence";
        Constant.Web.WordBASEURL = "http://word." + Constant.Web.WEB_SUFFIX + "words/";
        Constant.Web.VIP_VIDEO_PREFIX = "http://staticvip." + Constant.Web.WEB_SUFFIX + "video/voa/";
        Constant.Web.VIDEO_PREFIX = "http://staticvip." + Constant.Web.WEB_SUFFIX + "video/voa/";
        Constant.Web.VIDEO_PREFIX_NEW = "http://m." + Constant.Web.WEB_SUFFIX + "voaS/playPY.jsp?apptype=";
        Constant.Web.VIP_SOUND_PREFIX = "http://staticvip." + Constant.Web.WEB_SUFFIX + "sounds/voa/";
        Constant.Web.SOUND_PREFIX = "http://staticvip." + Constant.Web.WEB_SUFFIX + "sounds/voa/";
        // set the new domain for url
        Constant.Url.PROTOCOL_BJIYY_USAGE = "http://iuserspeech."+ Constant.Web.WEB_SUFFIX.replace("/","")+ ":9001/api/protocoluse666.jsp?company=3&apptype=";
        Constant.Url.PROTOCOL_BJIYY_PRIVACY = "http://iuserspeech."+ Constant.Web.WEB_SUFFIX.replace("/","")+ ":9001/api/protocolpri.jsp?company=3&apptype=";
        Constant.Url.PROTOCOL_BJIYB_PRIVACY = "http://iuserspeech."+ Constant.Web.WEB_SUFFIX.replace("/","")+ ":9001/api/protocolpri.jsp?company=1&apptype=";
        Constant.Url.PROTOCOL_BJIYB_USAGE = "http://iuserspeech."+ Constant.Web.WEB_SUFFIX.replace("/","")+ ":9001/api/protocoluse666.jsp?company=1&apptype=";
        Constant.Url.APP_ICON_URL = "http://app."+ Constant.Web.WEB_SUFFIX+"android/images/Englishtalkshow/Englishtalkshow.png";
        Constant.Url.APP_SHARE_URL = "http://voa."+ Constant.Web.WEB_SUFFIX+ "voa/shareApp.jsp?appType=";
        Constant.Url.WEB_PAY = "http://app."+ Constant.Web.WEB_SUFFIX+"wap/servlet/paychannellist?";
        Constant.Url.AD_PIC = "http://dev."+ Constant.Web.WEB_SUFFIX+ "";
        Constant.Url.MORE_APP = "http://app."+ Constant.Web.WEB_SUFFIX+"android";
        Constant.Url.COMMENT_VOICE_BASE = "http://voa."+ Constant.Web.WEB_SUFFIX+"voa/";
        Constant.Url.VOA_IMG_BASE = "http://staticvip."+ Constant.Web.WEB_SUFFIX+"images/voa/";
        Constant.Url.SHUOSHUO_PREFIX = "http://staticvip."+ Constant.Web.WEB_SUFFIX+"video/voa/";
        Constant.Url.NEW_DUBBING_PREFIX = "http://iuserspeech."+ Constant.Web.WEB_SUFFIX.replace("/","")+ ":9001/";
        Constant.Url.MY_DUBBING_PREFIX = "http://voa."+ Constant.Web.WEB_SUFFIX+"voa/talkShowShare.jsp?shuoshuoId=";
        App.Url.APP_ICON_URL = "http://app."+ Constant.Web.WEB_SUFFIX+"android/images/Primary%20school%20English/Primary%20school%20English_new.png";
        App.Url.PROTOCOL_USAGE = Constant.Url.PROTOCOL_BJIYB_USAGE;
        App.Url.PROTOCOL_URL = Constant.Url.PROTOCOL_BJIYB_PRIVACY;
        App.Url.SHARE_APP_URL = Constant.Url.APP_SHARE_URL + App.SHARE_NAME_EN;
    }
    private void setDomainLong() {
        // set domain long for update
        RetrofitUrlManager.getInstance().putDomain(Constant.DOMAIN_LONG_API, Constant.HTTP_LONG_API + CommonVars.domainLong);
        // set the new domain long for url
        Constant.Url.EMAIL_REGILTER = "http://api."+ Constant.Web.WEB2_SUFFIX+ "v2/api.iyuba?protocol=11002&app=meiyu";
        Constant.Url.PHONE_REGISTER = "http://api."+ Constant.Web.WEB2_SUFFIX+ "v2/api.iyuba?platform=android&app=meiyu&protocol=11002";
        Constant.Url.USER_IMAGE = "http://api."+ Constant.Web.WEB2_SUFFIX+ "v2/api.iyuba?";
    }


    //测试-域名切换
    private void hostSwitch(){
        if (BuildConfig.DEBUG){
            binding.hostSwitch.setVisibility(View.GONE);
        }else {
            binding.hostSwitch.setVisibility(View.GONE);
        }

        String curHost = configManager.getDomain();
        String curHostShort1 = configManager.getDomainShort();
        String host1 = binding.tvHost1.getText().toString().trim();
        String host2 = binding.tvHost2.getText().toString().trim();

        binding.curHostText.setText(curHost);

        if (curHost.equals(host1)
                ||curHostShort1.equals(host1)){
            binding.ivHost1.setSelected(true);
        }
        if (curHost.equals(host2)
                ||curHostShort1.equals(host2)){
            binding.ivHost2.setSelected(true);
        }

        binding.ivHost1.setOnClickListener(v->{
            if (binding.ivHost1.isSelected()){
                return;
            }

            binding.ivHost1.setSelected(true);
            binding.ivHost2.setSelected(false);
            if (host1.startsWith("www.")){
                configManager.setDomain(host1);
                configManager.setDomainShort(host1.replace("www.",""));
            }else {
                configManager.setDomain("www."+host1);
                configManager.setDomainShort(host1);
            }

            ToastUtil.showToast(this,"域名替换完成");
            binding.curHostText.setText(configManager.getDomain());
        });
        binding.ivHost2.setOnClickListener(v->{
            if (binding.ivHost2.isSelected()){
                return;
            }

            binding.ivHost1.setSelected(false);
            binding.ivHost2.setSelected(true);
            if (host2.startsWith("www.")){
                configManager.setDomain(host2);
                configManager.setDomainShort(host2.replace("www.",""));
            }else {
                configManager.setDomain("www."+host2);
                configManager.setDomainShort(host2);
            }

            ToastUtil.showToast(this,"域名替换完成");
            binding.curHostText.setText(configManager.getDomain());
        });
    }
}
