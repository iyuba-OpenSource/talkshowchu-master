package com.iyuba.talkshow.ui.feedback;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.manager.VersionManager;
import com.iyuba.talkshow.data.model.result.FeedbackResponse;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@ConfigPersistent
public class FeedbackPresenter extends BasePresenter<FeedBackMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    private static final String LEFT_BRACKET = "[";
    private static final String RIGHT_BRACKET = "]";
    private static final String COLON = ":";
    private static final String APP_VERSION = "  appversion";
    private static final String VERSION_CODE = "versionCode";
    private static final String PHONE = "phone";
    private static final String SDK = "sdk";
    private static final String SYS_VERSION = "sysversion";

    @Inject
    public FeedbackPresenter(DataManager dataManager) {
        this.mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mSubscription);
    }

    private String buildInfo() {
        return "  " + APP_VERSION + COLON +
                LEFT_BRACKET + VersionManager.VERSION_NAME + RIGHT_BRACKET +
                VERSION_CODE + COLON +
                LEFT_BRACKET + VersionManager.VERSION_CODE +
                PHONE + COLON +
                LEFT_BRACKET + android.os.Build.BRAND + android.os.Build.MODEL + android.os.Build.DEVICE +
                SDK + COLON +
                LEFT_BRACKET + android.os.Build.VERSION.SDK_INT + RIGHT_BRACKET +
                SYS_VERSION + COLON +
                LEFT_BRACKET + android.os.Build.VERSION.RELEASE + RIGHT_BRACKET;
    }

    private boolean verification(String content, String email) {
        if ("".equals(content.trim())) {
            getMvpView().setEdtContentError(R.string.feedback_info_null);
            return false;
        }

        if (!"".equals(email.trim())) {
            if (!emailCheck(email.trim())) {
                getMvpView().setEdtEmailError(R.string.feedback_effective_email);
                return false;
            }
        } else {
            if (!UserInfoManager.getInstance().isLogin()) {
                getMvpView().setEdtEmailError(R.string.feedback_email_null);
                return false;
            }
        }
        return true;
    }

    private boolean emailCheck(String email) {
        String check = "^([a-z0-ArrayA-Z]+[-_|\\.]?)+[a-z0-ArrayA-Z]@([a-z0-ArrayA-Z]+(-[a-z0-ArrayA-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(email);
        return matcher.matches();
    }

    public void submit(String content, String email) {
        if (verification(content, email)) {
            checkViewAttached();
            RxUtil.unsubscribe(mSubscription);
            getMvpView().showWaitingDialog();
            int uid = UserInfoManager.getInstance().getUserId();
            mSubscription = mDataManager.submitFeedback(uid, email,
                    content + buildInfo() + Constant.Voa.FEEDBACK_END)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<FeedbackResponse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if(!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                                getMvpView().showToast(R.string.please_check_network);
                            } else {
                                getMvpView().showToast(R.string.request_fail);
                            }
                        }

                        @Override
                        public void onNext(FeedbackResponse feedbackResponse) {
                            getMvpView().dismissWaitingDialog();
                            getMvpView().showDialog();
                        }
                    });
        }
    }
}
