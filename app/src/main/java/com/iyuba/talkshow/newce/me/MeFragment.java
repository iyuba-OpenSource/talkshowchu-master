package com.iyuba.talkshow.newce.me;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivity;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivityNew;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivityNew;
import com.iyuba.headlinelibrary.ui.video.VideoMiniContentActivity;
import com.iyuba.imooclib.ui.record.PurchaseRecordActivity;
import com.iyuba.module.favor.data.model.BasicFavorPart;
import com.iyuba.module.favor.event.FavorItemEvent;
import com.iyuba.module.user.IyuUserManager;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.manager.AbilityControlManager;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.databinding.FragmentMeBinding;
import com.iyuba.talkshow.event.LoginEvent;
import com.iyuba.talkshow.event.LoginOutEvent;
import com.iyuba.talkshow.event.SyncDataEvent;
import com.iyuba.talkshow.lil.help_fix.ui.ad.ui.AdContainerActivity;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.AdShowUtil;
import com.iyuba.talkshow.lil.help_fix.ui.collect.chapter.ChapterCollectActivity;
import com.iyuba.talkshow.lil.help_fix.ui.main.ui.video.VideoShowActivity;
import com.iyuba.talkshow.lil.help_fix.ui.me_wallet.WalletListActivity;
import com.iyuba.talkshow.lil.help_mvp.util.BigDecimalUtil;
import com.iyuba.talkshow.lil.help_mvp.util.DateUtil;
import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.event.UserInfoRefreshEvent;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.newview.CommonProgressDialog;
import com.iyuba.talkshow.newview.LoginResult;
import com.iyuba.talkshow.ui.about.AboutActivity;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.feedback.FeedbackActivity;
import com.iyuba.talkshow.ui.main.drawer.Share;
import com.iyuba.talkshow.ui.rank.RankActivity;
import com.iyuba.talkshow.ui.sign.SignActivity;
import com.iyuba.talkshow.ui.user.login.changeName.ChangeNameActivity;
import com.iyuba.talkshow.ui.user.me.CalendarActivity;
import com.iyuba.talkshow.ui.user.me.SyncActivity;
import com.iyuba.talkshow.ui.user.me.dubbing.MyDubbingActivity;
import com.iyuba.talkshow.ui.user.register.submit.RegisterSubmitActivity;
import com.iyuba.talkshow.ui.vip.buyvip.NewVipCenterActivity;
import com.iyuba.talkshow.ui.web.OfficialActivity;
import com.iyuba.talkshow.ui.web.WebActivity;
import com.iyuba.talkshow.ui.widget.DownloadDialog;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.iyuba.talkshow.ui.words.WordNoteActivity;
import com.iyuba.talkshow.util.BrandUtil;
import com.iyuba.talkshow.util.DialogUtil;
import com.iyuba.talkshow.util.MD5;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.entity.BookLevels;
import com.iyuba.wordtest.entity.NewBookLevels;
import com.iyuba.wordtest.manager.WordManager;
import com.mob.secverify.SecVerify;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.vasdolly.helper.ChannelReaderUtil;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import personal.iyuba.personalhomelibrary.event.UserPhotoChangeEvent;
import personal.iyuba.personalhomelibrary.ui.groupChat.GroupChatManageActivity;
import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;
import personal.iyuba.personalhomelibrary.ui.message.MessageActivity;
import personal.iyuba.personalhomelibrary.ui.my.MySpeechActivity;
import personal.iyuba.personalhomelibrary.ui.studySummary.SummaryActivity;
import personal.iyuba.personalhomelibrary.ui.studySummary.SummaryType;

/**
 * 我的界面
 * Created by carl shen on 2020/7/30
 * New Primary English, new study experience.
 */
public class MeFragment extends BaseFragment implements MeFragMvpView {

    @Inject
    MeFragPresenter mPresenter;
    @Inject
    ConfigManager configManager;
    private DownloadDialog downloadDialog;
    private static AlertDialog downAlert = null;
    @Inject
    DataManager mDataManager;
    FragmentMeBinding binding;
    public static final String TAG = "MeFragment";

    //这里判断下是否是从视频模块出来的，因为视频模块点击广告后没有回调事件，手动触发
    private boolean isToVideo = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMeBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mPresenter.attachView(this);
        EventBus.getDefault().register(this);

