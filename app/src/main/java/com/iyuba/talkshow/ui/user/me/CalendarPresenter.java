package com.iyuba.talkshow.ui.user.me;

import android.util.Log;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.result.ShareInfoRecord;
import com.iyuba.talkshow.data.model.result.ShareInfoResponse;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by carl shen on 2021/4/23
 * New Primary English, new study experience.
 */
@ConfigPersistent
public class CalendarPresenter extends BasePresenter<CalendarMvpView>{
    private final DataManager mDataManager;
    private Subscription mGetRankingSub;

    @Inject
    public CalendarPresenter(DataManager dataManager) {
        this.mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetRankingSub);
    }

    public void getCalendar(String pageNum) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetRankingSub);
        getMvpView().showLoadingLayout();
        mGetRankingSub = mDataManager.getCalendar(UserInfoManager.getInstance().getUserId(), App.APP_ID, pageNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ShareInfoResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().dismissLoadingLayout();
                        getMvpView().showCalendar(new ArrayList<>());
                        if (e != null) {
                            Log.e("CalendarPresenter", "getCalendar onError " + e.getMessage());
                        }
                        if(!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                            getMvpView().showToastShort(R.string.please_check_network);
                        }
                    }

                    @Override
                    public void onNext(ShareInfoResponse response) {
                        getMvpView().dismissLoadingLayout();
                        if((response != null) && "200".equals(response.result)) {
                            Log.e("CalendarPresenter", "getCalendar onNext response.count " + response.count);
                            List<ShareInfoRecord> rankingList = response.record;
                            if ((rankingList == null) || (rankingList.size() < 1)) {
                                Log.e("DakaInfoPresenter", "getRanking onNext empty.");
                            } else {
                                getMvpView().showCalendar(rankingList);
                                return;
                            }
                        } else {
                            Log.e("CalendarPresenter", "getCalendar onNext empty.");
                        }
                        getMvpView().showCalendar(new ArrayList<>());
                    }
                });
    }

}
