package com.iyuba.talkshow.newce.study.dubbingNew;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.module.toolbox.GsonUtils;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.local.PreferencesHelper;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaSoundNew;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.data.model.result.SendEvaluateResponse;
import com.iyuba.talkshow.databinding.ItemRecordBinding;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.CommonDataManager;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.JuniorDubbingHelpEntity;
import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.newdata.EvaluateBean;
import com.iyuba.talkshow.newview.EvalCorrectPage;
import com.iyuba.talkshow.ui.widget.DubbingProgressBar;
import com.iyuba.talkshow.util.DensityUtil;
import com.iyuba.talkshow.util.NumberUtil;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.talkshow.util.TimeUtil;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.talkshow.util.iseutil.ResultParse;
import com.iyuba.textpage.TextPage;
import com.iyuba.wordtest.utils.LibRxTimer;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class DubbingNewAdapter extends RecyclerView.Adapter<DubbingNewAdapter.RecordHolder> {

    private static final int POSITION = 65;
    private RecordingCallback mRecordingCallback;
    private PlayRecordCallback mPlayRecordCallback;
    private PlayVideoCallback mPlayVideoCallback;
    private ScoreCallback mScoreCallback;

    private List<VoaText> mList;
    private long mTimeStamp;
    private VoaText mOperateVoaText;
    public RecordHolder mOperateHolder;

    //    private int mActivitePosition;
    int fluent = 0;


    @Inject
    public DubbingNewPresenter mPresenter;

    Context mContext;

    @Inject
    public PreferencesHelper mHelper;

    private static final int FULL_PERCENT = 100;
    private EvalCorrectPage evalCorrectDialog;

    @Inject
    DataManager dataManager;

    //是否正在录音
    public boolean isRecording = false;
    //当前显示的位置
    private int curShowPosition = 0;

    @Inject
    public DubbingNewAdapter() {
        mList = new ArrayList<>();
    }

    @NotNull
    @Override
    public RecordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        ItemRecordBinding binder = ItemRecordBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RecordHolder(binder);
    }

    @Override
    public void onBindViewHolder(RecordHolder holder, int position) {
        holder.setItem(holder, mList.get(position), mList, position);
        holder.setClick(holder, position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setList(List<VoaText> mList) {
        this.mList.clear();
        this.mList.addAll(mList);
    }

    void setRecordingCallback(RecordingCallback mRecordingCallback) {
        this.mRecordingCallback = mRecordingCallback;
    }

    void setPlayRecordCallback(PlayRecordCallback mPlayRecordCallback) {
        this.mPlayRecordCallback = mPlayRecordCallback;
    }

    void setPlayVideoCallback(PlayVideoCallback mPlayVideoCallback) {
        this.mPlayVideoCallback = mPlayVideoCallback;
    }

    void setScoreCallback(ScoreCallback mScoreCallback) {
        this.mScoreCallback = mScoreCallback;
    }

    int getProgress(int total, int curPosition, float beginTime, float endTiming) {
        float curSec = TimeUtil.milliSecToSec(curPosition);
        return (int) (total * (curSec - beginTime) / (endTiming - beginTime));
    }

    //设置界面回调
    private DubbingNewFragment newFragment;

    public void setPageCallback(DubbingNewFragment fragment) {
        this.newFragment = fragment;
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NotNull Message msg) {
            int position = mPlayVideoCallback.getCurPosition();
            float recordEndTiming;
            float perfectTiming;
            int sidePos = mList.size() - 1;
            int firstProgress;
            if (curShowPosition == sidePos) {
                recordEndTiming = mList.get(curShowPosition).endTiming() + 0.1f;
                perfectTiming = mList.get(curShowPosition).endTiming();
            } else {
                recordEndTiming = mList.get(curShowPosition + 1).timing();
                perfectTiming = mList.get(curShowPosition).endTiming();
            }


            switch (msg.what) {
                //  读配音进度条的更改
                case 1:
                    if (TimeUtil.milliSecToSec(position) >= recordEndTiming) {
                        mPlayRecordCallback.stop();
//                        mOperateHolder.progressBar.setProgress(POSITION);
                        mOperateHolder.ivPlay.setVisibility(View.VISIBLE);
                        mOperateHolder.ivPause.setVisibility(View.INVISIBLE);
//                        mOperateHolder.progressBar.setSecondaryProgress(mOperateHolder.secondPosition);
                    } else {
                        handler.sendEmptyMessageDelayed(1, 25);
                        return;
                    }
                    break;
                // 录音计时
                case 2:
//                    firstProgress = getProgress(POSITION, position, mList.get(curShowPosition).timing(), mList.get(curShowPosition).endTiming());
//                    mOperateHolder.progress.setProgress(firstProgress);
                    firstProgress = getProgress(POSITION, position, mList.get(curShowPosition).timing(), mList.get(curShowPosition).endTiming());
                    mOperateHolder.progress.setProgress(firstProgress > POSITION ? POSITION : firstProgress);
                    if (firstProgress > POSITION) {
                        mOperateHolder.progress.setSecondaryProgress(firstProgress);
                    }


                    if (TimeUtil.milliSecToSec(position) >= recordEndTiming) {
                        endRecording();
//                        mOperateHolder.progress.setSecondaryProgress(getProgress(FULL_PERCENT, position, mList.get(curShowPosition).timing(), recordEndTiming));
//                        mOperateHolder.progress.setProgress(POSITION);
                    } else if (TimeUtil.milliSecToSec(position) >= perfectTiming) {
                        if (curShowPosition < sidePos) {
                            mOperateHolder.progress.setSecondaryProgress(getProgress(FULL_PERCENT, position, mList.get(curShowPosition).timing(), mList.get(curShowPosition + 1).timing()));
                        } else {
                            mOperateHolder.progress.setSecondaryProgress(getProgress(FULL_PERCENT, position, mList.get(curShowPosition).timing(), recordEndTiming));
                        }
                        mOperateHolder.progress.setProgress(POSITION);
                        handler.sendEmptyMessageDelayed(2, 25);
                    } else {
                        handler.sendEmptyMessageDelayed(2, 25);
                    }
                    break;
                case 3:
                    //视频播放处理
                    if (TimeUtil.milliSecToSec(position) >= recordEndTiming) {
                        mPlayVideoCallback.stop();
                        handler.removeMessages(3);
                    } else {
                        handler.sendEmptyMessageDelayed(3, 25);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    public void setTimeStamp(long timeStamp) {
        this.mTimeStamp = timeStamp;
    }

    class RecordHolder extends RecyclerView.ViewHolder implements TextPage.OnSelectListener {

        private float rate;

        int position1 = 0;
        int secondPosition = 100;
        VoaText mVoaText;
        AnimatorSet animatorSet = new AnimatorSet();
        private TextPage tvContentEn;
        private TextView tvContentCn;
        private TextView tvTime;
        private DubbingProgressBar progress;
        private ViewGroup recordLayout;
        private TextView voiceScore;
        private ImageView voiceLoading;
        private ImageView ivPlay;
        private ImageView ivPause;
        private TextView tVIndex;
        private ImageView ivRecord;
        private LinearLayout wordCorrect;
        private TextView tvChoose;
        private Button btCommit;

        //上边的布局
        private LinearLayout showLayout;
        //底部布局
        private LinearLayout bottomLayout;

        RecordHolder(ItemRecordBinding itemView) {
            super(itemView.getRoot());
            tvContentEn = itemView.tvContentEn;
            tvContentCn = itemView.tvContentCh;
            progress = itemView.progress;
            tvTime = itemView.tvTime;
            wordCorrect = itemView.wordCorrect;
            tvChoose = itemView.wordChoose;
            btCommit = itemView.wordCommit;
            recordLayout = itemView.recordLayout;
            voiceScore = itemView.voiceScore;
            voiceLoading = itemView.voiceLoading;
            ivPlay = itemView.ivPlay;
            ivPause = itemView.ivPause;
            tVIndex = itemView.tVIndex;
            ivRecord = itemView.ivRecord;

            showLayout = itemView.showLayout;
            bottomLayout = itemView.bottomLayout;
        }

        @SuppressLint("CheckResult")
        public void setItem(RecordHolder holder, VoaText voaText, List<VoaText> voaTextList, int pos) {
            tvContentEn.setOnSelectListener(this);
            this.mVoaText = voaText;
            position1 = voaText.paraId();

            //判断底部按钮是否打开
            if (curShowPosition == pos) {
                holder.bottomLayout.setVisibility(View.VISIBLE);
            } else {
                holder.bottomLayout.setVisibility(View.GONE);
            }

            if (pos < voaTextList.size() - 1) {
                float addTime = (voaTextList.get(pos + 1).timing() - voaText.endTiming());
                if (addTime < 1.0) {
                    Log.e("DubbingAdapter", "pos = " + pos + ", addTime = " + addTime);
                    addTime = 1;
                }
                progress.setAddingTime(MessageFormat.format(itemView.getResources().getString(R.string.record_time),
                        NumberUtil.keepOneDecimal(addTime)));
                rate = (voaText.endTiming() - voaText.timing()) * 100 / (voaTextList.get(pos + 1).timing() - voaText.timing());
            } else {
                float addTime = (voaText.endTiming() - voaText.timing()) / 4;
                if (addTime < 1.0) {
                    Log.e("DubbingAdapter", "pos = " + pos + ", addTime = " + addTime);
                    addTime = 1;
                }
                progress.setAddingTime(MessageFormat.format(itemView.getResources().getString(R.string.record_time),
                        NumberUtil.keepOneDecimal(addTime)));
                rate = 70;
            }
            progress.setPerfectTime(MessageFormat.format(itemView.getResources().getString(R.string.record_time), NumberUtil.keepOneDecimal(voaText.endTiming() - voaText.timing())));
            progress.setPosition(POSITION);
            setDefaultView(pos);
            tvTime.setText(MessageFormat.format(itemView.getResources().getString(R.string.record_time), NumberUtil.keepOneDecimal(voaText.endTiming() - voaText.timing())));
            // get record
            long tempId = Long.parseLong(mVoaText.getVoaId() + "" + mVoaText.paraId() + "" + mVoaText.idIndex());
            List<VoaSoundNew> voaRecord = mPresenter.getVoaSoundItemid(tempId);
            if ((voaRecord != null) && (voaRecord.size() > 0)) {
                VoaSoundNew voaSound = voaRecord.get(0);
                if ((voaSound != null) && (!TextUtils.isEmpty(voaSound.sound_url()))) {
                    String[] floats = voaSound.wordscore().split(",");
                    mVoaText.readScore = voaSound.totalscore();
                    mVoaText.readResult = ResultParse.getSenResultLocal(floats, mVoaText.sentence());
                    mVoaText.isRead = true;
                    mVoaText.evaluateBean = (new EvaluateBean(voaSound.sound_url()));
                    mVoaText.evaluateBean.setURL(voaSound.sound_url());
                    mVoaText.pathLocal = voaSound.filepath();
                    Log.e("DubbingAdapter", "onBindViewHolder mVoaText.pathLocal " + mVoaText.pathLocal);
//                    Log.e("DubbingAdapter", "onBindViewHolder detail.readResult " + mVoaText.readResult);
                    int flag = 0;
                    StringBuilder fWord = new StringBuilder(16);
                    if (!TextUtils.isEmpty(voaSound.words())) {
                        Log.e("DubbingAdapter", "onBindViewHolder voaSound.wordscore " + voaSound.wordscore());
                        try {
                            EvaluateBean evalBean = GsonUtils.toObject(voaSound.words(), EvaluateBean.class);
                            if (evalBean != null) {
                                mVoaText.evaluateBean = evalBean;
                                for (EvaluateBean.WordsBean word : evalBean.getWords()) {
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
                            }
                        } catch (Exception var) {
                        }
                    }
                    Log.e("DubbingAdapter", "onBindViewHolder flag ----- " + flag);
                    if (((position1 - 1) == pos) && (flag > 0)) {

                        //显示纠音
                        if (curShowPosition == pos){
                            holder.wordCorrect.setVisibility(View.VISIBLE);
                        }else {
                            holder.wordCorrect.setVisibility(View.GONE);
                        }

                        if (flag == 1) {
                            holder.tvChoose.setText(fWord + "单词发音有误");
                            ViewGroup.LayoutParams params = holder.wordCorrect.getLayoutParams();
                            params.height = DensityUtil.dp2px(mActivity, 42);
                            if (fWord.length() < 4) {
                                params.width = DensityUtil.dp2px(mActivity, 180);
                                holder.wordCorrect.setLayoutParams(params);
                            } else if (fWord.length() < 8) {
                                params.width = DensityUtil.dp2px(mActivity, 200);
                                holder.wordCorrect.setLayoutParams(params);
                            } else if (fWord.length() < 12) {
                                params.width = DensityUtil.dp2px(mActivity, 220);
                                holder.wordCorrect.setLayoutParams(params);
                            } else {
                                params.width = DensityUtil.dp2px(mActivity, 240);
                                holder.wordCorrect.setLayoutParams(params);
                            }
                        } else {
                            holder.tvChoose.setText(fWord + "等单词发音有误");
                            ViewGroup.LayoutParams params = holder.wordCorrect.getLayoutParams();
                            params.height = DensityUtil.dp2px(mActivity, 42);
                            if (fWord.length() < 4) {
                                params.width = DensityUtil.dp2px(mActivity, 190);
                                holder.wordCorrect.setLayoutParams(params);
                            } else if (fWord.length() < 8) {
                                params.width = DensityUtil.dp2px(mActivity, 210);
                                holder.wordCorrect.setLayoutParams(params);
                            } else if (fWord.length() < 12) {
                                params.width = DensityUtil.dp2px(mActivity, 230);
                                holder.wordCorrect.setLayoutParams(params);
                            } else {
                                params.width = DensityUtil.dp2px(mActivity, 250);
                                holder.wordCorrect.setLayoutParams(params);
                            }
                        }
                        holder.btCommit.setOnClickListener(v -> {
                            if (isRecording){
                                ToastUtil.showToast(mContext,"正在录音评测中～");
                                return;
                            }

                            //纠音弹窗
                            evalCorrectDialog = new EvalCorrectPage();
                            evalCorrectDialog.setUid(UserInfoManager.getInstance().getUserId());
                            evalCorrectDialog.setDataBean(mVoaText.evaluateBean);
                            evalCorrectDialog.setVoaText(mVoaText);
                            evalCorrectDialog.show(mActivity.getFragmentManager(), "EvalCorrectPage");
                            // stop the play
                            mPlayVideoCallback.stop();
                        });
                    } else {
                        // no data to process
                    }
                }
            }
            if (mVoaText.isRead) {
                tvContentEn.setText(mVoaText.readResult);
                voiceScore.setVisibility(View.VISIBLE);
                holder.mVoaText.setIscore(true);
                holder.mVoaText.setIsshowbq(true);
                setScoreColor(holder, mVoaText.readScore);
            } else {
                tvContentEn.setText(mVoaText.sentence());
                showScore();
            }
            tvContentEn.setLongClickable(false);
            showBiaoqian();
        }

        @SuppressLint("SetTextI18n")
        private void showScore() {
            if (mVoaText.isIscore()) {
                voiceScore.setVisibility(View.VISIBLE);
                voiceScore.setText(mVoaText.getScore() + "");
                if (mVoaText.getScore() >= 80) {
                    voiceScore.setBackgroundResource(R.drawable.blue_circle);
                } else if (mVoaText.getScore() >= 45 && mVoaText.getScore() < 80) {
                    voiceScore.setBackgroundResource(R.drawable.red_circle);
                } else {
                    voiceScore.setBackgroundResource(R.drawable.scroe_low);
                    voiceScore.setText("");
                }
            }
        }

        private void showBiaoqian() {
            if (mVoaText.isIsshowbq()) {
                ivPlay.setVisibility(View.VISIBLE);
                tVIndex.setBackgroundResource(R.drawable.index_green);
            }
        }

        @SuppressLint("SetTextI18n")
        private void setDefaultView(int curPosition) {
            ivPlay.setVisibility(View.INVISIBLE);
            ivPause.setVisibility(View.INVISIBLE);
            voiceScore.setVisibility(View.GONE);
            wordCorrect.setVisibility(View.GONE);
            tvContentEn.setText(mVoaText.sentence());
            tvContentCn.setText(mVoaText.sentenceCn());
            tVIndex.setText(mVoaText.paraId() + "/" + mList.size());
            if (checkExist(mVoaText.getVoaId(), mTimeStamp, mVoaText.paraId())) {
                tVIndex.setBackgroundResource(R.drawable.index_green);
                progress.setProgress(POSITION);
                progress.setSecondaryProgress(secondPosition);

                //优化进度条显示
//                ivPlay.setVisibility(View.VISIBLE);
//                ivPause.setVisibility(View.INVISIBLE);
                //评测数据
                String evalPath = StorageUtil.getAccRecordFile(mContext, mVoaText.getVoaId(), mTimeStamp, mVoaText.paraId()).getPath();
                //总时长
                long totalTime = getTotalTime(curPosition);
                long audioTime = getAudioPlayTime(evalPath);
                if (audioTime == 0) {
                    long itemId = Long.parseLong(mVoaText.getVoaId() + "" + mVoaText.paraId() + "" + mVoaText.idIndex());
                    JuniorDubbingHelpEntity entity = CommonDataManager.getDubbingHelpData(itemId, UserInfoManager.getInstance().getUserId());
                    if (entity != null) {
                        audioTime = entity.recordTime;
                    }
                }
                long progressTime = (long) ((mList.get(curPosition).endTiming() - mList.get(curPosition).timing()) * 1000L);
                //实际比率
                int audioRate = 0;
                if (audioTime >= totalTime) {
                    audioRate = 100;
                    progress.setProgress(POSITION);
                    progress.setSecondaryProgress(secondPosition);
                } else {
                    //计算显示的比率
                    if (audioTime > progressTime) {
                        audioRate = (int) (audioTime * 100 / totalTime);
                        progress.setProgress(POSITION);
                    } else {
                        audioRate = (int) ((audioTime * 1.0f / progressTime) * POSITION);
                        progress.setProgress(audioRate);
                    }
                    progress.setSecondaryProgress(audioRate);
                }
            } else {
                tVIndex.setBackgroundResource(R.drawable.index_gray);
                progress.setProgress(0);
                progress.setSecondaryProgress(0);
                ivPlay.setVisibility(View.INVISIBLE);
                ivPause.setVisibility(View.INVISIBLE);
            }
        }

        private boolean checkExist(int voaId, long timestamp, int paraId) {
            File file = StorageUtil.getAccRecordFile(tVIndex.getContext(), voaId, timestamp, paraId);
            return file.exists();
        }

        public void setClick(RecordHolder holder, int position) {
            holder.ivPlay.setOnClickListener(v -> onPlayClick(position));
            holder.ivPause.setOnClickListener(v -> onPauseClick());
            holder.ivRecord.setOnClickListener(v -> onRecordClick(position));


            holder.showLayout.setOnClickListener(v -> {
                if (curShowPosition == position){
                    return;
                }

                onLayoutClick(position);
            });
        }

        void onPlayClick(int position) {
            if (isRecording) {
                ToastUtil.showToast(mContext, "正在录音评测中～");
                return;
            }

            //先把上一个给关了
            if (mOperateHolder != null) {
                mOperateHolder.ivPause.setVisibility(View.INVISIBLE);
                mOperateHolder.ivPlay.setVisibility(View.VISIBLE);
            }

            mOperateHolder = this;
            if (ivPlay.getVisibility() == View.VISIBLE) {
                if (isRecording) {
                    mRecordingCallback.stop();
                }

                if (mOperateHolder != null) {
                    tVIndex.setBackgroundResource(R.drawable.index_green);
                    ivPause.setVisibility(View.INVISIBLE);
                    ivPlay.setVisibility(View.VISIBLE);
                }
                mOperateVoaText = mVoaText;
                ivPlay.setVisibility(View.INVISIBLE);
                ivPause.setVisibility(View.VISIBLE);
                playRecordTask(mOperateVoaText);
            }
        }

        public void playRecordTask(final VoaText voaText) {
//            if (isRecording) {
//                mRecordingCallback.stop();
//                mRecordingCallback.upload(voaText.paraId(), voaText.idIndex(), mList);
//                mRecordingCallback.convert(voaText.paraId(), mList);
//                isRecording = false;
//            }
            mPlayVideoCallback.stop();
            mPlayRecordCallback.start(voaText);
            handler.sendEmptyMessage(1);
        }


        void onPauseClick() {
            if (ivPause.getVisibility() == View.VISIBLE) {
                ivPause.setVisibility(View.INVISIBLE);
                ivPlay.setVisibility(View.VISIBLE);
                mPlayRecordCallback.stop();
                mPlayVideoCallback.stop();

//                if (isRecording) {
//                    isRecording = false;
//                    mRecordingCallback.stop();
//                    mRecordingCallback.upload(mVoaText.paraId(), mVoaText.idIndex(), mList);
//                    mRecordingCallback.convert(mVoaText.paraId(), mList);
//                }

            }
            handler.removeCallbacksAndMessages(null);
        }

        //录音按钮
        void onRecordClick(int position) {
            //先把权限给开启了
            if (!PermissionX.isGranted(mContext, Manifest.permission.RECORD_AUDIO)
                    || !PermissionX.isGranted(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                PermissionX.init((FragmentActivity) mContext).permissions(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .request(new RequestCallback() {
                            @Override
                            public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                                if (!allGranted) {
                                    Toast.makeText(mContext, "请授予必要的权限", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                return;
            }

            if (isRecording) {
                stopRecordTimer();
                endRecording();
                return;
            }

            //先把上个的播放停止了
            if (mOperateHolder != null) {
                mOperateHolder.ivPause.setVisibility(View.INVISIBLE);
                mOperateHolder.ivPlay.setVisibility(View.VISIBLE);
            }

            mOperateHolder = this;

            voiceScore.setText("");
            voiceScore.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.white));
            if (curShowPosition == position && isRecording) {
                mRecordingCallback.stop();
                mRecordingCallback.upload(mVoaText.paraId(), mVoaText.idIndex(), mList);
                mRecordingCallback.convert(mVoaText.paraId(), mList);
                tVIndex.setBackgroundResource(R.drawable.index_green);
                ivPlay.setVisibility(View.VISIBLE);
            } else {
                isRecording = true;
                wordCorrect.setVisibility(View.GONE);
                if (mOperateHolder != null) {
                    tVIndex.setBackgroundResource(R.drawable.index_green);
                    ivPlay.setVisibility(View.VISIBLE);
                    ivPause.setVisibility(View.INVISIBLE);
                }
                mOperateVoaText = mVoaText;
                //是否需要重新初始化
                File saveFile = StorageUtil.getAccRecordFile(
                        itemView.getContext(), mVoaText.getVoaId(), mTimeStamp, mVoaText.paraId());
                mOperateVoaText.pathLocal = saveFile.getAbsolutePath();
                Log.e("DubbingAdapter", "onRecordClick saveFile.getAbsolutePath() " + saveFile.getAbsolutePath());
                mRecordingCallback.init(saveFile.getAbsolutePath());
                mPlayRecordCallback.stop();
                mRecordingCallback.start(mVoaText);
                ivPlay.setVisibility(View.INVISIBLE);
                ivPause.setVisibility(View.INVISIBLE);
                setCallBack(this,position);

                progress.setProgress(0);
                progress.setSecondaryProgress(0);
//                handler.sendEmptyMessage(2);
                //开始定时器处理
                startRecordTimer(position);
            }
        }

        void onLayoutClick(int position) {
            if (isRecording) {
                ToastUtil.showToast(mContext,"正在录音评测中～");
                return;
            }

            //刷新选中位置
            curShowPosition = position;
            notifyDataSetChanged();

            handler.sendEmptyMessage(3);
            repeatPlayVoaText(mVoaText, true);
        }

        private void setCallBack(RecordHolder recordHolder,int position) {
            newFragment.setNewScoreCallback(new DubbingNewFragment.NewScoreCallback() {
                @Override
                public void onResult(int paraId, int score, SendEvaluateResponse.DataBean beans) {
                    if (paraId == recordHolder.position1) {
//                        if (null != recordHolder.animatorSet) {
//                            recordHolder.animatorSet.cancel();
//                        }

                        recordHolder.voiceLoading.setVisibility(View.INVISIBLE);
                        recordHolder.voiceScore.setVisibility(View.VISIBLE);

                        resetText(recordHolder);
                        mScoreCallback.onResult(position, score, fluent, beans.getURL());
                        setTextColorSpan(recordHolder, beans.getWords());
                        setScoreColor(recordHolder, score);

                        //刷新显示
                        notifyDataSetChanged();
                    }

                    //设置可以切换
                    isRecording = false;
                }

                @Override
                public void onError(int pos, String errorMessage) {
                    ToastUtil.show(mContext, errorMessage);
                    if (pos == recordHolder.position1) {
//                        if (null != recordHolder.animatorSet) {
//                            recordHolder.animatorSet.cancel();
//                        }

                        recordHolder.voiceLoading.setVisibility(View.INVISIBLE);
                        recordHolder.voiceScore.setVisibility(View.VISIBLE);

                        resetText(recordHolder);
                    }
                }

            });
        }

        private void setTextColorSpan(RecordHolder recordHolder, List<SendEvaluateResponse.DataBean.WordsBean> beans) {
            String string = tvContentEn.getText().toString();
            SpannableString spannableString = new SpannableString(string);
            int curentIndex = 0;
            for (int i = 0; i < beans.size(); i++) {
                if (beans.get(i).getScore() < 2.5) {
                    spannableString.setSpan(new ForegroundColorSpan(Color.RED), curentIndex, curentIndex + beans.get(i).getContent().length()
                            , Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                } else if (beans.get(i).getScore() > 3.75) {
                    spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.GREEN)), curentIndex, curentIndex + beans.get(i).getContent().length()
                            , Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                curentIndex += beans.get(i).getContent().length() + 1;
            }
            tvContentEn.setText(spannableString);
            recordHolder.mVoaText.setParseData(new SpannableStringBuilder(spannableString));
        }

        @SuppressLint("SetTextI18n")
        private void setScoreColor(RecordHolder holder, int score) {
            if (score >= 80) {
                voiceScore.setBackgroundResource(R.drawable.blue_circle);
                voiceScore.setText(score + "");
                holder.mVoaText.setScore(score);
            } else if (score >= 50 && score < 80) {
                voiceScore.setBackgroundResource(R.drawable.red_circle);
                voiceScore.setText(score + "");
                holder.mVoaText.setScore(score);
            } else {
                holder.mVoaText.setScore(score);
                voiceScore.setBackgroundResource(R.drawable.scroe_low);
                voiceScore.setText("");
            }
            if (holder.mVoaText.isIsshowbq()) {
                ivPlay.setVisibility(View.VISIBLE);
                tVIndex.setBackgroundResource(R.drawable.index_green);
            }
        }

        private void resetText(RecordHolder holder) {
            holder.mVoaText.setIsshowbq(true);
            holder.mVoaText.setIscore(true);
            voiceScore.setRotation(0);
            voiceScore.setAlpha(1);
            voiceScore.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSelect(String text) {
            if (text != null && text.length() != 0) {
                if (mActivity != null && mRecordingCallback != null) {
                    mRecordingCallback.lookWord(text);
                } else {
                    mPresenter.getNetworkInterpretation(text);
                }
            } else {
                ToastUtil.show(mContext, "请取英文单词");
            }
        }
    }

    public void repeatPlayVoaText(final VoaText voaText, boolean isStartPlay) {
        mOperateVoaText = voaText;

        if (isStartPlay) {
            mPlayVideoCallback.start(voaText);
        }
    }

    public VoaText getOperateVoaText() {
        return mOperateVoaText;
    }

    private void beginUITransform() {
        mOperateHolder.voiceScore.setVisibility(View.VISIBLE);
        mOperateHolder.voiceScore.setBackgroundResource(R.drawable.ic_wait_64px);


//        ValueAnimator rotateAnim = ObjectAnimator.ofFloat(mOperateHolder.voiceScore, "rotation", 0f, 45f);
//        ValueAnimator fadeAnim = ObjectAnimator.ofFloat(mOperateHolder.voiceScore, "alpha", 0f, 1f, 0f);
//        rotateAnim.setRepeatCount(3000);
//        fadeAnim.setRepeatCount(300);
//        mOperateHolder.animatorSet.playTogether(rotateAnim, fadeAnim);
//        mOperateHolder.animatorSet.setDuration(1050);
//        mOperateHolder.animatorSet.start();

        mOperateHolder.voiceScore.setVisibility(View.INVISIBLE);
        mOperateHolder.voiceLoading.setVisibility(View.VISIBLE);
        Glide3Util.loadGif(mContext, R.drawable.ic_loading, R.drawable.ic_loading, mOperateHolder.voiceLoading);
    }

    private void endRecording() {
        handler.removeCallbacksAndMessages(null);
        int temp = (int) (mOperateHolder.rate * 100 / mOperateHolder.progress.getSecondaryProgress());
        fluent = Math.min(temp, 100);
        beginUITransform();
        Log.e("DubbingAdapter", "endRecording : >>> cancel ");
        stopRecordTask(mOperateVoaText);
//        isRecording = false;
        mOperateHolder.ivPlay.setVisibility(View.VISIBLE);
        mOperateHolder.tVIndex.setBackgroundResource(R.drawable.index_green);
    }

    public void stopRecordTask(final VoaText voaText) {
        if (isRecording) {
            mRecordingCallback.stop();
            mRecordingCallback.upload(voaText.paraId(), voaText.idIndex(), mList);
            mRecordingCallback.convert(voaText.paraId(), mList);
        }
        mPlayVideoCallback.stop();
    }

    public void stopRecordPlayView() {
        if (mOperateHolder != null) {
            mOperateHolder.ivPlay.setVisibility(View.VISIBLE);
            mOperateHolder.ivPause.setVisibility(View.INVISIBLE);
        }
    }

    interface RecordingCallback {
        void init(String path);

        void start(VoaText voaText);

        boolean isRecording();

        void lookWord(String word);

        void setRecordingState(boolean state);

        void stop();

        // 現在不需要转化成mp3了
        void convert(int paraId, List<VoaText> list);

        void upload(int paraId, int idIndex, List<VoaText> list);
    }

    interface PlayRecordCallback {
        void start(VoaText voaText);

        void stop();

        int getLength();
    }

    interface PlayVideoCallback {
        void start(VoaText voaText);

        boolean isPlaying();

        int getCurPosition();

        void stop();

        int totalTimes();
    }

    interface ScoreCallback {
        void onResult(int pos, int score, int fluence, String url);
    }

    private Voa mVoa;

    public void SetVoa(Voa voa) {
        mVoa = voa;
    }

    Activity mActivity;

    public void SetActivity(Activity activ) {
        mActivity = activ;
    }

    /******************************新的逻辑操作*******************************/
    //获取音频文件的实际时间
    private long getAudioPlayTime(String audioUrl) {

        long playFileTime = 0;
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepare();

            playFileTime = mediaPlayer.getDuration();
        } catch (Exception e) {
            playFileTime = 0;
        }

        return playFileTime;
    }

    //获取显示的总时长
    private long getTotalTime(int curIndex) {
        long totalTime = 0;
        VoaText voaText = mList.get(curIndex);
        long progressTime = (long) ((voaText.endTiming() - voaText.timing()) * 1000L);

        if (curIndex < mList.size() - 1) {
            long addTime = (long) ((mList.get(curIndex + 1).timing() - voaText.endTiming()) * 1000L);
            totalTime = progressTime + addTime;
        } else {
            long addTime = (long) (((voaText.endTiming() - voaText.timing()) / 4) * 1000L);
            totalTime = progressTime + addTime;
        }
        return totalTime;
    }

    //获取显示的进度时长
    private long getProgressTime(int curIndex) {
        VoaText voaText = mList.get(curIndex);
        long progressTime = (long) ((voaText.endTiming() - voaText.timing()) * 1000L);
        return progressTime;
    }

    //录音计时器
    private static final String timer_record = "timer_record";

    //开启录音计时器
    private void startRecordTimer(int position) {
        long totalTime = getTotalTime(position);
        long progressTime = getProgressTime(position);

        stopRecordTimer();
        LibRxTimer.getInstance().multiTimerInMain(timer_record, 0, 200L, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                //计算当前时间进度并处理
                long curTime = number * 200L;

                if (curTime >= totalTime) {
                    mOperateHolder.progress.setProgress(POSITION);
                    mOperateHolder.progress.setSecondaryProgress(FULL_PERCENT);

                    stopRecordTimer();
                    endRecording();
                } else {
                    if (curTime > progressTime) {
                        mOperateHolder.progress.setProgress(POSITION);
                        int rate = (int) (curTime * 100.0f / totalTime);
                        mOperateHolder.progress.setSecondaryProgress(rate);
                    } else {
                        int rate = (int) ((curTime * 1.0f / progressTime) * POSITION);
                        mOperateHolder.progress.setProgress(rate);
                        mOperateHolder.progress.setSecondaryProgress(rate);
                    }
                }
            }
        });
    }

    //关闭录音计时器
    private void stopRecordTimer() {
        LibRxTimer.getInstance().cancelTimer(timer_record);
    }

    //停止录音和音频播放
    public void stopRecordAndPlay() {
        if (isRecording) {
            isRecording = false;
            mRecordingCallback.stop();
            stopRecordTimer();
        }

        if (mOperateHolder != null) {
            mOperateHolder.ivPause.setVisibility(View.INVISIBLE);
            mOperateHolder.ivPlay.setVisibility(View.VISIBLE);
        }
    }
}

