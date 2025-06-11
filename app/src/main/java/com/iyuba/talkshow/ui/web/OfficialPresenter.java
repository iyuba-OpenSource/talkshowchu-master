package com.iyuba.talkshow.ui.web;

import android.util.Log;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.OfficialResponse;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;
import com.iyuba.wordtest.db.OfficialAccount;
import com.iyuba.wordtest.db.OfficialAccountDao;
import com.iyuba.wordtest.db.WordDataBase;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by carl shen on 2021/6/7
 * New Primary English, new study experience.
 */
@ConfigPersistent
public class OfficialPresenter extends BasePresenter<OfficialMvpView>{
    private final DataManager mDataManager;

    private Subscription mGetCollectionSub;

    @Inject
    public OfficialPresenter(DataManager dataManager) {
        this.mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetCollectionSub);
    }

    public List<Voa> getVoaById(int voaid) {
        return mDataManager.getVoaByVoaId(voaid);
    }

    public void getOfficialAccount(int pageNum, int pageSize) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetCollectionSub);
        getMvpView().showLoadingLayout();
        mGetCollectionSub = mDataManager.getOfficialAccount(pageNum, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OfficialResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().dismissLoadingLayout();
                        if(!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                        if (e != null) {
                            Log.e("OfficialPresenter", "getOfficialAccount onError  " + e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(OfficialResponse response) {
                        getMvpView().dismissLoadingLayout();
                        if ((response == null) || (response.data == null)) {
                            getMvpView().setEmptyAccount();
                            Log.e("OfficialPresenter", "getOfficialAccount response == null ");
                            return;
                        }
                        Log.e("OfficialPresenter", "getOfficialAccount data.size " + response.data.size());
                        if(response.result == 200) {
                            getMvpView().setDataAccount(response.data);
                            TalkShowApplication.getSubHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    OfficialAccountDao artDao = WordDataBase.getInstance(TalkShowApplication.getContext()).getOfficialAccountDao();
                                    if (artDao == null) {
                                        Log.e("OfficialPresenter", "getOfficialAccountDao is null. ");
                                        return;
                                    }
                                    for (OfficialAccount bean: response.data) {
                                        if (bean == null) {
                                            continue;
                                        }
                                        OfficialAccount article = artDao.getOfficialAccount(bean.id);
                                        if (article == null) {
                                            artDao.saveOfficialAccount(bean);
                                        } else {
                                            artDao.updateOfficialAccount(bean);
                                        }
                                    }
                                }
                            });
                        } else {
                            getMvpView().setEmptyAccount();
                        }
                    }
                });
    }

    public void getMoreAccount(int pageNum, int pageSize) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetCollectionSub);
        getMvpView().showLoadingLayout();
        mGetCollectionSub = mDataManager.getOfficialAccount(pageNum, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OfficialResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().dismissLoadingLayout();
                        if(!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                        if (e != null) {
                            Log.e("OfficialPresenter", "getOfficialAccount onError  " + e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(OfficialResponse response) {
                        getMvpView().dismissLoadingLayout();
                        if ((response == null) || (response.data == null)) {
                            getMvpView().showToast(R.string.request_no_more);
                            Log.e("OfficialPresenter", "getOfficialAccount response == null ");
                            return;
                        }
                        Log.e("OfficialPresenter", "getOfficialAccount data.size " + response.data.size());
                        if(response.result == 200) {
                            getMvpView().setMoreAccount(response.data);
                            TalkShowApplication.getSubHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    OfficialAccountDao artDao = WordDataBase.getInstance(TalkShowApplication.getContext()).getOfficialAccountDao();
                                    if (artDao == null) {
                                        Log.e("OfficialPresenter", "getOfficialAccountDao is null. ");
                                        return;
                                    }
                                    for (OfficialAccount bean: response.data) {
                                        if (bean == null) {
                                            continue;
                                        }
                                        OfficialAccount article = artDao.getOfficialAccount(bean.id);
                                        if (article == null) {
                                            artDao.saveOfficialAccount(bean);
                                        } else {
                                            artDao.updateOfficialAccount(bean);
                                        }
                                    }
                                }
                            });
                        } else {
                            getMvpView().showToast(R.string.request_no_more);
                        }
                    }
                });
    }

    public void getLocalAccount(int categoryId, int pageNum, int mPageCount) {
        checkViewAttached();
        OfficialAccountDao artDao = WordDataBase.getInstance(TalkShowApplication.getContext()).getOfficialAccountDao();
        if (artDao == null) {
            Log.e("OfficialPresenter", "getLocalAccount OfficialAccountDao is null. ");
            return;
        }
        List<OfficialAccount> accountList;
        if (categoryId == 0) {
            accountList = artDao.getSizeOfficialAccount(mPageCount);
        } else {
            accountList = artDao.getMaxOfficialAccount(categoryId, mPageCount);
        }
        getMvpView().dismissLoadingLayout();
        if (accountList == null) {
            Log.e("OfficialPresenter", "accountList is null. ");
            getMvpView().setEmptyAccount();
            return;
        } else {
            Log.e("OfficialPresenter", "accountList size " + accountList.size());
        }
        if (categoryId == 0) {
            getMvpView().setDataAccount(accountList);
        } else {
            getMvpView().setMoreAccount(accountList);
        }
    }

}
