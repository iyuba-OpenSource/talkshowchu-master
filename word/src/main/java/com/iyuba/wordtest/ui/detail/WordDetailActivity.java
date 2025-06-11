package com.iyuba.wordtest.ui.detail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.iyuba.module.commonvar.CommonVars;
import com.iyuba.wordtest.R;
import com.iyuba.wordtest.bean.SendEvaluateResponse;
import com.iyuba.wordtest.data.AppData;
import com.iyuba.wordtest.databinding.ActivityWordDetail2Binding;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.db.WordOp;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.entity.TalkshowTexts;
import com.iyuba.wordtest.entity.WordEntity;
import com.iyuba.wordtest.manager.WordConfigManager;
import com.iyuba.wordtest.manager.WordManager;
import com.iyuba.wordtest.network.HttpManager;
import com.iyuba.wordtest.ui.TalkshowWordListActivity;
import com.iyuba.wordtest.ui.test.WordTestActivity;
import com.iyuba.wordtest.utils.FastClickUtil;
import com.iyuba.wordtest.utils.FileUtils;
import com.iyuba.wordtest.utils.LibRxTimer;
import com.iyuba.wordtest.utils.MediaUtils;
import com.iyuba.wordtest.utils.PermissionDialogUtil;
import com.iyuba.wordtest.utils.RecordManager;
import com.iyuba.wordtest.utils.SentenceSpanUtils;
import com.iyuba.wordtest.utils.Share;
import com.iyuba.wordtest.utils.StorageUtil;
import com.iyuba.wordtest.utils.TextAttr;
import com.iyuba.wordtest.utils.ToastUtil;
import com.iyuba.wordtest.widget.BubblePopupWindow;
import com.iyuba.wordtest.widget.EvalCorrectDialog;
import com.jaeger.library.StatusBarUtil;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 *
 */
@RuntimePermissions
public class WordDetailActivity extends AppCompatActivity implements WordDetailMvpView {
    private String imageHeaderUrl = "http://static2." + CommonVars.domain + "/images/words/";
    private ProgressDialog dialog;
    private TalkshowTexts text;
    private String TAG = WordDetailActivity.class.getSimpleName();
    private Disposable playDis;
    private CompoundButton.OnCheckedChangeListener collectListener;
    private String mDir;

    int bookId;
    int unit;
    int step;

    public static void start(Context context, List<TalkShowWords> words, int position, int book_id, int unit) {
        Intent intent = new Intent(context, WordDetailActivity.class);
        intent.putExtra("word", (Serializable) words);
        intent.putExtra(TalkshowWordListActivity.STEP, position);
        intent.putExtra(TalkshowWordListActivity.BOOKID, book_id);
        intent.putExtra(TalkshowWordListActivity.UNIT, unit);
        context.startActivity(intent);
    }

    WordDetailPresenter presenter;
    WordDataBase db;
    private WordOp wordOp;

    private String VIDEO_PREFIX = "http://staticvip." + CommonVars.domain + "/video/voa/";
    private String Header = "http://iuserspeech." + CommonVars.domain.replace("/", "") + ":9001/voa/";
    List<TalkShowWords> talkShowWords = new ArrayList<>();
    File file;
    int position;
    private String sentenceUrl;
    TalkShowWords talkshowWord;
    AnimationDrawable animation;
    AnimationDrawable animation_record;
    AnimationDrawable animation_own;
    RecordManager recordManager;
    Context context;
    MediaPlayer player;

    private boolean isSentence = true;
    ActivityWordDetail2Binding binding;
    private boolean isAutoPlay = false;
    private EvalCorrectDialog fragment;

    //是否正在录音
    private boolean isRecording = false;
    //是否正在评测
    private boolean isEvaling = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWordDetail2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
        presenter = new WordDetailPresenter();
        presenter.attachView(this);
        talkShowWords = (List<TalkShowWords>) getIntent().getExtras().get("word");
        //如果只有一个，则隐藏
        if (talkShowWords!=null&&talkShowWords.size()==1){
            binding.ivLast.setVisibility(View.GONE);
            binding.ivNext.setVisibility(View.GONE);
        }
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        context = this;
        db = WordDataBase.getInstance(this);
        player = new MediaPlayer();
        player.setOnPreparedListener(iMediaPlayer -> iMediaPlayer.start());
        player.setOnCompletionListener(iMediaPlayer -> {
            iMediaPlayer.stop();
            stopAnimation();
        });
        bookId = getIntent().getIntExtra(TalkshowWordListActivity.BOOKID, 217);
        unit = getIntent().getIntExtra(TalkshowWordListActivity.UNIT, 1);
        step = getIntent().getIntExtra(TalkshowWordListActivity.STEP, 0);
        position = step;
        mDir = StorageUtil.getMediaDir(getApplicationContext(), bookId, unit).getAbsolutePath();

