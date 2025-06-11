package com.iyuba.talkshow.ui.user.edit;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.util.Pair;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.model.result.EditUserBasicInfoResponse;
import com.iyuba.talkshow.data.model.result.location.GetLocationElement;
import com.iyuba.talkshow.data.model.result.location.GetLocationResponse;
import com.iyuba.talkshow.data.model.result.location.LocationElement;
import com.iyuba.talkshow.data.remote.LocationService;
import com.iyuba.talkshow.data.remote.UserService;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.GetLocation;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/12/22/022.
 */

@ConfigPersistent
public class EditUserInfoPresenter extends BasePresenter<EditUserInfoMvpView> {
    private final DataManager mDataManager;
    private final ConfigManager mConfigManager;
    private final GetLocation mGetLocation;

    private Subscription mEditSub;
    private Subscription mGetLocationSub;

    @Inject
    public EditUserInfoPresenter(DataManager mDataManager,
                                 ConfigManager configManager, GetLocation getLocation) {
        this.mDataManager = mDataManager;
        this.mConfigManager = configManager;
        this.mGetLocation = getLocation;
    }

    public void edit(String key, String value) {
        checkViewAttached();
        RxUtil.unsubscribe(mEditSub);
        mEditSub = mDataManager.editUserBasicInfo(UserInfoManager.getInstance().getUserId(), key, value)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<EditUserBasicInfoResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().dismissLoadingDialog();
                        getMvpView().setSaveBtnClickable(true);
                        if(!NetStateUtil.isConnected((Context) getMvpView())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                    }

                    @Override
                    public void onNext(EditUserBasicInfoResponse response) {
                        getMvpView().dismissLoadingDialog();
                        getMvpView().setSaveBtnClickable(true);
                        if(response != null && response.result() != null
                                && response.result() == UserService.EditUser.Result.SUCCESS) {

                            getMvpView().showToast(R.string.edit_success);
                        } else {
                            getMvpView().showToast(R.string.edit_failure);
                        }
                    }
                });
    }

    public Pair<String, String> getEditKeyValue(String gender, String birthday, String constellation,
                                                String zodiac, String collage, String location) {
        String key;
        StringBuilder sb = new StringBuilder();
        Log.e("性别：",gender);

        String genderCode = UserService.GetUserBasicInfo.Result.getCodeByMessage(gender);
        Log.e("性别：",gender+"code:"+ genderCode);
        String[] birth = birthday.split(UserService.EditUserBasicInfo.Param.SEP);

        sb.append(genderCode).append(UserService.EditUserBasicInfo.Param.COMMA)
                .append(birth[0]).append(UserService.EditUserBasicInfo.Param.COMMA)
                .append(birth[1]).append(UserService.EditUserBasicInfo.Param.COMMA)
                .append(birth[2]).append(UserService.EditUserBasicInfo.Param.COMMA)
                .append(constellation).append(UserService.EditUserBasicInfo.Param.COMMA)
                .append(zodiac).append(UserService.EditUserBasicInfo.Param.COMMA)
                .append(collage);

        if(location != null && location.contains(UserService.EditUserBasicInfo.Param.SPACE)) {
            String[] locals = location.split(UserService.EditUserBasicInfo.Param.SPACE);
            for(String local : locals) {
                sb.append(UserService.EditUserBasicInfo.Param.COMMA).append(local);
            }
            if(locals.length == UserService.EditUserBasicInfo.Param.SIZE_THREE) {
                key = UserService.EditUserBasicInfo.Param.Key.getLocationSizeThreeKey();
            } else {
                key = UserService.EditUserBasicInfo.Param.Key.getLocationSizeTwoKey();
            }
        } else {
            sb.append(UserService.EditUserBasicInfo.Param.COMMA).append(location);
            key = UserService.EditUserBasicInfo.Param.Key.getLocationSizeOnekey();
        }

        return new Pair<>(key, sb.toString());
    }

    String getUserImageUrl() {
        return Constant.Url.getMiddleUserImageUrl(
                UserInfoManager.getInstance().getUserId(),
                mConfigManager.getPhotoTimestamp());
    }

    public void getLocation() {
        getMvpView().showLoadingDialog();
        Location location = mGetLocation.getLocation();
        if(location == null) {
            getMvpView().dismissLoadingDialog();
            getMvpView().showToast(R.string.please_open_gps);
        } else {
            checkViewAttached();
            RxUtil.unsubscribe(mGetLocationSub);
            mGetLocationSub = mDataManager.getLocation(location.getLatitude(), location.getLongitude())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<GetLocationResponse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            getMvpView().dismissLoadingDialog();
                            if(!NetStateUtil.isConnected((Context) getMvpView())) {
                                getMvpView().showToast(R.string.please_check_network);
                            } else {
                                getMvpView().showToast(R.string.request_fail);
                            }
                        }

                        @Override
                        public void onNext(GetLocationResponse response) {
                            getMvpView().dismissLoadingDialog();
                            if(TextUtils.equals(response.status(),
                                    LocationService.GetLocation.Result.Code.SUCCESS)) {
                                List<GetLocationElement> elements = response.results();
                                getMvpView().setLocationTv(getLocation(elements.get(0)));
                            }
                        }
                    });
        }
    }

    private String getLocation(GetLocationElement getLocation) {
        List<LocationElement> locations = getLocation.addressComponents();
        String province = "";
        String city = "";
        String subCity = "";
        for(LocationElement location : locations) {
            for(String type : location.types()) {
                switch (type) {
                    case LocationService.GetLocation.Result.Value.PROVINCE:
                        province = location.shortName();
                        break;
                    case LocationService.GetLocation.Result.Value.CITY:
                        city = location.shortName();
                        break;
                    case LocationService.GetLocation.Result.Value.SUB_CITY:
                        subCity = location.shortName();
                        break;
                }
            }
        }
        return LocationService.GetLocation.Result.Value.getLocation(province, city, subCity);
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mEditSub);
        RxUtil.unsubscribe(mGetLocationSub);
    }
}
