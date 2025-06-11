package com.iyuba.talkshow.ui.detail;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.dlex.bizs.DLTaskInfo;
import com.iyuba.dlex.interfaces.IDListener;
import com.iyuba.dlex.interfaces.SimpleDListener;
import com.iyuba.imooclib.data.model.Course;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.Download;
import com.iyuba.talkshow.data.model.IntegralBean;
import com.iyuba.talkshow.data.model.StudyRecord;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.event.DownloadEvent;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.OkhttpUtil;
import com.iyuba.talkshow.util.RxUtil;
import com.iyuba.talkshow.util.SelectPicUtils;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.talkshow.util.TimeUtil;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.talkshow.util.VoaMediaUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@ConfigPersistent
public class DetailDownloadPresenter extends BasePresenter<DetailMvpView> {
    private static final long SHOW_PEROID = 300;
    public static int TYPE_DOWNLOAD = 1000;
    public static int PDF_ENG = 1001;
    public static int PDF_CN = 1002;
    public static int PDF_BOTH = 1003;

    private final DataManager mDataManager;
    private DLManager mDLManager;
    private DLTaskInfo taska;
    private DLTaskInfo taskMedia;

    private Voa mVoa;
    private String mDir;
    private String mVideoUrl;
    private String mMediaUrl;

    private Subscription mAddDownloadSub;
    private Subscription mGetVoaSub;
    private Subscription mSyncVoaSub;
    private Subscription mIntegralSub;

    private Timer mMsgTimer;
    private String mMsg;