        //动画
//        animation = (AnimationDrawable) binding.imgOriginal.getDrawable();
//        animation_own = (AnimationDrawable) binding.imgOwn.getDrawable();
//        animation_record = (AnimationDrawable) binding.imgRecord.getDrawable();
        binding.imgOriginal.setImageResource(R.drawable.wordtest_speaker_2);
        binding.imgRecord.setImageResource(R.drawable.record0);
        binding.imgOwn.setImageResource(R.drawable.own_record3);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        wordOp = new WordOp(context);
        refreshUI();
        setCLick();
        imageHeaderUrl = "http://static2." + CommonVars.domain + "/images/words/";
        VIDEO_PREFIX = "http://staticvip." + CommonVars.domain + "/video/voa/";
        Header = "http://iuserspeech." + CommonVars.domain.replace("/", "") + ":9001/voa/";
    }

    private void setCLick() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
//        binding.btnAuto.setOnClickListener(v -> onViewClicked(v));
        binding.btnMore.setOnClickListener(v-> onViewClicked(v));
        binding.ivLast.setOnClickListener(v -> onViewClicked(v));
        binding.ivNext.setOnClickListener(v -> onViewClicked(v));
//        binding.cbCollect.setOnClickListener(v -> onViewClicked(v));
        initCollectListener();
        binding.cbCollect.setOnCheckedChangeListener(collectListener);
        binding.imgOriginal.setOnClickListener(v -> onViewClicked(v));
        binding.imgOwn.setOnClickListener(v -> onViewClicked(v));
        binding.imgRecord.setOnClickListener(v -> onViewClicked(v));
        binding.imgSpeaker.setOnClickListener(v -> onViewClicked(v));