        //这里统一进行qq客服和qq群的请求
//        BrandUtil.requestQQGroupNumber(mDataManager.getPreferencesHelper(), mAccountManager.getUid());
        BrandUtil.requestQQNumber(mDataManager.getPreferencesHelper());
        BrandUtil.requestQQGroupNumber(mDataManager.getPreferencesHelper(), UserInfoManager.getInstance().getUserId());

        if (UserInfoManager.getInstance().isLogin()) {
            mPresenter.enterGroup();
        }

        //考虑到个人中心模块也能会进入视频中，所以写在这里
        //这里注意修改共通模块的url
        IHeadline.resetMseUrl();
        String extraUrl = "http://iuserspeech." + Constant.Web.WEB_SUFFIX.replace("/", "") + ":9001/test/ai/";
        IHeadline.setExtraMseUrl(extraUrl);
        String extraMergeUrl = "http://iuserspeech." + Constant.Web.WEB_SUFFIX.replace("/", "") + ":9001/test/merge/";
        IHeadline.setExtraMergeAudioUrl(extraMergeUrl);

        //设置显示
        setView();
        //设置点击
        setClick();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(mContext);
        setDes();

        //如果从视频模块中出来，则刷新数据
        if (isToVideo){
            isToVideo = false;
            UserInfoManager.getInstance().getRemoteUserInfo(UserInfoManager.getInstance().getUserId(), null);
        }
    }

    //刷新个人信息
    private void setDes() {
        /*Glide.clear(binding.meUserImage);
        Glide.with(mContext)
                .load(mPresenter.getUserImageUrl())
                .asBitmap()
                .signature(new StringSignature(System.currentTimeMillis()+""))
                .transform(new CircleTransform(mContext))
                .placeholder(R.drawable.default_avatar)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.meUserImage);*/
        Glide3Util.loadHeadImg(mContext, mPresenter.getUserImageUrl(), R.drawable.default_avatar, binding.meUserImage);
        if (UserInfoManager.getInstance().isLogin()) {
            binding.meUsernameTv.setText(UserInfoManager.getInstance().getUserName());
            binding.meUserinfoContainer.setVisibility(View.VISIBLE);
            binding.meLogin.setVisibility(View.GONE);
            binding.meLogout.setVisibility(View.VISIBLE);
            binding.meChangeUsername.setVisibility(View.GONE);

            //钱包显示
            double moneyPrice = UserInfoManager.getInstance().getMoney();
            binding.walletHistoryText.setText(String.valueOf(BigDecimalUtil.trans2Double(moneyPrice)) + "元");
            //积分显示
            int integral = UserInfoManager.getInstance().getJiFen();
            binding.integralStoreText.setText(String.valueOf(integral)+"积分");
        } else {
            binding.meUserinfoContainer.setVisibility(View.GONE);
            binding.meLogin.setVisibility(View.VISIBLE);
            binding.meLogout.setVisibility(View.GONE);
            binding.meChangeUsername.setVisibility(View.GONE);

            //默认钱包字符
            binding.walletHistoryText.setText("钱包");
            //默认积分字符
            binding.integralStoreText.setText("积分");
        }

        if (UserInfoManager.getInstance().isVip()) {
            binding.meVipstateIv.setImageResource(R.drawable.vip);
            if (UserInfoManager.getInstance().getVipTime()>0) {
                //这里处理下：5个小时之内的时间，则显示详情的会员时间；否则显示日期即可
                long outDateTime = 5*60*60*1000L;
                //判断显示到期时间
                long vipTime = UserInfoManager.getInstance().getVipTime();
                if (vipTime - System.currentTimeMillis() > outDateTime){
                    binding.meUserDate.setText("到期时间："+ DateUtil.toDateStr(UserInfoManager.getInstance().getVipTime(), DateUtil.YMD));
                }else {
                    binding.meUserDate.setText("到期时间："+ DateUtil.toDateStr(UserInfoManager.getInstance().getVipTime(), DateUtil.YMDHMS));
                }
            } else {
                binding.meUserDate.setText("应用会员");
            }
        } else {
            binding.meVipstateIv.setImageResource(R.drawable.no_vip);
            binding.meUserDate.setText("普通用户");
        }
    }

    //设置视图显示
    private void setView() {
        //根据要求，隐藏一些内容显示
        binding.meChangeUsername.setVisibility(View.GONE);
        binding.meFreeVip.setVisibility(View.GONE);

        //公众号根据要求，暂时不显示了
        binding.meOfficialAccount.setVisibility(View.GONE);

        //钱包记录和购买记录移动到会员中心界面
        binding.walletHistoryLayout.setVisibility(View.GONE);
        binding.mePayMark.setVisibility(View.GONE);

        //积分商城功能移到上方显示
        binding.meIntegralStore.setVisibility(View.GONE);

        //视频收藏和视频配音功能移到视频界面
        binding.videoCollect.setVisibility(View.GONE);
        binding.videoTalk.setVisibility(View.GONE);

        //开启激励视频广告(vivo上暂时先屏蔽激励视频，小版本的)
//        String channel = ChannelReaderUtil.getChannel(getActivity());
//        String packageName = getActivity().getPackageName();
//        if (channel.equals("vivo")&&packageName.equals(Constant.PackageName.Package_juniorenglish)){
//            binding.rewardVideoAd.setVisibility(View.GONE);
//        }else {
//            binding.rewardVideoAd.setVisibility(View.VISIBLE);
//        }
        // TODO: 2024/8/29 因为之前李涛在华为平台上显示激励视频需要及时显示关闭按钮，暂时关闭这个入口
        binding.rewardVideoAd.setVisibility(View.GONE);

        //根据接口控制视频显示
        if (AbilityControlManager.getInstance().isLimitVideo()) {
            binding.videoShow.setVisibility(View.GONE);
        } else {
            binding.videoShow.setVisibility(View.VISIBLE);
        }
    }

    //设置点击操作
    private void setClick() {
        //设置按钮
        binding.meSetting.setOnClickListener(v -> {
            startActivity(new Intent(mContext, SyncActivity.class));
        });
        //会员中心
        binding.meVipCenter.setOnClickListener(v -> {
            NewVipCenterActivity.start(getActivity(), NewVipCenterActivity.BENYINGYONG);
        });
        //登录
        binding.meLoginBtn.setOnClickListener(v -> {
            clickLogin();
        });
        //登出
        binding.meLogout.setOnClickListener(v -> {
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_logout, null);
            TextView remindText = view.findViewById(R.id.remindText);
            remindText.setText(R.string.logout_alert);
            AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle(getString(R.string.alert_title))
                    .setView(view)
                    .create();
            dialog.show();
            TextView agreeNo = view.findViewById(R.id.text_no_agree);
            TextView agree = view.findViewById(R.id.text_agree);
            agreeNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    UserInfoManager.getInstance().clearUserInfo();
                    showToast(R.string.login_out_success);
                    EventBus.getDefault().post(new LoginOutEvent());

                    //个人中心
                    IyuUserManager.getInstance().logout();
                }
            });
        });
        //积分商城
        binding.meIntegralStore.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                clickLogin();
                return;
            }

            String url = "http://m." + com.iyuba.talkshow.Constant.Web.WEB_SUFFIX + "mall/index.jsp?"
                    + "&uid=" + UserInfoManager.getInstance().getUserId()
                    + "&sign=" + MD5.getMD5ofStr("iyuba" + UserInfoManager.getInstance().getUserId() + "camstory")
                    + "&username=" + UserInfoManager.getInstance().getUserName()
                    + "&platform=android&appid=" + App.APP_ID;

            startActivity(WebActivity.buildIntent(mContext, url,
                    "积分明细",
                    "http://api." + com.iyuba.talkshow.Constant.Web.WEB_SUFFIX + "credits/useractionrecordmobileList1.jsp?uid=" + UserInfoManager.getInstance().getUserId())
            );
        });
        //积分
        binding.integralStoreLayout.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()) {
                clickLogin();
                return;
            }

            String url = "http://m." + com.iyuba.talkshow.Constant.Web.WEB_SUFFIX + "mall/index.jsp?"
                    + "&uid=" + UserInfoManager.getInstance().getUserId()
                    + "&sign=" + MD5.getMD5ofStr("iyuba" + UserInfoManager.getInstance().getUserId() + "camstory")
                    + "&username=" + UserInfoManager.getInstance().getUserName()
                    + "&platform=android&appid=" + App.APP_ID;

            startActivity(WebActivity.buildIntent(mContext, url,
                    "积分明细",
                    "http://api." + com.iyuba.talkshow.Constant.Web.WEB_SUFFIX + "credits/useractionrecordmobileList1.jsp?uid=" + UserInfoManager.getInstance().getUserId())
            );
        });
        //我的单词
        binding.meWordCollect.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                clickLogin();
                return;
            }

            WordNoteActivity.start(getActivity());
        });
        //好评送书
        binding.meSendBook.setOnClickListener(v -> {
            DialogUtil.showSendBookDialog(getActivity(), DialogUtil.FRAGMENT);
        });
        //举报
        binding.meComplain.setOnClickListener(v -> {
            try {
                String url = "mqqwpa://im/chat?chat_type=wpa&uin=";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url + "572828703")));
            } catch (Exception e) {
                showToastShort("您的设备尚未安装QQ客户端，举报功能需要使用QQ");
                e.printStackTrace();
            }
        });
        //我的配音
        binding.meDubbing.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                clickLogin();
                return;
            }

            Intent intent = new Intent(mContext, MyDubbingActivity.class);
            startActivity(intent);
        });
        //文章收藏
        binding.meArticleCollect.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                clickLogin();
                return;
            }

            ChapterCollectActivity.start(getActivity());
        });
        //排行榜
        binding.meRank.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                clickLogin();
                return;
            }

            Intent intent = new Intent(mContext, RankActivity.class);
            startActivity(intent);
        });
        //消息中心
        binding.meMessageCenter.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                clickLogin();
                return;
            }

            startActivity(new Intent(getActivity(), MessageActivity.class));
        });
        //头像
        binding.meUserImage.setOnClickListener(v -> {
            startActivity(PersonalHomeActivity.buildIntent(getActivity(),
                    Integer.parseInt(String.valueOf(UserInfoManager.getInstance().getUserId())),
                    UserInfoManager.getInstance().getUserName(), 0));
        });
        //用户政策
        binding.meUserProtocol.setOnClickListener(v -> {
            Intent intent = WebActivity.buildIntent(mContext, App.Url.PROTOCOL_USAGE + App.APP_NAME_CH, "用户协议");
            mContext.startActivity(intent);
        });
        //隐私政策
        binding.mePrivacyAgreement.setOnClickListener(v -> {
            Intent intent = WebActivity.buildIntent(mContext, App.Url.PROTOCOL_URL + App.APP_NAME_CH, App.APP_NAME_PRIVACY);
            mContext.startActivity(intent);
        });
        //打卡
        binding.meClockIn.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                clickLogin();
                return;
            }

            Intent intent = new Intent(mContext, SignActivity.class);
            startActivity(intent);
        });
        //打卡报告
        binding.meClockReport.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                clickLogin();
                return;
            }

            Intent intent = new Intent(mContext, CalendarActivity.class);
            startActivity(intent);
        });
        //学习报告
        binding.meStudyReport.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                clickLogin();
                return;
            }

            String[] types = new String[]{
                    SummaryType.LISTEN,
                    SummaryType.WORD,
                    SummaryType.EVALUATE,
                    SummaryType.MOOC,
                    SummaryType.READ
            };
            String type = "all";
            startActivity(SummaryActivity.getIntent(mContext, type, types, 0));
        });
        //官方群
        binding.addIyubaGroup.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                clickLogin();
                return;
            }

            if (configManager.getQQOfficial() > 0) {
                GroupChatManageActivity.start(mContext, configManager.getQQOfficial(), App.DEFAULT_QQ_GROUP, true);
            } else {
                GroupChatManageActivity.start(mContext, App.DEFAULT_QQ_ID, App.DEFAULT_QQ_GROUP, true);
            }
        });
        //免费会员(暂时关闭)
        binding.meFreeVip.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                clickLogin();
                return;
            }

            if (!ConfigData.openWxSmallShare) {
                ToastUtil.showToast(mContext, "对不起，分享暂时不支持");
                return;
            }

            if (!Share.isWXSmallAvailable(mContext)) {
                showToastShort("您的手机暂时不支持跳转到微信小程序，谢谢！");
                return;
            }

            IWXAPI api = WXAPIFactory.createWXAPI(mContext, ConfigData.wx_key);
            WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
            req.userName = ConfigData.wx_small_name;
            String minipath = String.format("/pages/getMember/getMember?uid=%d", UserInfoManager.getInstance().getUserId());
            req.path = minipath;
            req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;
            api.sendReq(req);
        });
        //意见反馈
        binding.meFeedback.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), FeedbackActivity.class));
        });
        //推荐给好友
        binding.meRecommend.setOnClickListener(v -> {
            Share.prepareMessage(getActivity());
        });
        //关于
        binding.meAbout.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AboutActivity.class));
        });
        //公众号
        binding.meOfficialAccount.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                clickLogin();
                return;
            }

            OfficialActivity.start(getActivity());
        });
        //口语圈
        binding.speechCircle.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                clickLogin();
                return;
            }

            startActivity(MySpeechActivity.buildIntent(getActivity()));
        });
        //增加钱包记录
        binding.walletHistoryLayout.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                clickLogin();
                return;
            }

            WalletListActivity.start(getActivity());
        });
        //增加激励视频
        binding.rewardVideoAd.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                clickLogin();
                return;
            }

            AdContainerActivity.start(getActivity(), AdShowUtil.NetParam.AdShowPosition.show_rewardVideo);
        });
        //购买记录
        binding.mePayMark.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                clickLogin();
                return;
            }

            startActivity(PurchaseRecordActivity.buildIntent(getActivity()));
        });
        //视频界面
        binding.videoShow.setOnClickListener(v -> {
            isToVideo = true;
            VideoShowActivity.start(getActivity());
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(mContext);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        EventBus.getDefault().unregister(this);
    }

    public void startChangeName() {
        if (!UserInfoManager.getInstance().isLogin()) {
            ToastUtil.showToast(mContext, "登录后才能修改用户名。");
            return;
        }
        Intent intent = new Intent(mContext, ChangeNameActivity.class);
        intent.putExtra(ChangeNameActivity.RegisterMob, 0);
        startActivity(intent);
    }

    //登录
    void clickLogin() {
        NewLoginUtil.startToLogin(getActivity());
    }

    public void showToast(int resId) {
        Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startDownload(int bookId) {
        downloadDialog = new DownloadDialog(getContext());
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
        if (App.APP_SHOW_SUBPAGE > 0) {
            return;
        }
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
                if (downloadEvent.downloadId == 1000) {
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
            ToastUtil.showToast(mContext, "下载全部课程资源，需要打开网络数据连接");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("是否需要下载首页课程的课文，音频，视频及单词全部课程资源？该过程可能需要耗费一些流量及时间，最好打开Wifi下载。");
        builder.setNegativeButton("不需要", (dialog, which) -> {
            dialog.dismiss();
            downAlert = null;
        });
        builder.setPositiveButton("下载", (dialog, which) -> {
            dialog.dismiss();
            downAlert = null;
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

    private LoadingDialog mLoadingDialog;

    @Override
    public void showLoadingDialog() {
        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.show();
    }

    @Override
    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    //登录回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEvent event) {
        setDes();
        mPresenter.enterGroup();
    }

    //登出的回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginOutEvent event) {
        setDes();
    }

    //刷新用户信息回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UserInfoRefreshEvent event){
        setDes();
    }

    //用户更换头像的回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UserPhotoChangeEvent event) {
        /*Glide.clear(binding.meUserImage);
        Glide.with(mContext)
                .load(mPresenter.getUserImageUrl())
                .asBitmap()
                .signature(new StringSignature(System.currentTimeMillis()+""))
                .transform(new CircleTransform(mContext))
                .placeholder(R.drawable.default_avatar)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.meUserImage);*/
        Glide3Util.loadHeadImg(mContext, mPresenter.getUserImageUrl(), R.drawable.default_avatar, binding.meUserImage);
    }

    //共通模块的收藏界面
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FavorItemEvent fEvent) {
        //收藏页面点击
        if (fEvent == null) {
            ToastUtil.show(getActivity(), "目前暂时不支持跳转");
            return;
        }
        BasicFavorPart fPart = fEvent.items.get(fEvent.position);
        goFavorItem(fPart);
    }

    private void goFavorItem(BasicFavorPart part) {
        switch (part.getType()) {
            case "news":
            case "voa":
            case "csvoa":
            case "bbc":
                startActivity(AudioContentActivityNew.buildIntent(getActivity(), part.getCategoryName(), part.getTitle(), part.getTitleCn(), part.getPic(), part.getType(), part.getId(), part.getSound()));
                break;
            case "song":
                startActivity(AudioContentActivity.buildIntent(getActivity(), part.getCategoryName(), part.getTitle(), part.getTitleCn(), part.getPic(), part.getType(), part.getId(), part.getSound()));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "topvideos":
            case "japanvideos":
                startActivity(VideoContentActivityNew.buildIntent(getActivity(), part.getCategoryName(), part.getTitle(), part.getTitleCn(), part.getPic(), part.getType(), part.getId(), part.getSound()));
                break;
//            case "series":
//                Intent intent = SeriesActivity.buildIntent(this, part.getSeriesId(), part.getId());
//                startActivity(intent);
//                break;
            case "smallvideo":
                startActivity(VideoMiniContentActivity.buildIntentForOne(getActivity(), part.getId(), 0, 1, 1));
                break;
        }
    }
}