    private final IDListener mVideoListener = new IDListener() {
        private int mFileLength = 0;

        @Override
        public void onPrepare() {

        }

        @Override
        public void onStart(String fileName, String realUrl, int fileLength) {
            this.mFileLength = fileLength;
        }

        @Override
        public void onProgress(int progress) {
            if (mFileLength != 0) {
                int percent = progress * 100 / mFileLength;
                if (getMvpView() != null) {
                    mMsg = MessageFormat.format(
                            ((Context) getMvpView()).getString(R.string.video_loading_tip), percent);
                }
            }
        }

        @Override
        public void onStop(int progress) {
            if (mMsgTimer != null) {
                mMsgTimer.cancel();
            }
        }

        @Override
        public void onFinish(File file) {
            StorageUtil.renameVideoFile(mDir, mVoa.voaId());
            if (StorageUtil.checkFileExist(mDir, mVoa.voaId())) {
                addDownload();
            }
            mMsgTimer.cancel();
            EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, mVoa.voaId()));
        }

        @Override
        public void onError(int status, String error) {
            mMsgTimer.cancel();
            EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, mVoa.voaId()));
        }
    };

    private final IDListener mMediaListener = new IDListener() {
        private int mFileLength = 0;

        @Override
        public void onPrepare() {

        }

        @Override
        public void onStart(String fileName, String realUrl, int fileLength) {
            this.mFileLength = fileLength;
        }

        @Override
        public void onProgress(int progress) {
            if (mFileLength != 0) {

                int percent = progress * 100 / mFileLength;
                if (getMvpView() != null) {
                    mMsg = MessageFormat.format(
                            ((Context) getMvpView()).getString(R.string.media_loading_tip), percent);
                }
            }
        }

        @Override
        public void onStop(int progress) {
            if (mMsgTimer != null) {
                mMsgTimer.cancel();
            }
        }

        @Override
        public void onFinish(File file) {
            StorageUtil.renameAudioFile(mDir, mVoa.voaId());
            if (StorageUtil.checkFileExist(mDir, mVoa.voaId())) {
                mMsgTimer.cancel();
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, mVoa.voaId()));
                addDownload();
            } else {
                //下载视频
                if (!StorageUtil.isVideoExist(mDir, mVoa.voaId())) {
                    downloadVideo();
                } else {
                    mMsgTimer.cancel();
                    EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, mVoa.voaId()));
                }
            }
        }

        @Override
        public void onError(int status, String error) {
            if (getMvpView() != null) {
                mMsg = "音频资源下载错误，请稍后再试。";
            }
            //下载视频
            if (!StorageUtil.isVideoExist(mDir, mVoa.voaId())) {
                downloadVideo();
            } else {
                mMsgTimer.cancel();
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, mVoa.voaId()));
            }
        }
    };

    @Inject
    public DetailDownloadPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
        this.mDLManager = DLManager.getInstance();
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetVoaSub);
        RxUtil.unsubscribe(mSyncVoaSub);
        RxUtil.unsubscribe(mAddDownloadSub);
        RxUtil.unsubscribe(mIntegralSub);
    }

    public void init(Voa voa) {
        this.mVoa = voa;
        mDir = StorageUtil
                .getMediaDir(TalkShowApplication.getContext(), voa.voaId())
                .getAbsolutePath();
    }

    public void download() {
        mMsgTimer = new Timer();
        mMsgTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mMsg != null) {
                    EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.DOWNLOADING, mMsg, mVoa.voaId()));
                }
            }
        }, 0, SHOW_PEROID);

        if (!StorageUtil.isAudioExist(mDir, mVoa.voaId())) {
            downloadMedia();
        } else if (!StorageUtil.isVideoExist(mDir, mVoa.voaId())) {
            downloadVideo();
        }
    }

    private void downloadVideo() {
        mVideoUrl = VoaMediaUtil.getVideoUrl(mVoa.video());
        if (UserInfoManager.getInstance().isVip()){
            mVideoUrl = VoaMediaUtil.getVideoVipUrl(mVoa.video());
        }
//        mDLManager.dlStart(mVideoUrl, mDir,
//                StorageUtil.getVideoTmpFilename(mVoa.voaId()), mVideoListener);
        Log.e("DetailDownloadPresenter", " downloadVideo mVideoUrl " + mVideoUrl);
        // init the download task
        DLTaskInfo task = mDLManager.getDLTaskInfo(mVideoUrl);
        if (task != null) {
            task.setDListener(mVideoListener);
            mDLManager.reExecuteTask(task);
            return;
        }
        taska = new DLTaskInfo();
        taska.tag = mVideoUrl;
        taska.filePath = mDir;
        taska.fileName = StorageUtil.getVideoTmpFilename(mVoa.voaId());
        taska.category = Course.DOWNLOAD_VIDEO_CATEGORY;
        taska.initalizeUrl(mVideoUrl);
        taska.setDListener(mVideoListener);
        mDLManager.addDownloadTask(taska);
    }

    //mMediaUrl = VoaMediaUtil.getAudioUrl(mVoa);
    private void downloadMedia() {
//        mMediaUrl = UserInfoManager.getInstance().isVip() ?
        mMediaUrl =       VoaMediaUtil.getAudioUrl(mVoa.sound());
//        mDLManager.dlStart(mMediaUrl, mDir,
//                StorageUtil.getAudioTmpFilename(mVoa.voaId()), mMediaListener);
        Log.e("DetailDownloadPresenter", " downloadMedia mMediaUrl " + mMediaUrl);
        // init the download task
        DLTaskInfo task = mDLManager.getDLTaskInfo(mMediaUrl);
        if (task != null) {
            task.setDListener(mMediaListener);
            mDLManager.reExecuteTask(task);
            return;
        }
        taskMedia = new DLTaskInfo();
        taskMedia.tag = mMediaUrl;
        taskMedia.filePath = mDir;
        taskMedia.fileName = StorageUtil.getAudioTmpFilename(mVoa.voaId());
        taskMedia.category = Course.DOWNLOAD_VIDEO_CATEGORY;
        taskMedia.initalizeUrl(mMediaUrl);
        taskMedia.setDListener(mMediaListener);
        mDLManager.addDownloadTask(taskMedia);
    }

    public void cancelDownload() {
        if (taskMedia != null) {
            mDLManager.cancelTask(taskMedia);
            taskMedia = null;
        }

        if (taska != null) {
            mDLManager.cancelTask(taska);
            taska = null;
        }
        if (mMsgTimer != null) {
            mMsgTimer.cancel();
        }

        //取消文件下载
        if (fileCall!=null){
            fileCall.cancel();
        }
    }

    public boolean checkFileExist() {
        return StorageUtil.checkFileExist(mDir, mVoa.voaId());
    }

    public void addDownload() {
        checkViewAttached();
        RxUtil.unsubscribe(mAddDownloadSub);
        mAddDownloadSub = mDataManager.saveDownload(
                Download.builder()
                        .setUid(UserInfoManager.getInstance().getUserId())
                        .setVoaId(mVoa.voaId())
                        .setDate(TimeUtil.getCurDate())
                        .build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("DetailDownloadPresenter", "addDownload onError  " + e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });
    }

    public void syncVoaTexts(final int voaId) {
        Log.e("DetailDownloadPresenter", "syncVoaTexts voaId " + voaId);
        checkViewAttached();
        RxUtil.unsubscribe(mSyncVoaSub);
        mSyncVoaSub = mDataManager.syncVoaTexts(voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<VoaText>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("DetailDownloadPresenter", "syncVoaTexts onError  " + e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(List<VoaText> voaTextList) {
                        getMvpView().showVoaTextLit(voaTextList);
                    }
                });
    }

    public void setWordCount(List<VoaText> voaTextList, final StudyRecord studyRecord) {
        if (!voaTextList.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (VoaText text : voaTextList) {
                builder.append(text.sentence());
            }
            studyRecord.setWordCount(builder.toString());
        }
    }

    public boolean checkIsFree() {
        if (UserInfoManager.getInstance().isVip()) {
            return true;
        }
//        return mDataManager.getVoaDownloadNumber() < 5;
        return false;
    }

    public void addFreeDownloadNumber() {
        if (!UserInfoManager.getInstance().isVip()) {
            mDataManager.addVoaDownloadNumber();
        }
    }

    //这里怎么处理的，mvoa数据为null都没有测试出来，服气了
    //在上边有个初始化init的方法，在其他界面中调用即可
    public void deductIntegral(int type) {
        checkViewAttached();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        String flag = null;
        try {
            flag = Base64.encodeToString(
                    URLEncoder.encode(df.format(new Date(System.currentTimeMillis())), "UTF-8").getBytes(), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mIntegralSub = mDataManager.deductIntegral(flag, UserInfoManager.getInstance().getUserId(), App.APP_ID, mVoa.voaId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<IntegralBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        //这里部分界面没有绑定，无法调用这个方法
                        getMvpView().showToast(" 积分扣取失败！"+e.getMessage());
//                        ToastUtil.showToast(TalkShowApplication.getInstance()," 积分扣取失败！"+e.getMessage());
                    }

                    @Override
                    public void onNext(IntegralBean bean) {
                        if ("200".equals(bean.result)) {
                            getMvpView().onDeductIntegralSuccess(type);
                        } else {
//                            getMvpView().showToast("Code：" + bean.result + " 积分扣取失败！");
                            getMvpView().showToast("积分不足，扣取失败！");
//                            ToastUtil.showToast(TalkShowApplication.getInstance(),"Code：" + bean.result + " 积分扣取失败！");
//                            ToastUtil.showToast(TalkShowApplication.getInstance(),"积分不足，扣取失败！");
                        }
                    }
                });
    }

    DLTaskInfo task = null;
    public void onDownloadPdf(Context mContext, String title, String url) {
        String downPath = "/sdcard/iyuba/" + App.APP_NAME_EN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            downPath = getExternalFilesDir(null).getAbsolutePath();
            SelectPicUtils.downloadQFile(mContext, url, title + ".pdf");
            ToastUtil.showToast(mContext, "文件将会下载到 /sdcard/Download/" + title + ".pdf");
            return;
        } else {
            File audioDir = new File(downPath);
            if (!audioDir.exists()) {
                audioDir.mkdirs();
            }
        }
        ToastUtil.showToast(mContext, "文件将会下载到 " + downPath + "/" + title + ".pdf");
        File audioFile = new File(downPath,  title + ".pdf");
        if (audioFile.exists()) {
            audioFile.delete();
        }
        if (mDLManager == null) {
            mDLManager = DLManager.getInstance();
        }
        if (task == null) {
            task = new DLTaskInfo();
            task.tag = title + ".pdf";
            task.filePath = downPath;
            task.fileName = title + ".pdf";
            task.initalizeUrl(url);
            task.setDListener(new DListener(downPath));
            mDLManager.addDownloadTask(task);
        } else {
            switch (task.state) {
                case DLTaskInfo.TaskState.INIT:
                    ToastUtil.showToast(mContext, "正在初始化");
                    break;
                case DLTaskInfo.TaskState.WAITING:
                    ToastUtil.showToast(mContext, "下载任务正在等待");
                    break;
                case DLTaskInfo.TaskState.PREPARING:
                case DLTaskInfo.TaskState.DOWNLOADING:
                    mDLManager.stopTask(task);
                    break;
                case DLTaskInfo.TaskState.ERROR:
                case DLTaskInfo.TaskState.PAUSING:
                    task.setDListener(new DListener(downPath));
                    mDLManager.resumeTask(task);
                    break;
                default:
                    break;
            }
        }
    }
    private class DListener extends SimpleDListener {
        private int total;
        private final String mDownPath;
        DListener(String headlines) {
            mDownPath = headlines;
        }

        @Override
        public void onStart(String fileName, String realUrl, int fileLength) {
            total = fileLength;
        }

        @Override
        public void onProgress(int progress) {
            int percentage = getCurrentPercentage(progress);
        }

        @Override
        public void onStop(int progress) {
        }

        @Override
        public void onFinish(File file) {
            task = null;
            Log.e("DetailActivity", "DListener onFinish mDownPath " + mDownPath);
        }

        @Override
        public void onError(int status, String error) {
            Log.e("DetailActivity", "DListener onError status " + status);
            Log.e("DetailActivity", "DListener onError error " + error);
        }

        private int getCurrentPercentage(int progress) {
            int result = 0;
            if (total >= 10000) {
                result = progress / (total / 100);
            } else if (total > 0) {
                result = (progress * 100) / total;
            }
            return result;
        }
    }

    /***********保存视频到相册*********/
    //判断相册中有无视频
    public boolean checkAlbumVideoExist(String fileName) {
        Cursor cursor = TalkShowApplication.getContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.Media._ID}, MediaStore.Video.Media.DISPLAY_NAME + " = '" + fileName + "'", null, null);
        boolean result = false;
        if (cursor != null) {
            result = cursor.getCount() > 0;
            cursor.close();
        }
        return result;
    }

    //将本地视频导入到相册
    private void importLocalVideoToAlbum(String localPath) {
        if (TextUtils.isEmpty(localPath)){
            return;
        }

        File localVideoFile = new File(localPath);
        if (!localVideoFile.exists()){
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, localVideoFile.getName());
        contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
        } else {
            contentValues.put(MediaStore.Video.Media.DATA, Environment.getExternalStorageDirectory().getPath() + File.separator + Environment.DIRECTORY_DCIM + File.separator + localVideoFile.getName());
        }

        Uri fileUri = TalkShowApplication.getContext().getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
        if (fileUri != null) {
            try {
                ParcelFileDescriptor pfd = TalkShowApplication.getContext().getContentResolver().openFileDescriptor(fileUri, "w");
                FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());

                FileInputStream fis = new FileInputStream(localPath);
                byte[] bytes = new byte[1024];
                int len = 0;

                while ((len = fis.read(bytes)) != -1) {
                    fos.write(bytes, 0, len);
                }
                fos.close();
                fis.close();

                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, 1002));
            } catch (Exception e) {
                e.printStackTrace();
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.ERROR, 1002));
            }
        }
    }

    //下载本地视频并且导入到相册
    public void downVideoAndImportAlbum(String videoUrl,String localPath) {
        //获取本地视频和相册视频路径
        File localFile = new File(localPath);

        //1.先判断相册中有无视频
        //2.判断本地有无视频，有则直接导入到相册中
        //3.都没有视频则直接下载
        if (checkAlbumVideoExist(localFile.getName())) {
            EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, 1002));
            Log.d("下载测试", "本地存在");
            return;
        }

        if (new File(localPath).exists()) {
            try {
                localFile.delete();
            }catch (Exception e){
                e.printStackTrace();
            }
            Log.d("下载测试", "删除本地文件");
        }

        downloadAlbumVideo(videoUrl, localPath);
    }

    //可中断的call
    private Call fileCall = null;
    //下载文件
    private long progress = 0;
    public void downloadAlbumVideo(String videoUrl,String localPath) {
        fileCall = null;
        progress = 0;

        //这里判断file存在并且创建file
        try {
            File localFile = new File(localPath);
            if (!localFile.exists()) {
                if (!localFile.getParentFile().exists()) {
                    localFile.getParentFile().mkdirs();
                }
                localFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        OkhttpUtil.downloadFile(videoUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (!call.isCanceled()){
                    EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.ERROR, 1002));
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response == null || response.body().byteStream() == null) {
                    EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.ERROR, 1002));
                    return;
                }

                try {
                    fileCall = call;

                    long max = response.body().contentLength();
                    InputStream is = response.body().byteStream();
                    FileOutputStream fos = new FileOutputStream(localPath);
                    byte[] bytes = new byte[4096];
                    int len = 0;

                    while ((len = is.read(bytes)) != -1) {
                        fos.write(bytes, 0, len);

                        progress += len;
                        int percent = (int) (progress * 100 / max);
                        if (getMvpView() != null) {
                            mMsg = MessageFormat.format(((Context) getMvpView()).getString(R.string.saveAlbum_loading_tip), percent);
                        }
                        if (mMsg != null) {
                            EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.DOWNLOADING, mMsg, mVoa.voaId()));
                        }
                    }

                    fos.flush();
                    fos.close();
                    importLocalVideoToAlbum(localPath);
                } catch (IOException e) {
                    if (!call.isCanceled()){
                        EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.ERROR, 1002));
                    }
                }
            }
        });
    }
}