//        binding.imgSwift.setOnClickListener(v -> onViewClicked(v));
        binding.wordImg.setOnClickListener(v -> onViewClicked(v));
        binding.videoPlay.setOnClickListener(v -> onViewClicked(v));
        binding.imgRecord.setOnClickListener(v -> onViewClicked(v));
    }

    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.iv_last) {
            if (isRecording||isEvaling) {
                ToastUtil.showToast(context, "正在录音评测中...");
                return;
            }

            if (position != 0) {
                position--;
                refreshUI();
            }
            stopPlayer();
        } else if (id == R.id.btn_auto) {
            isAutoPlay = !isAutoPlay;
            if (isAutoPlay) {
                ToastUtil.showToast(context, "开启单词自动发音");
//                binding.btnAuto.setImageResource(R.drawable.ic_auto_open);
                WordConfigManager.Instance(context).putBoolean("autoplay", true);
            } else {
                ToastUtil.showToast(context, "关闭单词自动发音");
//                binding.btnAuto.setImageResource(R.drawable.ic_auto_close);
                WordConfigManager.Instance(context).putBoolean("autoplay", false);
            }
        } else if (id == R.id.iv_next) {
            if (FastClickUtil.fastClick()){
                return;
            }

            if (isRecording||isEvaling) {
                ToastUtil.showToast(context, "正在录音评测中...");
                return;
            }

            if (position != talkShowWords.size() - 1) {
                position++;
                refreshUI();
            } else {
                WordTestActivity.start(context, bookId, unit, step);
            }
            stopPlayer();
        } else if (id == R.id.img_original) {
            if (FastClickUtil.fastClick()){
                return;
            }

            if (isRecording||isEvaling) {
                ToastUtil.showToast(context, "正在录音评测中...");
                return;
            }

            if (binding.videoView.isPlaying()){
                stopAnimation();
                return;
            }

            if (!isSentence) {
                playAudio(talkshowWord.word, false, false, true);
            } else {
                playAudio(talkshowWord.Sentence_audio, true, false, true);
            }
        } else if (id == R.id.img_swift) {
            if (isRecording||isEvaling) {
                ToastUtil.showToast(context, "正在录音评测中...");
                return;
            }

            binding.llScore.setVisibility(View.INVISIBLE);
//            binding.imgLowScore.setVisibility(View.INVISIBLE);
            binding.wordCorrect.setVisibility(View.INVISIBLE);
            isSentence = !isSentence;
            setSentenceTxt();
        } else if (id == R.id.img_speaker) {
            if (FastClickUtil.fastClick()){
                return;
            }

            if (isRecording||isEvaling) {
                ToastUtil.showToast(context, "正在录音评测中...");
                return;
            }

            if (binding.videoView.isPlaying()){
                return;
            }

            playAudio(talkshowWord.word, false, false, false);
        } else if (id == R.id.img_own) {
            if (FastClickUtil.fastClick()){
                return;
            }

            if (isRecording||isEvaling) {
                ToastUtil.showToast(context, "正在录音评测中...");
                return;
            }

            if (binding.videoView.isPlaying()){
                stopAnimation();
                return;
            }

            playAudio(talkshowWord.word, isSentence, true, true);
        } else if (id == R.id.img_record) {
            if (FastClickUtil.fastClick()){
                return;
            }

            if (player!=null&&player.isPlaying()) {
                player.stop();
                stopAnimation();
            }

            if (binding!=null&&binding.videoView.isPlaying()) {
                binding.videoView.stopPlayback();
                showVideoView(false);
            }

            if (isRecording){
                stopRecord();
                dialog.show();
                presenter.sendEvaluate(binding.txtSentence.getText().toString(), talkshowWord, isSentence, HttpManager.fromFile(file));
                return;
            }

            //显示权限说明弹窗
            List<Pair<String, Pair<String,String>>> pairList = new ArrayList<>();
            pairList.add(new Pair<>(Manifest.permission.RECORD_AUDIO,new Pair<>("麦克风权限","录制评测时朗读的音频，用于评测打分使用")));
            pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE,new Pair<>("存储权限","保存评测的音频文件，用于评测打分使用")));
            PermissionDialogUtil.getInstance().showMsgDialog(this, pairList, new PermissionDialogUtil.OnPermissionResultListener() {
                @Override
                public void onGranted(boolean isSuccess) {
                    if (isSuccess){

                        //录音或者停止录音
                        if (!isRecording){
                            startRecord(talkshowWord.word);
                        }
                    }
                }
            });
        } else if (id == R.id.word_img || id == R.id.video_play) {
            if (FastClickUtil.fastClick()){
                return;
            }

            if (isRecording||isEvaling) {
                ToastUtil.showToast(context, "正在录音评测中...");
                return;
            }

            if (player.isPlaying()) {
                return;
            }

            if (!TextUtils.isEmpty(talkshowWord.videoUrl) && talkshowWord.voa_id > 30000) {
                playVideo();
            } else {
                ToastUtil.showToast(context, "例句无视频");
            }
        }else if (id == R.id.btn_more){
            if (FastClickUtil.fastClick()){
                return;
            }

            showMenu();
        }
    }

    private void showVideoView(boolean show) {
        if (show) {
            binding.videoView.setVisibility(View.VISIBLE);
            binding.wordImg.setVisibility(View.GONE);
            binding.videoPlay.setVisibility(View.GONE);
            binding.videoLoading.setVisibility(View.GONE);
        } else {
            binding.videoView.setVisibility(View.GONE);
            binding.wordImg.setVisibility(View.VISIBLE);
            binding.videoPlay.setVisibility(View.VISIBLE);
            binding.videoLoading.setVisibility(View.GONE);
        }
    }

    private void playVideo() {
        String videoUri;
        binding.videoView.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion() {
                showVideoView(false);
            }
        });

        //关闭按钮，显示加载
        binding.videoPlay.setVisibility(View.GONE);
        binding.videoLoading.setVisibility(View.VISIBLE);
        binding.videoLoading.setProgressTintList(ColorStateList.valueOf(Color.GRAY));

        if (!TextUtils.isEmpty(talkshowWord.videoUrl)) {
            String tmpDir = StorageUtil.getMediaDir(this, talkshowWord.videoUrl).getAbsolutePath();
            if (StorageUtil.isVideoClipExist(tmpDir, talkshowWord.videoUrl)) {
                videoUri = new File(tmpDir, StorageUtil.getVideoClipFilename(talkshowWord.videoUrl)).getAbsolutePath();
            } else {
                videoUri = talkshowWord.videoUrl;
            }
            Log.e(TAG, "playVideo videoUri: " + videoUri);
            binding.videoView.setBackgroundColor(Color.TRANSPARENT);
            binding.videoView.setVideoURI(Uri.parse(videoUri));
            //这里为了去除上次播放的界面残留，增加时间延迟（按理说应该获取surface进行处理的）
            new Handler().postDelayed(() -> runOnUiThread(() -> showVideoView(true)),250);
//            showVideoView(true);
            binding.videoView.start();
            return;
        }

        String mDir = StorageUtil
                .getMediaDir(this, talkshowWord.voa_id)
                .getAbsolutePath();
        if (StorageUtil.isVideoExist(mDir, talkshowWord.voa_id)) {
            videoUri = new File(mDir, StorageUtil.getVideoFilename(talkshowWord.voa_id)).getAbsolutePath();
        } else {
            videoUri = VIDEO_PREFIX + talkshowWord.voa_id / 1000 + "/" + talkshowWord.voa_id + ".mp4";
        }

        binding.videoView.setVideoURI(Uri.parse(videoUri));
        //这里为了去除上次播放的界面残留，增加时间延迟（按理说应该获取surface进行处理的）
        new Handler().postDelayed(() -> runOnUiThread(() -> showVideoView(true)),250);
