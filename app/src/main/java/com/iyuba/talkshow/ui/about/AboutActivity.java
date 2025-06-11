package com.iyuba.talkshow.ui.about;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.iyuba.talkshow.BuildConfig;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.local.PreferencesHelper;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.manager.VersionManager;
import com.iyuba.talkshow.data.model.QQResponse;
import com.iyuba.talkshow.data.model.TitleSeries;
import com.iyuba.talkshow.data.model.TitleSeriesResponse;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.databinding.ActivityAboutBinding;
import com.iyuba.talkshow.http.Http;
import com.iyuba.talkshow.http.HttpCallback;
import com.iyuba.talkshow.lil.help_fix.ui.preSaveData.PreSaveDataActivity;
import com.iyuba.talkshow.lil.help_fix.util.FixUtil;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxTimer;
import com.iyuba.talkshow.lil.help_mvp.util.xxpermission.XXPermissionUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.web.WebActivity;
import com.iyuba.talkshow.util.BrandUtil;
import com.iyuba.talkshow.util.LogUtil;
import com.iyuba.talkshow.util.Util;
import com.iyuba.wordtest.db.WordDataBase;
import com.tencent.vasdolly.helper.ChannelReaderUtil;
import com.umeng.analytics.MobclickAgent;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import okhttp3.Call;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class AboutActivity extends BaseActivity implements AboutMvpView {
    private static final String VERSION_CODE = "versionCode";
    private static final String APP_URL = "appUrl";
    public static final String TAG = "AboutActivity";
    @Inject
    VersionManager mVersionManager;
    @Inject
    AboutPresenter mPresenter;
    @Inject
    PreferencesHelper mPreferencesHelper;
    @Inject
    ConfigManager mConfigManager;
    @Inject
    DataManager mDataManager;

    private ClearUserFragment fragment;
    ActivityAboutBinding binding ;

    //图标点击次数
    private int logoClickCount = 0;

    public static Intent buildIntent(Context context, String versionCode, String appUrl) {
        Intent intent = new Intent(context, AboutActivity.class);
        intent.putExtra(VERSION_CODE, versionCode);
        intent.putExtra(APP_URL, appUrl);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activityComponent().inject(this);
        mPresenter.attachView(this);
        setSupportActionBar(binding.aboutToolbar.listToolbar);

//        binding.logoIv.setText(App.APP_NAME_CH);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Intent intent = getIntent();
        String versionCode = intent.getStringExtra(VERSION_CODE);
        String appUrl = intent.getStringExtra(APP_URL);
        if (versionCode != null && appUrl != null) {
            binding.aboutHasnewImageview.setVisibility(View.VISIBLE);
            binding.aboutAppUpdateRl.setEnabled(false);
            binding.aboutDownloadProgressbar.setVisibility(View.VISIBLE);
            mPresenter.downloadApk(versionCode, appUrl);
        } else {
            if (App.APP_CHECK_UPGRADE) {
                mVersionManager.checkVersion(callBack);
            }
        }

        if (!UserInfoManager.getInstance().isLogin()) {
            binding.customClearUser.setVisibility(View.GONE);
        }
        if (App.APP_HUAWEI_PRIVACY) {
            binding.aboutMoreAppBtn.setVisibility(View.GONE);
        }
        binding.weixinButton.setText(String.format("%s用户反馈群: %s", BrandUtil.getBrandChinese(), BrandUtil.getQQGroupNumber(mPreferencesHelper)));
        binding.weixinButton.setOnClickListener(v -> {
            joinQQGroup(BrandUtil.getQQGroupKey(mPreferencesHelper));
        });
        binding.aboutMoreAppBtn.setOnClickListener(v -> clickMoreAppBtn());
        binding.aboutVersionTextview.setText(MessageFormat.format(getString(R.string.about_version), VersionManager.VERSION_NAME));

        binding.customClearUser.setOnClickListener(v -> clickClearUser());
        binding.customeServicePart.setOnClickListener(v -> {
            AboutActivityPermissionsDispatcher.callPhoneWithPermissionCheck(AboutActivity.this);
        });

        //这里不再请求，统一更换到我拍的界面
//        requestQQNumber();

        //无作用，用于识别打包的渠道
        binding.logoIv.setOnClickListener(v->{
            logoClickCount++;

            if (logoClickCount>5){
                String channel = ChannelReaderUtil.getChannel(this);
                binding.channel.setVisibility(View.VISIBLE);
                binding.channel.setText(channel);
            }
        });

        //用于获取预存数据并进行处理
        if (BuildConfig.DEBUG){
            binding.textView1.setOnClickListener(v->{
                startActivity(new Intent(this, PreSaveDataActivity.class));
            });
        }

        //显示备案号
        showFilingNumber();

        //隐藏右下角的爱语吧图片
        binding.aboutCornerLogo.setVisibility(View.INVISIBLE);
    }

    public void requestQQNumber() {
        String url = "http://iuserspeech." + Constant.Web.WEB_SUFFIX.replace("/","") + ":9001/japanapi/getJpQQ.jsp?appid=" + App.APP_ID;
        Http.get(url, new HttpCallback() {
            @Override
            public void onSucceed(Call call, String response) {
                try {
                    Log.e(TAG, "requestQQNumber onSucceed result  " + response);
                    QQResponse bean = new Gson().fromJson(response, QQResponse.class);
                    if ((bean != null) && (bean.result == 200)) {
                        if ((bean.data != null) && (bean.data.size() > 0)) {
                            mConfigManager.setQQEditor(bean.data.get(0).editor);
                            mConfigManager.setQQTechnician(bean.data.get(0).technician);
                            mConfigManager.setQQManager(bean.data.get(0).manager);
                        } else {
                            Log.e(TAG, "result ok, data is null? ");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Call call, Exception e) {
                if (e != null) {
                    Log.e(TAG, "onError  " + e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about_qq, menu);
        menu.findItem(R.id.menu1).setTitle("内容QQ:" + BrandUtil.getQQEditor(mPreferencesHelper));
        menu.findItem(R.id.menu2).setTitle("技术QQ:" + BrandUtil.getQQTechnician(mPreferencesHelper));
        menu.findItem(R.id.menu3).setTitle("投诉QQ:" + BrandUtil.getQQManager(mPreferencesHelper));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu1:
                Util.startQQ(mContext, BrandUtil.getQQEditor(mPreferencesHelper));
                return true;
            case R.id.menu2:
                Util.startQQ(mContext, BrandUtil.getQQTechnician(mPreferencesHelper));
                return true;
            case R.id.menu3:
                Util.startQQ(mContext, BrandUtil.getQQManager(mPreferencesHelper));
                return true;
            default:
                return false;
        }
    }

    void clickMoreAppBtn() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = df.parse("2019-01-09");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        long timestamp = cal.getTimeInMillis();
        if (System.currentTimeMillis() < timestamp) {
            return;
        }
        Intent intent = WebActivity.buildIntent(this, Constant.Url.MORE_APP);
        startActivity(intent);
    }

    void clickUpdateBtn() {
        mVersionManager.checkVersion(callBack);
    }

    private void showAlertDialog(String msg, DialogInterface.OnClickListener ocl) {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle(R.string.alert_title);
        alert.setMessage(msg);
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.alert_btn_ok), ocl);
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.alert_btn_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        binding.aboutAppUpdateRl.setEnabled(true);
                        binding.aboutDownloadProgressbar.setVisibility(View.INVISIBLE);
                    }
                });
        alert.show();
    }

    VersionManager.AppUpdateCallBack callBack = new VersionManager.AppUpdateCallBack() {
        @Override
        public void appUpdateSave(final String versionCode, final String appUrl) {
            binding.aboutHasnewImageview.setVisibility(View.VISIBLE);
            binding.aboutAppUpdateRl.setEnabled(false);
            binding.aboutDownloadProgressbar.setVisibility(View.VISIBLE);
            showAlertDialog(
                    MessageFormat.format(getString(R.string.about_update_alert), versionCode),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPresenter.downloadApk(versionCode, appUrl);
                        }
                    });
        }

        @Override
        public void appUpdateFailed() {
            binding.aboutHasnewImageview.setVisibility(View.INVISIBLE);
            binding.aboutDownloadProgressbar.setVisibility(View.INVISIBLE);
            showToast(App.APP_NAME_CH + getString(R.string.about_update_isnew));
        }
    };

    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    @Override
    public void setDownloadProgress(int progress) {
        binding.aboutDownloadProgressbar.setProgress(progress);
    }

    @Override
    public void setDownloadMaxProgress(int maxProgress) {
        binding.aboutDownloadProgressbar.setMax(maxProgress);
    }

    @Override
    public void setProgressVisibility(int visible) {
        binding.aboutDownloadProgressbar.setVisibility(visible);
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    void clickClearUser() {
        if (!UserInfoManager.getInstance().isLogin()) {
            NewLoginUtil.startToLogin(mContext);
            return;
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_logout, null);
        TextView remindText = view.findViewById(R.id.remindText);
        remindText.setText(R.string.clear_user_alert);
        AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle(getString(R.string.alert_title))
                .setView(view)
                .create();
        dialog.show();
        TextView agreeNo = view.findViewById(R.id.text_no_agree);
        agreeNo.setOnClickListener(v -> {
            dialog.dismiss();
        });
        TextView agree = view.findViewById(R.id.text_agree);
        agree.setText("继续注销");
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Log.e(TAG, "clickClearUser clicked.");
                fragment= new ClearUserFragment();
                fragment.show(getFragmentManager(), "ClearUser");
                fragment.setOnResult(new IDialogResultListener() {
                    @Override
                    public void onDataResult(Object result) {
                        if (result == null) {
                            Log.e(TAG, "clickClearUser onDataResult is null.");
                            showToast("密码输入为空，请输入有效的密码！");
                            return;
                        }
                        String userPassword = (String) result;
                        Log.e(TAG, "onDataResult userPassword " + userPassword);
                        if (userPassword.length() < 6 || userPassword.length() > 20) {
                            Log.e(TAG, "clickClearUser onDataResult is null.");
                            showToast("密码的格式(6-20位英文、数字、下划线)");
                            return;
                        }
                        // no need check, as user may change password by web page
                        mPresenter.clearUser(userPassword);
                    }
                });
            }
        });
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AboutActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.CALL_PHONE)
    public void callPhone() {
        Intent intents = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + "4008881905");
        intents.setData(data);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intents);
    }

    @OnPermissionDenied(Manifest.permission.CALL_PHONE)
    public void callPhoneFail() {
        showToast(getString(R.string.call_permission) + getString(R.string.permission_fail));
    }

    //设置备案号
    // TODO: 2025/2/27 之前都是使用包名处理的，这里使用名称判断处理
    private void showFilingNumber(){
        String appName = getResources().getString(R.string.app_name);
        if (appName.equals("初中英语")){
            binding.filingNumber.setText("京ICP备18027903号-9A");
        }else if (appName.equals("初中英语口语秀")){
            binding.filingNumber.setText("京ICP备18027903号-12A");
        }else {
            binding.filingNumber.setVisibility(View.INVISIBLE);
        }

        //debug状态下可以进行的测试
        if (BuildConfig.DEBUG){
            binding.filingNumber.setOnClickListener(v->{
                new AlertDialog.Builder(this)
                        .setTitle("准备更新数据库数据")
                        .setMessage("点击后可以更新数据库数据(debug模式下)，是否确认？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateAllData();
                            }
                        }).setNegativeButton("取消",null)
                        .create().show();
            });
        }
    }

    /***************************************更新数据使用*************************************************************************************************/
    //测试-更新课程数据、课程详情数据和单词数据(不要删除，这里用于更新单词和课程、课程详情数据使用，更新时注意关闭单词和课程数据库的预存数据操作，之后执行功能，完成后处理即可)
    private int[] allDataArray = null;

    private void updateAllData(){
        allDataArray = new int[]{
                488,507,217,218,219,220,221,//人教版
                388,389,390,391,392,//北师版
                397,398,399,400,401,402,//仁爱版
                403,404,405,406,407,//冀教版
                408,409,410,411,412,413,//译林版
                414,415,416,417,418,419,420,//鲁教版
                353,354,355,356,357,358,//外研社
                450,451,452,453//新概念
        };

        //更新课程数据和详情数据
//        updateAllVoa();

        //更新单词数据
//        WordDataBase.getInstance(getApplicationContext()).getTalkShowTextDao().clearTable();
        updateAllWord();

        //差值数据
//        voaIdList.clear();
//        updateAllVoaText();
    }

    /*************************更新操作*******************************/
    //失败的数据
    private StringBuffer failVoaBuffer = new StringBuffer();
    //失败的文本数据
    private StringBuffer failVoaTextBuffer = new StringBuffer();

    /**************************单词数据更新***************************/
    //单词数据更新
    private int curWordIndex = 0;
    private int wordSuccessInt = 0;
    private int wordFailInt = 0;
    private void updateAllWord(){
        if (curWordIndex>=allDataArray.length){
            LogUtil.d("单词更新", "全部单词更新完成--成功数量："+wordSuccessInt+"--失败数量："+wordFailInt);
            return;
        }

        int bookId = allDataArray[curWordIndex];

        mDataManager.getWordByBookId(bookId)
//        mDataManager.updateWordByBookId(bookId,0)
                .subscribeOn(rx.schedulers.Schedulers.io())
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new rx.Observer<com.iyuba.talkshow.data.model.UpdateWordResponse>() {
                    @Override
                    public void onCompleted() {
                        curWordIndex++;
                    }

                    @Override
                    public void onError(Throwable e) {
                        wordFailInt++;
                        LogUtil.d("单词更新", "更新失败--"+bookId);

                        //延迟1.5s下一个
                        RxTimer.getInstance().timerInMain("delay", 1500L, new RxTimer.RxActionListener() {
                            @Override
                            public void onAction(long number) {
                                RxTimer.getInstance().cancelTimer("delay");

                                //下一个开始
                                updateAllWord();
                            }
                        });
                    }

                    @Override
                    public void onNext(com.iyuba.talkshow.data.model.UpdateWordResponse respons) {
                        WordDataBase.getInstance(TalkShowApplication.getInstance()).getTalkShowWordsDao().insertWord(respons.getData());

                        wordSuccessInt++;
                        LogUtil.d("单词更新", "更新成功--"+bookId+"--"+respons.getData().size());

                        //延迟1.5s下一个
                        RxTimer.getInstance().timerInMain("delay", 1500L, new RxTimer.RxActionListener() {
                            @Override
                            public void onAction(long number) {
                                RxTimer.getInstance().cancelTimer("delay");

                                //保存数据
//                                wordList = respons.getData();
//                                //下一个开始
//                                curWordDetailIndex = 0;
//                                wordDetailSuccess = 0;
//                                wordDetailFail = 0;
//                                updateWordText();

                                updateAllWord();
                            }
                        });
                    }
                });
    }

    //单词详情数据更新
    /*private List<TalkShowWords> wordList = new ArrayList<>();
    private int curWordDetailIndex = 0;
    private int wordDetailSuccess = 0;
    private int wordDetailFail = 0;
    private void updateWordText(){
        if (curWordDetailIndex>=wordList.size()-1){
            LogUtils.d("单词更新", "当前课程的单词完成,准备下一组数据--成功："+wordDetailSuccess+"，失败："+wordDetailFail);
            //下一个
            updateAllWord();
            return;
        }

        //当前数据
        TalkShowWords words = wordList.get(curWordDetailIndex);
        TalkshowTexts texts = WordDataBase.getInstance(getApplicationContext()).getTalkShowTextDao().getSentenceByWord(words.voa_id,words.idindex);
        if (texts!=null){
            wordDetailSuccess++;
            curWordDetailIndex++;
            LogUtils.d("单词更新", "当前数据存在，下一个--"+words.word);
            RxTimer.getInstance().timerInMain("delayWordDetail", 1000L, new RxTimer.RxActionListener() {
                @Override
                public void onAction(long number) {
                    RxTimer.getInstance().cancelTimer("delayWordDetail");
                    updateWordText();
                }
            });
            return;
        }


        HttpManager.getSearchApi().getTexts("json",words.voa_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SearchTextResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        curWordDetailIndex++;
                    }

                    @Override
                    public void onNext(SearchTextResult searchTextResult) {
                        wordDetailSuccess++;
                        LogUtils.d("单词更新", "单词文本更新成功--"+words.word);

                        //保存数据
                        if (searchTextResult!=null && searchTextResult.getVoatext()!=null && searchTextResult.getVoatext().size()>0){
                            WordDataBase.getInstance(getApplicationContext()).getTalkShowTextDao().intserTexts(searchTextResult.getVoatext());
                        }
                        //下一个数据
                        RxTimer.getInstance().timerInMain("delayWordDetailSuccess", 1000L, new RxTimer.RxActionListener() {
                            @Override
                            public void onAction(long number) {
                                RxTimer.getInstance().cancelTimer("delayWordDetailSuccess");
                                updateWordText();
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        wordDetailFail++;
                        LogUtils.d("单词更新", "单词文本更新失败--"+words.word+"--"+e.getMessage());
                        RxTimer.getInstance().timerInMain("delayWordDetailFail", 1000L, new RxTimer.RxActionListener() {
                            @Override
                            public void onAction(long number) {
                                RxTimer.getInstance().cancelTimer("delayWordDetailFail");
                                updateWordText();
                            }
                        });
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }*/

    /***************************课程数据更新***************************/
    //更新课程数据
    private int curVoaIndex = 0;
    private int voaSuccessInt = 0;
    private int voaFailInt = 0;
    private void updateAllVoa(){
        if (curVoaIndex>=allDataArray.length){
            LogUtil.d("课程更新", "全部课程更新完成--成功数量："+voaSuccessInt+"--失败数量："+voaFailInt);
            LogUtil.d("课程更新", "全部失败数据显示：---课程数据："+failVoaBuffer.toString()+"---文本数据："+failVoaTextBuffer);
            return;
        }

        int bookId = allDataArray[curVoaIndex];
        LogUtil.d("课程更新", "开始更新课程--"+bookId);

        mDataManager.getTitleSeriesList(String.valueOf(bookId),0)
                .subscribeOn(rx.schedulers.Schedulers.io())
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new rx.Observer<TitleSeriesResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        voaFailInt++;
                        curVoaIndex++;
                        failVoaBuffer.append(bookId+",");
                        LogUtil.d("课程更新", "课程更新失败--"+bookId);

                        //延迟1.5s下一个
                        RxTimer.getInstance().timerInMain("delay", 1500L, new RxTimer.RxActionListener() {
                            @Override
                            public void onAction(long number) {
                                RxTimer.getInstance().cancelTimer("delay");

                                //下一个开始
                                updateAllVoa();
                            }
                        });
                    }

                    @Override
                    public void onNext(TitleSeriesResponse response) {
                        List<TitleSeries> seriesData = response.getData();
                        voaIdList.clear();
                        for (TitleSeries series: seriesData) {
                            try {
                                Voa tempVoa = Series2Voa(series);
                                mDataManager.insertVoaDB(tempVoa);

                                //暂存数据
                                voaIdList.add(tempVoa.voaId());
                            } catch (Exception var2) {
                                LogUtil.d("课程更新", "有个数据不正确--"+series.series+"--"+series.Title);
                            }
                        }

                        voaSuccessInt++;
                        curVoaIndex++;

                        //延迟1.5s下一个
                        RxTimer.getInstance().timerInMain("delay", 1500L, new RxTimer.RxActionListener() {
                            @Override
                            public void onAction(long number) {
                                RxTimer.getInstance().cancelTimer("delay");

                                //下一个开始
                                updateAllVoaText();
                            }
                        });
                    }
                });
    }

    //更新课程详情数据
    private List<Integer> voaIdList = new ArrayList<>();
    private int curVoaTextIndex = 0;
    private int voaTextSuccessInt = 0;
    private int voaTextFailInt = 0;
    private void updateAllVoaText(){
        if (curVoaTextIndex>=voaIdList.size()){
            LogUtil.d("课程更新", "文本更新完成("+allDataArray[curVoaIndex-1]+")--成功数量："+voaTextSuccessInt+"--失败数量："+voaTextFailInt);
            //重置数据
            curVoaTextIndex = 0;
            voaIdList.clear();

            voaTextSuccessInt = 0;
            voaTextFailInt = 0;
            //下一个
            updateAllVoa();
            return;
        }

        int voaId = voaIdList.get(curVoaTextIndex);
        LogUtil.d("课程更新", "文本开始更新--"+voaId);

        mDataManager.syncVoaTexts(voaId)
                .subscribeOn(rx.schedulers.Schedulers.io())
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new rx.Observer<List<VoaText>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        //失败后刷新下一个
                        voaTextFailInt++;
                        curVoaTextIndex++;
                        failVoaTextBuffer.append(voaId+",");
                        LogUtil.d("课程更新", "文本更新失败--"+voaId);

                        //延迟1.5s下一个
                        RxTimer.getInstance().timerInMain("delay", 1500L, new RxTimer.RxActionListener() {
                            @Override
                            public void onAction(long number) {
                                RxTimer.getInstance().cancelTimer("delay");

                                //下一个开始
                                updateAllVoaText();
                            }
                        });
                    }

                    @Override
                    public void onNext(List<VoaText> list) {
                        //成功之后也是下一个
                        voaTextSuccessInt++;
                        curVoaTextIndex++;

                        //延迟1.5s下一个
                        RxTimer.getInstance().timerInMain("delay", 1500L, new RxTimer.RxActionListener() {
                            @Override
                            public void onAction(long number) {
                                RxTimer.getInstance().cancelTimer("delay");

                                //下一个开始
                                updateAllVoaText();
                            }
                        });
                    }
                });
    }

    private Voa Series2Voa(TitleSeries series) {
        if (series == null) {
            return null;
        }
        return Voa.builder().setUrl(series.Sound).setPic(series.Pic).setTitle(series.Title).setTitleCn(series.Title_cn)
                .setVoaId(series.Id).setCategory(series.Category).setDescCn(series.DescCn).setSeries(series.series)
                .setCreateTime(series.CreatTime).setPublishTime(series.PublishTime).setHotFlag(series.HotFlg).setReadCount(series.ReadCount)
                .setClickRead(series.clickRead).setSound(series.Sound.replace("http://staticvip."+Constant.Web.WEB_SUFFIX.replace("/","")+"/sounds/voa", ""))
                .setTotalTime(series.totalTime).setPercentId(series.percentage).setOutlineId(series.outlineid).setPackageId(series.packageid).setCategoryId(series.categoryid).setClassId(series.classid)
                .setIntroDesc(series.IntroDesc).setPageTitle(series.Title).setKeyword(series.Keyword)
                //这里增加video字段数据
                .setVideo(series.video)
                .build();
    }
}
