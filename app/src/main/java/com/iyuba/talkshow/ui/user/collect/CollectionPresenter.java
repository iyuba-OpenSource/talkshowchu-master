package com.iyuba.talkshow.ui.user.collect;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.Collect;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.RxUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/12/27/027.
 */

@ConfigPersistent
public class CollectionPresenter extends BasePresenter<CollectionMvpView>{
    private final DataManager mDataManager;

    private Subscription mGetCollectionSub;
    private Subscription mDeleteCollectionSub;

    @Inject
    public CollectionPresenter(DataManager dataManager) {
        this.mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetCollectionSub);
        RxUtil.unsubscribe(mDeleteCollectionSub);
    }

    public List<Voa> getVoaById(int voaid) {
        return mDataManager.getVoaByVoaId(voaid);
    }

    public void getCollection() {
        checkViewAttached();
        RxUtil.unsubscribe(mGetCollectionSub);
        int uid = UserInfoManager.getInstance().getUserId();
        mGetCollectionSub = mDataManager.getCollect(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Collect>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().setAdapterData("数据查询异常，请重试～",null);
                    }

                    @Override
                    public void onNext(List<Collect> collects) {
                        getMvpView().dismissLoadingLayout();
                        if(collects.isEmpty()) {
                            getMvpView().setAdapterData("暂无文章收藏数据",new ArrayList<>());
                        } else {
                            getMvpView().setAdapterData(null,collects);
                        }
                    }
                });
    }

    public void deleteCollection(List<String> list) {
        checkViewAttached();
        RxUtil.unsubscribe(mDeleteCollectionSub);

        mDeleteCollectionSub = mDataManager.deleteCollect(UserInfoManager.getInstance().getUserId(), list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if(aBoolean) {
                            getCollection();
                            getMvpView().showToast(R.string.delete_success);
                        } else {
                            getMvpView().showToast(R.string.delete_failure);
                        }
                    }
                });
    }

    public int getUnitId(Voa voa){
        return mDataManager.getUnitId4Voa(voa);
    }
}