//        showVideoView(true);
        if (null == text) {
            binding.videoView.start();
            return;
        }
        int timing = (int) (Float.parseFloat(text.timing) * 1000);
        final int endTiming = (int) (Float.parseFloat(text.endTiming) * 1000);
        binding.videoView.seekTo(timing);
        binding.videoView.setOnSeekCompletionListener(() -> {
            binding.videoView.start();
            startVideoOb(endTiming);
        });
    }

    private void startVideoOb(final int endTiming) {
        if (playDis != null) playDis.dispose();
        playDis = Observable.interval(100, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
//                    Log.d(TAG, "accept: ");
                    if (binding.videoView.getCurrentPosition() > endTiming) {
                        binding.videoView.pause();
                        showVideoView(false);
                        playDis.dispose();
                    }
                });
    }

    private void setSentenceTxt() {
        if (isSentence) {
            if (TextUtils.isEmpty(talkshowWord.Sentence)) {
                showSentenceFail();
            } else {
                binding.txtSentenceCh.setText(talkshowWord.Sentence_cn);
                if (!TextUtils.isEmpty(talkshowWord.word) && talkshowWord.Sentence.toLowerCase().contains(talkshowWord.word.toLowerCase())) {
                    String[] wordList = talkshowWord.Sentence.split(" ");
                    StringBuilder stringBuilder = new StringBuilder(64);
                    boolean flag = false;
                    for (String word : wordList) {
                        if (TextUtils.isEmpty(word)) {
                            continue;
                        }
                        String wordReal = word.replaceAll("[?:!.,;\"]*([a-zA-Z]+)[?:!.,;\"]*", "$1");
                        if (word.equalsIgnoreCase(talkshowWord.word)) {
                            stringBuilder.append("<b>").append(word).append("</b> ");
                            flag = true;
                        } else if (talkshowWord.word.equalsIgnoreCase(wordReal)) {
                            int index = word.toLowerCase().indexOf(wordReal.toLowerCase());
                            stringBuilder.append(word.substring(0, index)).append("<b>").append(wordReal).append("</b>")
                                    .append(word.substring(index + wordReal.length())).append(" ");
                            flag = true;
                        } else {
                            stringBuilder.append(word).append(" ");
                        }
                    }
                    if (flag) {
                        binding.txtSentence.setText(Html.fromHtml(stringBuilder.toString()));
                    } else {
                        int index = talkshowWord.Sentence.toLowerCase().indexOf(talkshowWord.word.toLowerCase());
                        //这里不要将例句中的内容替换为单词内容了
                        String showWord = talkshowWord.Sentence.substring(index,index+talkshowWord.word.length());
                        binding.txtSentence.setText(Html.fromHtml(talkshowWord.Sentence.substring(0, index)
                                + "<b>" + showWord + "</b>" + talkshowWord.Sentence.substring(index + talkshowWord.word.length())));
                    }
                } else {
                    binding.txtSentence.setText(talkshowWord.Sentence);
                }
            }
        } else {
            binding.txtSentence.setText(talkshowWord.word);
            binding.txtSentenceCh.setText(talkshowWord.def);
        }
    }

    void initCollectListener() {
        collectListener = (buttonView, isChecked) -> {
            if (isChecked) {
//                if (talkshowWord.flag == 1) return;
//                talkshowWord.flag = 1;
//                db.getTalkShowWordsDao().insertWord(talkshowWord);
                if (wordOp.isFavorWord(talkshowWord.word, talkshowWord.voa_id, Integer.parseInt(WordManager.getInstance().userid))) {
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WordEntity wordEntity = new WordEntity();
                        wordEntity.key = talkshowWord.word;
                        wordEntity.audio = talkshowWord.audio;
                        wordEntity.pron = talkshowWord.pron;
                        wordEntity.def = talkshowWord.def;
                        wordEntity.unit = talkshowWord.unit_id;
                        wordEntity.book = talkshowWord.book_id;
                        wordEntity.voa = talkshowWord.voa_id;
                        wordOp.insertWord(wordEntity, Integer.parseInt(WordManager.getInstance().userid));
                    }
                }).start();
                addNetwordWord(talkshowWord.word);
            } else {
//                if (talkshowWord.flag == 0) return;
//                talkshowWord.flag = 0;
//                db.getTalkShowWordsDao().insertWord(talkshowWord);
                if (!wordOp.isFavorWord(talkshowWord.word, talkshowWord.voa_id, Integer.parseInt(WordManager.getInstance().userid))) {
                    return;
                }
                deleteNetWord(talkshowWord.word);
            }
        };
    }

    private void playAudio(String word, boolean isSentence, boolean isOwn, boolean startAnim) {
        if (player.isPlaying()) {
            player.pause();
            stopAnimation();
//            return;
        }
        try {
            player.reset();
            if (isSentence && !isOwn) {
                playSentenceAudio();
            } else if (isOwn) {
                playUserAudio();
            } else {
                playWord(startAnim);
            }
            player.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            stopAnimation();
        }
    }

    private void playSentenceAudio() throws IOException {
        if (TextUtils.isEmpty(talkshowWord.Sentence_audio)) {
            return;
        }
        if (StorageUtil.isAudioExist(mDir, talkshowWord.position)) {
            player.setDataSource(mDir + "/" + talkshowWord.position + ".mp3");
        } else {
            boolean downloaded = false;
            for (TalkShowWords talkShowWordTemp : talkShowWords) {
                if (talkShowWordTemp.Sentence_audio!=null
                        && talkShowWordTemp.Sentence_audio.equals(talkshowWord.Sentence_audio)
                        && StorageUtil.isAudioExist(mDir, talkShowWordTemp.position)) {
                    downloaded = true;
                    player.setDataSource(mDir + "/" + talkShowWordTemp.position + ".mp3");
                    break;
                }
            }
            if (!downloaded) {
                if (MediaUtils.isConnected(getApplicationContext())) {
                    player.setDataSource(this, Uri.parse(talkshowWord.Sentence_audio));
                } else {
                    showText("暂时没有这个句子的音频，请打开数据网络播放。");
                    return;
                }
            }
        }
//        Log.d(TAG, "playAudio: " + talkshowWord.Sentence_audio);
        startPlayAnim();
    }

    private void playUserAudio() throws IOException {
//        Log.e(TAG, "playUserAudio: " + Header + sentenceUrl);
        player.setDataSource(Header + sentenceUrl);
        startEvalAnim();
    }

    private void playWord(boolean startAnim) throws IOException {
        if (TextUtils.isEmpty(talkshowWord.audio)) {
            presenter.getNetWord(talkshowWord.word);
            return;
        }
        String dir = StorageUtil.getWordDir(this).getAbsolutePath();
        File file = new File(dir, StorageUtil.getWordName(talkshowWord.audio));
        String worDir = StorageUtil.getWordDir(this).getAbsolutePath() + "/primary_audio/" + StorageUtil.getWordName(talkshowWord.audio);
        File wordFile = new File(worDir);
        if (file.exists() && file.isFile()) {
//            Log.d(TAG, "playWord file.getAbsolutePath(): " + file.getAbsolutePath());
            this.player.setDataSource(file.getAbsolutePath());
        } else if (wordFile.exists() && wordFile.isFile()) {
//            Log.e(TAG, "playWord worDir.getAbsolutePath(): " + worDir);
            this.player.setDataSource(worDir);
        } else {
//            Log.d(TAG, "playWord talkshowWord.audio: " + talkshowWord.audio);
            if (MediaUtils.isConnected(getApplicationContext())) {
                this.player.setDataSource(talkshowWord.audio);
            } else {
                showText("暂时没有这个单词的音频，请打开数据网络播放。");
                return;
            }
        }
        if (startAnim) {
            startPlayAnim();
        }
    }

    private void stopRecord() {
        stopAnimation();
        stopTimer();

        binding.clickRecordHint.setText("点击开始");
        if (recordManager != null) {
            recordManager.stopRecord();
        }
        isRecording = false;
    }

    @NeedsPermission({Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void startRecord(String word) {
        file = FileUtils.getNewFile(getApplicationContext(), word);
        binding.clickRecordHint.setText("点击停止");
        binding.wordCorrect.setVisibility(View.INVISIBLE);
        //显示动画
        startRecordAnim();
        //开始录音
        recordManager = new RecordManager(file);
        recordManager.startRecord();
        isRecording = true;

        //使用MediaPlayer进行时间推算
//        long playTime = getAudioPlayTime();
        long playTime = 0;
        if (playTime == 0){
            if (isSentence){
                playTime = 6000L;
            }else {
                playTime = 3000L;
            }
        }else {
            playTime+=3000L;
        }
        startTimer(playTime);
    }

    /*************************计时器****************************/
    private static final String timer_record = "timer_record";

    private void startTimer(long delayTime){
        LibRxTimer.getInstance().multiTimerInMain(timer_record, 0, 200L, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                long allTime = number*200L;

                if (isRecording&&allTime>=delayTime){
                    stopTimer();
                    stopRecord();
                    dialog.show();

                    isEvaling = true;
                    presenter.sendEvaluate(binding.txtSentence.getText().toString(), talkshowWord, isSentence, HttpManager.fromFile(file));
                }
            }
        });
    }

    private void stopTimer(){
        LibRxTimer.getInstance().cancelTimer(timer_record);
    }

    private void hideButton() {
        if (position!=0&&position!=talkShowWords.size()-1){
            binding.ivNext.setVisibility(View.VISIBLE);
            binding.ivLast.setVisibility(View.VISIBLE);
        }else {
            if (position==0){
                binding.ivLast.setVisibility(View.INVISIBLE);
            }

            if (position == talkShowWords.size() - 1) {
                binding.ivNext.setVisibility(View.INVISIBLE);
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void refreshUI() {
        /*处理上一个下一个的显示*/
        hideButton();
        /*隐藏视频*/
        showVideoView(false);

        //临时处理：判断是否数组越界
        if (position>=talkShowWords.size()){
            return;
        }

        talkshowWord = talkShowWords.get(position);
        //这里增加部分数据处理操作，从源头上处理各种空格问题
        talkshowWord.word = talkshowWord.word.trim();

        if (binding.videoView.isPlaying()) {
            binding.videoView.stopPlayback();
        }
        binding.cbCollect.setChecked((wordOp != null) && wordOp.isFavorWord(talkshowWord.word, talkshowWord.voa_id, Integer.parseInt(WordManager.getInstance().userid)));
        if (talkshowWord.voa_id > 0) {
            presenter.getSentence(talkshowWord, this);  //从数据库获取例句 后期会删去? 暂时不删了
        }
        setSentenceTxt(); // 设置例句汉语和英文
        binding.llOwn.setVisibility(View.INVISIBLE); // 个人配音不可见
        binding.videoView.setVisibility(View.GONE); //  视频不可见
        binding.clickRecordHint.setText("点击录音");
        String mImageDirPath = StorageUtil.getImageUnzipDir(getApplicationContext(), talkshowWord.book_id).getAbsolutePath();
        if (!TextUtils.isEmpty(talkshowWord.pic_url) && StorageUtil.isImageExists(mImageDirPath, talkshowWord.pic_url)) {
            File file = new File(mImageDirPath, talkshowWord.pic_url);
            Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
            binding.wordImg.setImageBitmap(bm);
        } else if (!TextUtils.isEmpty(talkshowWord.pic_url)) {
            Log.e(TAG, "imageHeaderUrl = " + imageHeaderUrl + talkshowWord.pic_url);
            try {
                /*Glide.with(this).load(imageHeaderUrl + talkshowWord.pic_url)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                            ToastUtils.showShort("图片资源加载失败");
                                if (e != null) {
                                    Log.d(TAG, "refreshUI onException: " + e.getMessage());
                                }
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                return false;
                            }
                        }).into(binding.wordImg);*/

                Glide.with(this).load(imageHeaderUrl + talkshowWord.pic_url).into(binding.wordImg);
            } catch (Exception arg) {
                Log.e(TAG, "refreshUI glide pic_url exception: " + arg.getMessage());
            }
        } else {
            Log.e(TAG, "imageHeaderUrl pic_url is null. ");
            binding.wordImg.setVisibility(View.GONE);
        }
        if (((388 <= bookId) && (bookId <= 392)) || ((397 <= bookId) && (bookId <= 402)) || ((450 <= bookId) && (bookId <= 457))
                || ((403 <= bookId) && (bookId <= 420)) || ((421 <= bookId) && (bookId <= 432))) {
            binding.videoPlay.setVisibility(View.INVISIBLE);
            binding.videoImage.setVisibility(View.GONE);
        } else if (TextUtils.isEmpty(talkshowWord.videoUrl)) {
            binding.videoPlay.setVisibility(View.INVISIBLE);
            binding.videoImage.setVisibility(View.GONE);
        } else {
            binding.videoPlay.setVisibility(View.VISIBLE);
            binding.videoImage.setVisibility(View.VISIBLE);
        }
        binding.txtWord.setText(talkshowWord.word);
        binding.txtExplain.setText(talkshowWord.def);
        if (!TextUtils.isEmpty(talkshowWord.pron)) {
            if (talkshowWord.pron.startsWith("[")) {
                binding.txtPron.setText(String.format("%s", TextAttr.decode(talkshowWord.pron)));
            } else {
                binding.txtPron.setText(String.format("[%s]", TextAttr.decode(talkshowWord.pron)));
            }
        } else {
            binding.txtPron.setText("");
        }
        binding.txtPosHint.setText(String.format("%d / %d", position + 1, talkShowWords.size()));
        binding.llScore.setVisibility(View.INVISIBLE);
        binding.imgLowScore.setVisibility(View.INVISIBLE);
        binding.wordCorrect.setVisibility(View.INVISIBLE);
        isAutoPlay = WordConfigManager.Instance(context).loadBoolean("autoplay", false);
        if (isAutoPlay) {
//            binding.btnAuto.setImageResource(R.drawable.ic_auto_open);
            playAudio(talkshowWord.word, false, false, false);
        } else {
//            binding.btnAuto.setImageResource(R.drawable.ic_auto_close);
        }
    }

    private void deleteNetWord(String word) {
        presenter.deleteNetWord(word);
    }

    private void addNetwordWord(String wordTemp) {
        presenter.addNetWord(wordTemp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog!=null){
            dialog.dismiss();
        }

        stopTimer();
        if (player != null) {
            player.stop();
            player.release();
        }

        if (recordManager!=null){
            recordManager.stopRecord();
        }

        presenter.detachView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void stopPlayer() {
        stopAnimation();
        if (player.isPlaying()) {
            player.stop();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void showScore(Float scoreF, String sentence, List<SendEvaluateResponse.DataBean.WordsBean> dataBeans, String url) {
        dialog.dismiss();
        isEvaling = false;
        binding.llOwn.setVisibility(View.VISIBLE);
        sentenceUrl = url;
        Animation animation = new TranslateAnimation(0, 0, 300, 0);
        animation.setDuration(1000);
        if (scoreF < 50) {
            binding.imgLowScore.setVisibility(View.VISIBLE);
            binding.llScore.setVisibility(View.INVISIBLE);
            binding.imgLowScore.startAnimation(animation);
        } else {
            binding.txtScore.setText(Math.round(scoreF) + "");
            binding.imgLowScore.setVisibility(View.INVISIBLE);
            binding.llScore.setVisibility(View.VISIBLE);
            binding.txtScore.startAnimation(animation);
        }
        try {
            if (isSentence) {
                SpannableStringBuilder builder = SentenceSpanUtils.getSpanned(context, sentence,
                        dataBeans, talkshowWord.word);
                binding.txtSentence.setText(builder);
            } else if (scoreF < 50) {
                SpannableStringBuilder spanBuilder = new SpannableStringBuilder(sentence);
                ClickableSpan clickSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Log.e(TAG, "onStart setSpan onClick ");
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(getResources().getColor(R.color.red));
                    }
                };
                spanBuilder.setSpan(clickSpan, 0, sentence.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                binding.txtSentence.setText(spanBuilder);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        int flag = 0;
        StringBuilder fWord = new StringBuilder(16);
        if ((dataBeans != null) && (dataBeans.size() > 0)) {
            Log.e(TAG, "showScore dataBeans.size() " + dataBeans.size());
            try {
                for (SendEvaluateResponse.DataBean.WordsBean word : dataBeans) {
                    if ((word != null) && !TextUtils.isEmpty(word.getContent()) && !("-".equals(word.getContent())) && (word.getScore() < 2.5)) {
                        ++flag;
                        if (flag < 2) {
                            if ("Mr.".equals(word.getContent()) || "Mrs.".equals(word.getContent()) || "Ms.".equals(word.getContent())) {
                                fWord.append(word.getContent()).append(" ");
                            } else {
                                fWord.append(word.getContent().replaceAll("[?:!.,;\"]*([a-zA-Z]+)[?:!.,;\"]*", "$1")).append(" ");
                            }
                        }
                    }
                }
            } catch (Exception var) {
            }
        }
        Log.e(TAG, "showScore flag ----- " + flag);
        if (flag > 0) {
            binding.wordCorrect.setVisibility(View.VISIBLE);
            if (flag == 1) {
                binding.tvChoose.setText(fWord + "单词发音有误");
                ViewGroup.LayoutParams params = binding.wordCorrect.getLayoutParams();
                params.height = SentenceSpanUtils.dp2px(context, 42);
                if (fWord.length() < 4) {
                    params.width = SentenceSpanUtils.dp2px(context, 180);
                    binding.wordCorrect.setLayoutParams(params);
                } else if (fWord.length() < 8) {
                    params.width = SentenceSpanUtils.dp2px(context, 200);
                    binding.wordCorrect.setLayoutParams(params);
                } else if (fWord.length() < 12) {
                    params.width = SentenceSpanUtils.dp2px(context, 220);
                    binding.wordCorrect.setLayoutParams(params);
                } else {
                    params.width = SentenceSpanUtils.dp2px(context, 240);
                    binding.wordCorrect.setLayoutParams(params);
                }
            } else {
                binding.tvChoose.setText(fWord + "等单词发音有误");
                ViewGroup.LayoutParams params = binding.wordCorrect.getLayoutParams();
                params.height = SentenceSpanUtils.dp2px(context, 42);
                if (fWord.length() < 4) {
                    params.width = SentenceSpanUtils.dp2px(context, 190);
                    binding.wordCorrect.setLayoutParams(params);
                } else if (fWord.length() < 8) {
                    params.width = SentenceSpanUtils.dp2px(context, 210);
                    binding.wordCorrect.setLayoutParams(params);
                } else if (fWord.length() < 12) {
                    params.width = SentenceSpanUtils.dp2px(context, 230);
                    binding.wordCorrect.setLayoutParams(params);
                } else {
                    params.width = SentenceSpanUtils.dp2px(context, 250);
                    binding.wordCorrect.setLayoutParams(params);
                }
            }
            binding.btCommit.setOnClickListener(v -> {
                Log.e(TAG, "EvalCorrectDialog clicked.");
                fragment = new EvalCorrectDialog();
                fragment.setDataBean(dataBeans);
                fragment.setVoaText(talkshowWord);
                fragment.setSentence(isSentence);
                fragment.show(getFragmentManager(), "EvalCorrectDialog");
                // stop the play
                stopPlayer();
            });
        } else {
            // no data to process
        }
    }

    @Override
    public void showFail() {
        isEvaling = false;
        if (MediaUtils.isConnected(context)) {
            Toast.makeText(this, "评测失败，请稍后再试", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "评测失败，请确认开启了数据网络", Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
    }

    @Override
    public void showSentenceFail() {
        binding.txtSentence.setText("");
        binding.txtSentenceCh.setText("暂无例句");
    }

    @Override
    public void showSearchResult(WordEntity newWord) {
        if (newWord != null && !TextUtils.isEmpty(newWord.audio)) {
            Log.e(TAG, "showSearchResult newWord.audio: " + newWord.audio);
            talkshowWord.audio = newWord.audio;
            refreshUI();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int result = db.getTalkShowWordsDao().updateSingleWord(talkshowWord);
                    Log.e(TAG, "updateSingleWord result " + result);
                }
            }).start();
        } else {
            ToastUtil.showToast(context, "此单词暂无音频");
        }
    }

    @Override
    public void showSentence(TalkshowTexts text) {
        this.text = text;
    }

    @Override
    public void showText(String text) {
        ToastUtil.showToast(context, text);
    }

    private void stopAnimation() {
        closePlayAnim();
        closeRecordAnim();
        closeEvalAnim();
    }


    //右上角列表显示
    private void showMenu(){
        BubblePopupWindow popupWindow = new BubblePopupWindow(this);
        View menu = LayoutInflater.from(this).inflate(R.layout.layout_word_menu,null,false);
        LinearLayout shareLayout = menu.findViewById(R.id.share);
        shareLayout.setOnClickListener(v->{
            if (FastClickUtil.fastClick()){
                return;
            }

            popupWindow.dismiss();

            shareWord();
        });
        LinearLayout autoLayout = menu.findViewById(R.id.auto);
        ImageView autoImage = menu.findViewById(R.id.btn_auto);
        autoLayout.setOnClickListener(v->{
            if (FastClickUtil.fastClick()){
                return;
            }

            popupWindow.dismiss();

            boolean isAutoPlay = WordConfigManager.Instance(context).loadBoolean("autoplay");

            if (isAutoPlay) {
                ToastUtil.showToast(context, "关闭单词自动发音");
                autoImage.setImageResource(R.drawable.ic_auto_close);
                WordConfigManager.Instance(context).putBoolean("autoplay", false);
            } else {
                ToastUtil.showToast(context, "开启单词自动发音");
                autoImage.setImageResource(R.drawable.ic_audo_orange);
                WordConfigManager.Instance(context).putBoolean("autoplay", true);
            }
        });

        boolean isAuto = WordConfigManager.Instance(context).loadBoolean("autoplay");
        if (isAuto){
            autoImage.setImageResource(R.drawable.ic_audo_orange);
        }else {
            autoImage.setImageResource(R.drawable.ic_auto_close);
        }

        //判断功能是否存在
        if (AppData.APP_SHARE_HIDE>0){
            shareLayout.setVisibility(View.GONE);
        }else {
            shareLayout.setVisibility(View.VISIBLE);
        }

        popupWindow.setBubbleView(menu);
        popupWindow.show(binding.btnMore, Gravity.BOTTOM,1000,true);
    }

    //播放动画刷新
    private void startPlayAnim(){
        binding.imgOriginal.setImageResource(R.drawable.wordtest_speaker_anim);
        animation = (AnimationDrawable) binding.imgOriginal.getDrawable();

        if (animation!=null){
            animation.start();
        }
    }

    //关闭播放动画
    private void closePlayAnim(){
        if (animation!=null){
            animation.stop();
        }

        binding.imgOriginal.setImageResource(R.drawable.wordtest_speaker_2);
    }

    //录音动画刷新
    private void startRecordAnim(){
        binding.imgRecord.setImageResource(R.drawable.wordtest_record_anim);
        animation_record = (AnimationDrawable) binding.imgRecord.getDrawable();

        if (animation_record!=null){
            animation_record.start();
        }
    }

    //关闭录音动画
    private void closeRecordAnim(){
        if (animation_record!=null){
            animation_record.stop();
        }

        binding.imgRecord.setImageResource(R.drawable.record0);
    }

    //评测动画刷新
    private void startEvalAnim(){
        binding.imgOwn.setImageResource(R.drawable.wordtest_speaker_anim_own);
        animation_own = (AnimationDrawable) binding.imgOwn.getDrawable();

        if (animation_own!=null){
            animation_own.start();
        }
    }

    //关闭评测动画
    private void closeEvalAnim(){
        if (animation_own!=null){
            animation_own.stop();
        }

        binding.imgOwn.setImageResource(R.drawable.own_record3);
    }

    //获取音频播放时长
    private MediaPlayer timePlayer = null;
    private long getAudioPlayTime(){
        try {
            if (timePlayer!=null){
                timePlayer.reset();
                timePlayer.release();
                timePlayer = null;
            }

            String playPath = null;

            if (isSentence){
                if (TextUtils.isEmpty(talkshowWord.Sentence_audio)) {
                    playPath = null;
                }else {
                    if (MediaUtils.isConnected(getApplicationContext())){
                        playPath = talkshowWord.Sentence_audio;
                    }else {
                        playPath = null;
                    }
                }
            }else {
                if (TextUtils.isEmpty(talkshowWord.audio)){
                    playPath = null;
                }else {
                    if (MediaUtils.isConnected(getApplicationContext())){
                        playPath = talkshowWord.audio;
                    }else {
                        playPath = null;
                    }
                }
            }

            if (playPath==null){
                return 0;
            }else {
                timePlayer = new MediaPlayer();
                timePlayer.setDataSource(playPath);
                timePlayer.prepare();

                return timePlayer.getDuration();
            }
        }catch (Exception e){
            return 0;
        }
    }

    //分享功能（分享到小程序）
    private void shareWord(){
        if (!MediaUtils.isConnected(getApplicationContext())) {
            ToastUtil.showToast(this, "分享功能需要打开网络数据连接");
            return;
        }

        String imageUrl = null;
        if (!TextUtils.isEmpty(talkshowWord.pic_url)){
            imageUrl = imageHeaderUrl + talkshowWord.pic_url;
        }
        Share.prepareWord(this,talkshowWord.word,bookId,unit,talkshowWord.voa_id,imageUrl);
    }

}
