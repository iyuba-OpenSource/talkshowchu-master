package com.iyuba.talkshow.ui.detail.ranking.watch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.Ranking;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.event.DownloadEvent;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.OkhttpUtil;
import com.iyuba.talkshow.util.RxUtil;
import com.iyuba.talkshow.util.StorageUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
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
import timber.log.Timber;

@ConfigPersistent
public class WatchDownloadPresenter extends BasePresenter<WatchDownloadMvpView> {
    private static final long SHOW_PEROID = 300;

    private final DataManager mDataManager;

    private Voa mVoa;
    private Ranking mRanking;
    private String mDir;
    private String mVideoUrl;

    private Subscription mGetVoaSub;
    private Subscription mSyncVoaSub;

    private Timer mMsgTimer;
    private String mMsg;

    private final FileDownloadListener mVideoListener = new FileDownloadListener() {
        private int mFileLength = 0;
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.e("DetailDownloadPresenter", "startFileDownload:pending soFarBytes = " + soFarBytes);
            Log.e("DetailDownloadPresenter", "startFileDownload:pending totalBytes = " + totalBytes);
        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            mFileLength = totalBytes;
            Log.e("DetailDownloadPresenter", "startFileDownload:connected soFarBytes = " + soFarBytes);
            Log.e("DetailDownloadPresenter", "startFileDownload:connected totalBytes = " + totalBytes);
        }

        @Override
        protected void progress(BaseDownloadTask task, int progress, int totalBytes) {
            Log.e("DetailDownloadPresenter", "startFileDownload:progress progress = " + progress);
            Log.e("DetailDownloadPresenter", "startFileDownload:progress totalBytes = " + totalBytes);
            mFileLength = totalBytes;
            if (mFileLength != 0) {
                int percent = progress * 100 / mFileLength;
                if (getMvpView() != null) {
                    mMsg = MessageFormat.format(
                            ((Context) getMvpView()).getString(R.string.video_loading_tip), percent);
                }
            }
        }

        @Override
        protected void blockComplete(BaseDownloadTask task) {
        }

