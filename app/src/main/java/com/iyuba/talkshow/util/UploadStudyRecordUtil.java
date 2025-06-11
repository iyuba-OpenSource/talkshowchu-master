package com.iyuba.talkshow.util;

import android.content.Context;
import android.util.Log;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.model.StudyRecord;
import com.iyuba.talkshow.data.model.UploadRecordResult;
import com.iyuba.talkshow.data.remote.UploadStudyRecordService;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_mvp.util.BigDecimalUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * UploadStudyRecordUtil
 *
 * @author wayne
 * @date 2018/2/8
 */
public class UploadStudyRecordUtil {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    SimpleDateFormat sdf222 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    private final StudyRecord studyRecord;
    private final int userId;
    private final String getLocalDeviceType;
    private final String getLocalMACAddress;
    boolean isFirst = true;

    public UploadStudyRecordUtil(boolean login, Context context, int uid, int lessonId, String testNum, String mode) {

        //去掉获取mac地址操作
//        GetDeviceInfo getDeviceInfo = new GetDeviceInfo(context);
//        getLocalDeviceType = getDeviceInfo.getLocalDeviceType();
//        getLocalMACAddress = getDeviceInfo.getLocalMACAddress();
        getLocalDeviceType = "";
        getLocalMACAddress = "";

        this.userId = uid;
        studyRecord = new StudyRecord();
        studyRecord.setLesson(App.APP_NAME_EN);
        studyRecord.setLessonid(lessonId + "");
        studyRecord.setTestNumber(testNum);
        studyRecord.setTestmode(mode);
    }

    public UploadStudyRecordUtil(boolean login, Context context, int uid, int lessonId, String testNum) {

        //去掉获取mac地址操作
//        GetDeviceInfo getDeviceInfo = new GetDeviceInfo(context);
//        getLocalDeviceType = getDeviceInfo.getLocalDeviceType();
//        getLocalMACAddress = getDeviceInfo.getLocalMACAddress();
        getLocalDeviceType = "";
        getLocalMACAddress = "";

        this.userId = uid;
        studyRecord = new StudyRecord();
        studyRecord.setLesson(App.APP_NAME_EN);
        studyRecord.setLessonid(lessonId + "");
        studyRecord.setTestNumber(testNum);
    }

    public void stopStudyRecord(final Context context, boolean login, String flag, UploadStudyRecordService service) {

        Observable<UploadRecordResult> observable = stopAndUpload(login, flag, service);
        if (observable == null) {
            if (flag.equals("1")){
                EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.reward_refresh_toast,""));
            }
            return;
        }
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UploadRecordResult>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (flag.equals("1")){
                            EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.reward_refresh_toast,""));
                        }
                    }

                    @Override
                    public void onNext(UploadRecordResult uploadRecordResult) {
                        if (uploadRecordResult == null) {
                            if (flag.equals("1")){
                                EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.reward_refresh_toast,""));
                            }
                            return;
                        }
                        Log.e("UploadStudyRecordUtil", "uploadRecordResult.getResult() " + uploadRecordResult.getResult());
                        if ("1".equals(uploadRecordResult.getResult())) {
                            //显示奖励
                            double price = Integer.parseInt(uploadRecordResult.getReward());
                            if (price>0){
                                price = BigDecimalUtil.trans2Double(price*0.01f);
                                String showMsg = String.format(ResUtil.getInstance().getString(R.string.reward_show),price);

                                EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.reward_refresh_toast,String.valueOf(price)));
                            }else {
                                if (flag.equals("1")){
                                    EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.reward_refresh_toast,""));
                                }
                            }
                        }else {
                            if (flag.equals("1")){
                                EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.reward_refresh_toast,""));
                            }
                        }
                    }
                });
    }

    public StudyRecord getStudyRecord() {
        return studyRecord;
    }

    public Observable<UploadRecordResult> stopAndUpload(boolean isLogin, String flag, UploadStudyRecordService service) {
        if (!isLogin) {
            return null;
        }
        if (flag.equals("0") && isFirst) {
            isFirst = false;
//        } else {
//            return null;
        }
        studyRecord.setEndtime(System.currentTimeMillis());
        studyRecord.setFlag(flag);

        if (studyRecord.getEndtime() - studyRecord.getStarttime() < 3 * 1000) {
            Log.e("UploadStudyRecordUtil", "stopAndUpload duration is less 3s.");
            return null;
        }
        Log.e("UploadStudyRecordUtil", "stopAndUpload duration  " + (studyRecord.getEndtime() - studyRecord.getStarttime()));

        return uploadStudyRecord(userId + "", service, studyRecord);
    }

    public String getFormatTime(SimpleDateFormat format, long currentTime) {
        return format.format(new Date(currentTime));
    }

    public Observable<UploadRecordResult> uploadStudyRecord(String userId,
                                                            UploadStudyRecordService service,
                                                            StudyRecord studyRecord) {

        //增加奖励机制
        int rewardVersion = 1;

        return service.uploadStudyRecord(
                "json",
                App.APP_ID + "",
                App.APP_NAME_CH,
                studyRecord.getLesson(),
                studyRecord.getLessonid(),
                userId,
                getLocalDeviceType,
                getLocalMACAddress,
                getFormatTime(sdf222, studyRecord.getStarttime()),
                getFormatTime(sdf222, studyRecord.getEndtime()),
                studyRecord.getFlag(),
                studyRecord.getWordCount(),
                studyRecord.getTestmode(),
                "android",
                studyRecord.getTestNumber(),
                MD5.getMD5ofStr(userId + getFormatTime(sdf222, studyRecord.getStarttime()) + sdf.format(new Date())),
                rewardVersion
        );
    }
}
