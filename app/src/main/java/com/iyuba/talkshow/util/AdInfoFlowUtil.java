package com.iyuba.talkshow.util;

import android.content.Context;
import android.util.Log;

import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.youdao.sdk.nativeads.NativeAds;
import com.youdao.sdk.nativeads.NativeErrorCode;
import com.youdao.sdk.nativeads.NativeResponse;
import com.youdao.sdk.nativeads.YouDaoMultiNative;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * AdInfoFlowUtil
 *
 * @author wayne
 * @date 2017/11/30
 */
public class AdInfoFlowUtil {
    private int START_POSITION = -2;
    private int requestSize = 3;
    private int videoRequestSize = 2;

    private boolean supportVideo = false;

    private YouDaoMultiNative multiNative;
    private YouDaoMultiNative multiVideoNative;

    private final List allAds = new ArrayList();
    public List adList = new ArrayList<>();
    public List videoAdList = new ArrayList<>();

    private final Callback mCallback;
    private boolean isVip = false;
    public int lastAdPosition = START_POSITION;
    private boolean isAdReponse = false;
    private boolean isVideoAdReponse = false;

    public static synchronized int insertAD4(List mDataList, List ads, AdInfoFlowUtil util) {
        int count = 1;
        if (mDataList.size() > util.lastAdPosition) {
            int index = util.lastAdPosition;
            // 有广告，且有足够的数据
            while (ads.size() > 0 && (mDataList.size() >= index + 4)) {
                try {
                    index += (count % 2 == 1) ? 7 : 5;
                    Log.e("insert pos ", index + "");
                    mDataList.add(index, ads.get(0));
                    ads.remove(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                count += 1;
            }
            util.lastAdPosition = mDataList.size() - 2;
        }
        return util.lastAdPosition;
    }

    public static synchronized int insertAD(List mDataList, List ads, AdInfoFlowUtil util) {
        if (mDataList.size() > util.lastAdPosition) {
            int index = util.lastAdPosition;
            // 有广告，且有足够的数据
            while (ads.size() > 0 && (mDataList.size() >= index + 4)) {
                index += 4;
                mDataList.add(index, ads.get(0));
                ads.remove(0);
            }
            util.lastAdPosition = mDataList.size() - 2;
        }
        return util.lastAdPosition;
    }


    public interface Callback {
        void onADLoad(List ads);
    }

    public AdInfoFlowUtil(Context context, boolean vip, final Callback callback) {
        this.isVip = vip;
        this.mCallback = callback;
        // 47a9a23756450e1f3c79aa21899885eb  慢速原生信息流视频样式
        // 3438bae206978fec8995b280c49dae1e
        if (multiNative == null)
        multiNative = new YouDaoMultiNative(context, ConfigData.YOUDAO_AD_STEAM_CODE, new YouDaoMultiNative.YouDaoMultiNativeNetworkListener() {
            @Override
            public void onNativeLoad(NativeAds nativeAds) {
                if ((nativeAds == null) || (nativeAds.getNativeResponses() == null)) {
                    Log.e("AdInfoFlowUtil", "onNativeLoad nativeAds is null.");
                    return;
                }
                List<NativeResponse> responses = nativeAds.getNativeResponses();
                int count = responses.size();
                for (int i = 0; i < count; i++) {
                    adList.add(responses.get(i));
                    Timber.e(responses.get(i).toString());
                }
                isAdReponse = true;
                Timber.e("info flow youDao ad  onNativeLoad ------");
                callback(callback);
            }

            @Override
            public void onNativeFail(NativeErrorCode nativeErrorCode) {
                Timber.e("info flow youDao: onNativeFail :  " + nativeErrorCode.name() + "  --  Message : " + nativeErrorCode);
                isAdReponse = true;
                callback(callback);
            }
        });
        if (multiVideoNative == null)
        multiVideoNative = new YouDaoMultiNative(context, "47a9a23756450e1f3c79aa21899885eb", new YouDaoMultiNative.YouDaoMultiNativeNetworkListener() {
            @Override
            public void onNativeLoad(NativeAds nativeAds) {
                if ((nativeAds == null) || (nativeAds.getNativeResponses() == null)) {
                    Log.e("AdInfoFlowUtil", "onNativeLoad nativeAds is null.");
                    return;
                }
                List<NativeResponse> responses = nativeAds.getNativeResponses();
                int count = responses.size();
                for (int i = 0; i < count; i++) {
                    videoAdList.add(responses.get(i));
                    Timber.e(responses.get(i).toString());
                }
                Timber.e("info flow youDao ad  onNativeLoad ------");
                isVideoAdReponse = true;
                callback(callback);
            }

            @Override
            public void onNativeFail(NativeErrorCode nativeErrorCode) {
                Timber.e("info flow youDao: onNativeFail :  " + nativeErrorCode.name() + "  --  Message : " + nativeErrorCode);
                isVideoAdReponse = true;
                callback(callback);
            }
        });
    }

    private void callback(Callback callback) {
        if ((isAdReponse && isVideoAdReponse) || (isAdReponse && !supportVideo)) {
            List list = new ArrayList();
            if (adList.size() > 0 && videoAdList.size() == 0) {
                list.addAll(adList);
            } else if (adList.size() == 0 && videoAdList.size() > 0) {
                list.addAll(videoAdList);
            } else if (adList.size() > 0 && videoAdList.size() > 0) {
                try {
                    while (adList.size() > 0 || videoAdList.size() > 0) {
                        if (adList.size() > 0) {
                            list.add(adList.get(0));
                            adList.remove(0);
                        }
                        if (videoAdList.size() > 0) {
                            list.add(videoAdList.get(0));
                            videoAdList.remove(0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            allAds.addAll(list);
            callback.onADLoad(list);
        }
    }

    public List getAdList() {
        return allAds;
    }

    public void refreshAd() {

        if (isVip) {
            Log.d("com.iyuba.talkshow", "refreshAd: "+isVip);
            return;
        }
        Log.d("com.iyuba.talkshow", "refreshAd: "+isVip);
        Timber.e("info flow refresh ad ------");
        isAdReponse = false;
        isVideoAdReponse = false;
        adList.clear();
        videoAdList.clear();

        multiNative.refreshRequest(requestSize);
        if (supportVideo) {
            multiVideoNative.makeRequest(videoRequestSize);
        }
    }

    public void destroy() {
        multiNative.destroy();
        multiVideoNative.destroy();
    }

    public AdInfoFlowUtil resetLastPosition() {
        lastAdPosition = START_POSITION;
        return this;
    }

    public void reset() {
        resetLastPosition();
        adList.clear();
        videoAdList.clear();
        allAds.clear();
    }

    public AdInfoFlowUtil setStartPos(int pos) {
        START_POSITION = pos;
        return this;
    }

    public void setVip(boolean isVip) {
        this.isVip = isVip;
        if (!isVip) {
            lastAdPosition = START_POSITION;
        }
    }

    public AdInfoFlowUtil setAdRequestSize(int size) {
        this.requestSize = size;
        return this;
    }

    public AdInfoFlowUtil setVideoAdRequestSize(int size) {
        this.videoRequestSize = size;
        return this;
    }

    public AdInfoFlowUtil setSupportVideo(boolean supportVideo) {
        this.supportVideo = supportVideo;
        return setAdRequestSize(3);
    }
}