        @Override
        protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
            Log.e("DetailDownloadPresenter", "startFileDownload:progress soFarBytes = " + soFarBytes);
            Log.e("DetailDownloadPresenter", "startFileDownload:progress retryingTimes = " + retryingTimes);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            Log.e("DetailDownloadPresenter", "startFileDownload:completed" );
            StorageUtil.renameVideoFile(mDir, mRanking.id());
            if (checkFileExist()) {
                mMsgTimer.cancel();
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, mRanking.id()));
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.e("DetailDownloadPresenter", "startFileDownload:paused soFarBytes = " + soFarBytes);
            Log.e("DetailDownloadPresenter", "startFileDownload:paused totalBytes = " + totalBytes);
            mMsgTimer.cancel();
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            Log.e("DetailDownloadPresenter", "startFileDownload:error" );
            mMsgTimer.cancel();
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            Log.e("DetailDownloadPresenter", "startFileDownload:warn" );
        }
    };

    @Inject
    public WatchDownloadPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetVoaSub);
        RxUtil.unsubscribe(mSyncVoaSub);
    }

    public void init(Voa voa, Ranking ranking) {
        this.mVoa = voa;
        this.mRanking = ranking;
        mDir = StorageUtil
                .getMediaRankingDir((Context) getMvpView(), voa.voaId())
                .getAbsolutePath();
    }

    public void download() {
        mMsgTimer = new Timer();
        mMsgTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mMsg != null) {
                    EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.DOWNLOADING, mMsg, mRanking.id()));
                }
            }
        }, 0, SHOW_PEROID);

        getVoaTexts(mVoa.voaId());
        if (!StorageUtil.isVideoExist(mDir, mRanking.id())) {
            downloadVideo();
        }
    }

    private void downloadVideo() {
        mVideoUrl = Constant.Url.getNewDubbingUrl(mRanking.videoUrl());
//        mDLManager.dlStart(mVideoUrl, mDir,
//                StorageUtil.getVideoTmpFilename(mRanking.id()), mVideoListener);
        // init the download task
        String file = mDir + "/" + StorageUtil.getVideoTmpFilename(mRanking.id());
        Log.e("DetailDownloadPresenter", "addImageDownloadList:file " + file);
        FileDownloader.getImpl().create(mVideoUrl).setTag(mVideoUrl).setPath(file, false).setListener(mVideoListener).start();
    }

    public void cancelDownload() {
        if (mVideoUrl != null) {
//            mDLManager.dlCancel(mVideoUrl);
            FileDownloader.getImpl().pause(mVideoListener);
        }
        if (mMsgTimer != null) {
            mMsgTimer.cancel();
        }

        if (fileCall!=null){
            fileCall.cancel();
        }
    }

    public boolean checkFileExist() {
        return StorageUtil.isVideoExist(mDir, mRanking.id());
    }

    public void getVoaTexts(final int voaId) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetVoaSub);
        mGetVoaSub = mDataManager.getVoaTexts(voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<VoaText>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (!NetStateUtil.isConnected((Context) getMvpView())) {
                            getMvpView().showToastShort(R.string.please_check_network);
                        } else {
//                            getMvpView().showToastShort(R.string.request_fail);
                            syncVoaTexts(voaId);
                        }
                    }

                    @Override
                    public void onNext(List<VoaText> voaTextList) {
                        if (voaTextList.isEmpty()) {
                            syncVoaTexts(voaId);
                        }
                    }
                });
    }

    public void syncVoaTexts(final int voaId) {
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
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<VoaText> voaTextList) {
                        getMvpView().showVoaTextLit(voaTextList);
                    }
                });
    }

    public void deleteLocalFile() {
        File file = new File(mDir);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] fileList = file.listFiles();
                for (File f : fileList) {
                    if (f.getName().startsWith(mRanking.id() + "")) {
                        f.delete();
                        return;
                    }
                }
            }
        }
    }

    public boolean isSelf(int uid) {
        return UserInfoManager.getInstance().getUserId() == uid;
    }

    public Uri getVideoUri() {
        if (checkFileExist()) {
            Uri uri = Uri.fromFile(new File(mDir, StorageUtil.getVideoFilename(mRanking.id())));
            Timber.tag("tag").e("exist%s", uri.getPath());
            return uri ;
        } else {
            Uri uri = Uri.parse(Constant.Url.getNewDubbingUrl(mRanking.videoUrl()));
            Timber.tag("tag").e(Constant.Url.getNewDubbingUrl(mRanking.videoUrl()));
            return uri;
        }
    }

    /***********保存视频到相册*********/
    //获取下载的链接
    public Uri getDownloadVideoUri(){
        Uri uri = Uri.parse(Constant.Url.getNewDubbingUrl(mRanking.videoUrl()));
        return uri;
    }

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

                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, 1001));
            } catch (Exception e) {
                e.printStackTrace();
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.ERROR, 1001));
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
            EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, 1001));
            Log.d("下载测试", "本地存在");
            return;
        }

        if (localFile.exists()) {
            //这里如果存在本地的视频，则删除，然后进行下载，避免出现问题
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
                    EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.ERROR, 1001));
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response == null || response.body().byteStream() == null) {
                    EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.ERROR, 1001));
                    return;
                }


                try {
                    fileCall = call;

                    long max = response.body().contentLength();
                    InputStream is = response.body().byteStream();
                    FileOutputStream fos = new FileOutputStream(localPath);
                    byte[] bytes = new byte[1024];
                    int len = 0;

                    while ((len = is.read(bytes)) != -1) {
                        fos.write(bytes, 0, len);

                        progress+=len;
                        Log.d("下载进度", "onNext: --"+progress+"--"+max);
                        int percent = (int) (progress*100/max);
                        if (getMvpView()!=null){
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
                        EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.ERROR, 1001));
                    }
                }
            }
        });
    }
}