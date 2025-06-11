package com.iyuba.talkshow.newce.study.eval;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.module.toolbox.GsonUtils;
import com.iyuba.play.ExtendedPlayer;
import com.iyuba.play.IJKPlayer;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaSoundNew;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.databinding.FragmentEvalItemNewBinding;
import com.iyuba.talkshow.event.EvalEvent;
import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.newce.study.StudyActivity;
import com.iyuba.talkshow.newdata.AudioSendApi;
import com.iyuba.talkshow.newdata.Config;
import com.iyuba.talkshow.newdata.EvaSendBean;
import com.iyuba.talkshow.newdata.EvaluateBean;
import com.iyuba.talkshow.newdata.EvaluateRequset;
import com.iyuba.talkshow.newdata.MediaRecordHelper;
import com.iyuba.talkshow.newdata.MyIjkPlayer;
import com.iyuba.talkshow.newdata.NetWorkState;
import com.iyuba.talkshow.newdata.RetrofitUtils;
import com.iyuba.talkshow.newdata.SPconfig;
import com.iyuba.talkshow.newdata.ShareUtils;
import com.iyuba.talkshow.newview.CustomDialog;
import com.iyuba.talkshow.newview.EvalCorrectPage;
import com.iyuba.talkshow.newview.RoundProgressBar;
import com.iyuba.talkshow.newview.WaittingDialog;
import com.iyuba.talkshow.ui.vip.buyvip.NewVipCenterActivity;
import com.iyuba.talkshow.util.DensityUtil;
import com.iyuba.talkshow.util.DialogUtil;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.talkshow.util.TextAttr;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.talkshow.util.iseutil.ResultParse;
import com.iyuba.wordtest.utils.PermissionDialogUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by carl shen on 2020/7/31
 * New Primary English, new study experience.
 */
public class EvalAdapter extends RecyclerView.Adapter<EvalAdapter.MyViewHolder> {
    private static final int handler_auto_stop = 3;
    private static final int handler_follow_player = 4;
    private static final int handler_player = 0;
    private static final int handler_send = 5;
    private static final int handler_sound_db = 1;
    private static final int handler_stop_evaluate = 2;
    private int clickPosition = 0;
    private MyViewHolder clickViewHolder;
    private VoaText clickVoaDetail;
    private VoaSoundNew clickVoaSound;
    private final ExtendedPlayer followPlayer;
    private boolean isEvaluating = false;
    private boolean isRecording = false;
    private boolean isSending = false;
    private Voa mVoa;
    private final List<VoaText> list = new ArrayList<>();
    private Context mContext;
    private final MediaRecordHelper mediaRecordHelper;
    private MixSound mixSound;
    private IJKPlayer player;
    private final MyIjkPlayer textPlayer;
    private CustomDialog waitingDialog;
    private EvalCorrectPage fragment;
    @Inject
    public EvalPresenter mPresenter;
    private final long mTimeStamp = System.currentTimeMillis();
    private Handler mSubHandler;
    private Handler mMainHandler = null;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case handler_player:
                    RoundProgressBar bar = clickViewHolder.senPlay;
                    bar.setBackgroundResource(R.mipmap.sen_stop_new);
                    int totalTime;
                    if (0 != list.get(clickPosition).endTiming()) {
                        totalTime = (int) (list.get(clickPosition).endTiming() * 1000.0D);
                    } else {
                        if (clickPosition == list.size() - 1) {
                            totalTime = player.getDuration();
                        } else {
                            totalTime = (int) ((list.get(clickPosition + 1)).timing() * 1000.0D);
                        }
                    }

                    int starTime = (int) (clickVoaDetail.timing() * 1000.0D);
                    int currTime = player.getCurrentPosition();
                    bar.setMax(totalTime - starTime);
                    Log.e(EvalFragment.TAG, "播放时间 getCurrentPosition " + player.getCurrentPosition());
                    Log.e(EvalFragment.TAG, "播放时间" + currTime + "==" + starTime);
                    bar.setProgress((currTime - starTime) > 0 ? (currTime - starTime) : 0);
                    if (player.getCurrentPosition() < totalTime) {
                        handler.sendEmptyMessageDelayed(0, 300L);
                    } else {
                        handler.removeMessages(0);
                        player.pause();
                        bar.setBackgroundResource(R.mipmap.sen_play_new);
                        bar.setProgress(0);
                    }
                    if (player.isCompleted()) {
                        handler.removeMessages(0);
                        bar.setBackgroundResource(R.mipmap.sen_play_new);
                        bar.setProgress(0);
                    }
                    break;
                case handler_sound_db:
                    try {
                        clickViewHolder.senIRead.setBackgroundResource(R.drawable.sen_i_read_new2);
                        clickViewHolder.senIRead.setCricleProgressColor(0xff87c973);
                        clickViewHolder.senIRead.setMax(100);
                        clickViewHolder.senIRead.setProgress(mediaRecordHelper.getDB());
//                        Drawable drawable = clickViewHolder.senIRead.getDrawable();
//                        //因为level为0-10000，因此需要计算
//                        int temp = mediaRecordHelper.getDB()*10000/100;
//                        if (temp>0){
//                            temp+=100;
//                        }
//                        if (temp>10000){
//                            temp = 10000;
//                        }
//                        drawable.setLevel(temp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessageDelayed(1, 100L);
                    break;
                case handler_stop_evaluate:
                    handler.removeMessages(1);
                    clickViewHolder.senIRead.setBackgroundResource(R.drawable.sen_i_read_new2);
                    clickViewHolder.senIRead.setProgress(0);
//                    clickViewHolder.senIRead.setImageResource(R.drawable.layer_record);
                    break;
                case handler_auto_stop:
                    stopPC();
                    break;
                case handler_follow_player:
                    RoundProgressBar bar1 = clickViewHolder.senReadPlay;
                    bar1.setBackgroundResource(R.mipmap.sen_stop_new);
                    bar1.setMax(followPlayer.getDuration());
                    bar1.setProgress(followPlayer.getCurrentPosition());
                    if (followPlayer.getCurrentPosition() < followPlayer.getDuration()) {
                        handler.sendEmptyMessageDelayed(4, 300L);
                    } else {
                        handler.removeMessages(4);
                        try {
                            followPlayer.pause();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        bar1.setBackgroundResource(R.mipmap.play_ok_new);
                        bar1.setProgress(0);
                    }
                    break;
                case handler_send:
                    String addscore = String.valueOf(msg.arg1);
                    if (ConfigData.openShare) {
                        clickViewHolder.senReadShare.setVisibility(View.VISIBLE);
                    } else {
                        clickViewHolder.senReadShare.setVisibility(View.GONE);
                    }
                    clickViewHolder.senReadSend.setVisibility(View.GONE);
                    if (addscore.equals("5")) {
                        String mg = "语音成功发送至排行榜，恭喜您获得了" + addscore + "分";
                        ToastUtil.showToast(mContext, mg);
                    } else {
                        String mg = "语音成功发送至排行榜";
                        ToastUtil.showToast(mContext, mg);
                    }
                    break;
                case 13:
                    isRecording = false;
                    ToastUtil.showToast(mContext, "录音失败，请稍后再试");
                    //设置回调
                    if (onEvalListener!=null){
                        onEvalListener.onEvalRecord(false);
                    }
                    break;
                case 14:
                    clickViewHolder.ivAnim.setVisibility(View.GONE);
                    clickViewHolder.senReadResult.setVisibility(View.VISIBLE);
                    isEvaluating = false;
                    //设置回调
                    if (onEvalListener!=null){
                        onEvalListener.onEvalRecord(false);
                    }
                    ToastUtil.showToast(mContext, "评测失败");
                    break;
                case 15:
                    clickViewHolder.ivAnim.setVisibility(View.GONE);
                    clickViewHolder.senReadResult.setVisibility(View.VISIBLE);
                    //设置回调
                    if (onEvalListener!=null){
                        onEvalListener.onEvalRecord(false);
                    }

                    String result = (String) msg.obj;
                    Log.e("评测返回", result);
                    isEvaluating = false;
                    clickVoaDetail.evaluateBean = GsonUtils.toObject(result, EvaluateBean.class);
                    double totalScore = Double.parseDouble(clickVoaDetail.evaluateBean.getTotal_score());
                    clickVoaDetail.isRead = true;
                    clickVoaDetail.readResult = ResultParse.getSenResultEvaluate(clickVoaDetail.evaluateBean.getWords(), clickVoaDetail.sentence());
                    int score = (int) (totalScore * 20.0D);
                    clickVoaDetail.readScore = score;
                    ToastUtil.showToast(mContext, "评测成功");
                    notifyDataSetChanged();
                    StringBuilder wordScore = new StringBuilder(64);
                    for (int i = 0; i < clickVoaDetail.evaluateBean.getWords().size(); i++) {
                        EvaluateBean.WordsBean word = clickVoaDetail.evaluateBean.getWords().get(i);
                        wordScore.append(word.getScore());
                        if (i != (clickVoaDetail.evaluateBean.getWords().size() - 1)) {
                            wordScore.append(",");
                        }
                    }
                    mixSound.reStart();
                    // need instert to db
                    Log.e(EvalFragment.TAG, "Record Save add wordScore " + wordScore);
//                    Log.e(EvalFragment.TAG, "Record Save add score " + score);
//                    Log.e(EvalFragment.TAG, "Record clickVoaDetail.evaluateBean.getURL() " + clickVoaDetail.evaluateBean.getURL());
                    VoaSoundNew voaSound = VoaSoundNew.builder()
                            .setItemid(Long.parseLong(clickVoaDetail.getVoaId() + "" + clickVoaDetail.paraId()+""+clickVoaDetail.idIndex()))
                            .setUid(UserInfoManager.getInstance().getUserId())
                            .setVoa_id(clickVoaDetail.getVoaId())
                            .setTotalscore(score)
                            .setWordscore(wordScore.toString())
                            .setFilepath(getMP4FileName())
                            .setTime("" + mTimeStamp)
                            .setSound_url(clickVoaDetail.evaluateBean.getURL())
                            .setWords(result)
                            .setRvc("")
                            .build();
                    mPresenter.saveVoaSound(voaSound);
                    EventBus.getDefault().post(new EvalEvent());
                    break;
            }
        }
    };

    private String getMP4FileName() {
        VoaText mVoaText = list.get(clickPosition);
        if (mVoaText != null) {
            File saveFile = StorageUtil.getAccRecordFile(mActivity, mVoaText.getVoaId(), mTimeStamp, mVoaText.paraId());
            Log.e(EvalFragment.TAG, "getMP4FileName saveFile.getAbsolutePath() " + saveFile.getAbsolutePath());
            return saveFile.getAbsolutePath();
        }
        boolean isAmerican = SPconfig.Instance().loadBoolean(Config.ISAMEICAN, true);
        if (isAmerican)
            return Constant.getsimRecordAddr() + "/" + clickVoaDetail.getVoaId() + clickVoaDetail.paraId() + ".mp3";
        else
            return Constant.getsimRecordAddr() + "/" + (clickVoaDetail.getVoaId() * 10) + clickVoaDetail.paraId() + ".mp3";
    }

    @Inject
    public EvalAdapter() {
        mediaRecordHelper = new MediaRecordHelper();
        textPlayer = MyIjkPlayer.getInstance();
        followPlayer = new ExtendedPlayer(TalkShowApplication.getInstance());
        followPlayer.setOnPreparedListener(mp -> handler.sendEmptyMessage(4));

        followPlayer.setOnCompletionListener(mp -> {
            handler.removeMessages(4);
            clickViewHolder.senReadPlay.setBackgroundResource(R.mipmap.play_ok_new);
            clickViewHolder.senReadPlay.setProgress(0);
        });
    }

    public void SetVoaList(List<VoaText> voalist) {
        if (voalist != null) {
            list.clear();
            list.addAll(voalist);
        }
    }
    public void SetVoa(Voa voa) {
         mVoa = voa;
    }
    Activity mActivity;
    public void SetActivity(Activity activ) {
        mActivity = activ;
    }
    public void SetSubHandler(Handler times) {
        mSubHandler = times;
    }

    public void SetMainHandler(Handler mainhandler) {
        mMainHandler = mainhandler;
    }

    private boolean canEvaluate() {
        if (!NetWorkState.isConnectingToInternet()) {
            ToastUtil.showToast(this.mContext, "网络未连接！");
            return false;
        }

        if (!UserInfoManager.getInstance().isLogin()){
            NewLoginUtil.startToLogin(mContext);
            return false;
        }

        List<VoaSoundNew> voaSounds = mPresenter.getVoaSoundVoaId(mVoa.voaId());
        boolean isVip = UserInfoManager.getInstance().isVip();
        if ((!clickVoaDetail.isRead) && (voaSounds.size() >= 3) && (!isVip)) {
            /*final MaterialDialog materialDialog = new MaterialDialog(mContext);
            materialDialog.setTitle("提醒");
            materialDialog.setMessage("本篇你已评测3句！成为VIP后可评测更多");
            materialDialog.setNegativeButton("确定", view -> {
                materialDialog.dismiss();
            });
            materialDialog.setPositiveButton("立即开通", view -> {
                materialDialog.dismiss();
                mContext.startActivity(new Intent(mContext, NewVipCenterActivity.class));
            });
            materialDialog.show();*/
            DialogUtil.showVipDialog(mContext,"非VIP会员每篇课程免费评测3句，VIP会员无限制评测，是否开通会员使用?",NewVipCenterActivity.BENYINGYONG);
            return false;
        }
        return true;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    private boolean isLogin() {
        int userId = UserInfoManager.getInstance().getUserId();
        return userId != 0;
    }

    private void send() {
        if (!isLogin()) {
            ToastUtil.showToast(this.mContext, "请登录后再执行此操作");
            return;
        }

        if (isSending) {
            ToastUtil.showToast(this.mContext, "评测发送中，请不要重复提交");
            return;
        }

        waitingDialog.show();
        boolean isAmerican = SPconfig.Instance().loadBoolean(Config.ISAMEICAN, true);

        String currVoaId;
        if (isAmerican) {
            currVoaId = String.valueOf(clickVoaDetail.getVoaId());
        } else {
            currVoaId = String.valueOf(clickVoaDetail.getVoaId() * 10);
        }

        String actionUrl = "http://voa." + Constant.Web.WEB_SUFFIX + "voa/UnicomApi?"
                + "platform=android&format=json&protocol=60003"
                + "&topic=" + Constant.EVAL_TYPE
                + "&topicid=" + currVoaId
                + "&paraid=" + clickVoaDetail.paraId()
                + "&userid=" + UserInfoManager.getInstance().getUserId()
                + "&username=" + TextAttr.encode(UserInfoManager.getInstance().getUserName())
                + "&voaid=" + currVoaId + "&idIndex=" + clickVoaDetail.idIndex()
                + "&score=" + clickVoaDetail.readScore + "&shuoshuotype=2"
                + "&content=" + clickVoaDetail.evaluateBean.getURL();

        Log.e(EvalFragment.TAG, "actionUrl = " + actionUrl);
        isSending = true;
        AudioSendApi audioSendApi = RetrofitUtils.getInstance().getApiService(AudioSendApi.BASEURL, AudioSendApi.class);
        audioSendApi.evalSendApi("UnicomApi",
                Constant.EVAL_TYPE, currVoaId, clickVoaDetail.paraId(), clickVoaDetail.idIndex(),
                AudioSendApi.platform, AudioSendApi.format, AudioSendApi.protocol,
                UserInfoManager.getInstance().getUserId() + "", TextAttr.encode(UserInfoManager.getInstance().getUserName()), currVoaId,
                clickVoaDetail.readScore + "", "2", clickVoaDetail.evaluateBean.getURL()).enqueue(new retrofit2.Callback<EvaSendBean>() {
            @Override
            public void onResponse(retrofit2.Call<EvaSendBean> call, retrofit2.Response<EvaSendBean> response) {
                waitingDialog.dismiss();
                isSending = false;
                if ((response != null) && response.isSuccessful()) {
                    EvaSendBean evaSendBean = response.body();
                    Log.e(EvalFragment.TAG, "evaSendBean = " + evaSendBean.toString());
                    clickVoaDetail.shuoshuoId = evaSendBean.getShuoshuoId();
                    //保存shoushouid数据，辨别是否显示分享
                    tempIdMap.put(String.valueOf(clickPosition), String.valueOf(clickVoaDetail.shuoshuoId));
                    if ("501".equals(evaSendBean.getResultCode())) {
                        Message msg = handler.obtainMessage();
                        msg.what = handler_send;
                        msg.arg1 = evaSendBean.getAddScore();
                        handler.sendMessage(msg);
                        VoaSoundNew voaSound = VoaSoundNew.builder()
                                .setItemid(clickVoaSound.itemid())
                                .setUid(clickVoaSound.uid())
                                .setVoa_id(clickVoaSound.voa_id())
                                .setTotalscore(clickVoaSound.totalscore())
                                .setWordscore(clickVoaSound.wordscore())
                                .setFilepath(clickVoaSound.filepath())
                                .setTime(clickVoaSound.time())
                                .setSound_url(clickVoaSound.sound_url())
                                .setWords(clickVoaSound.words())
                                .setRvc("" + evaSendBean.getShuoshuoId())
                                .build();
                        mPresenter.saveVoaSound(voaSound);
                    }
                } else {
                    ToastUtil.showToast(mContext, "发布失败");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<EvaSendBean> call, Throwable t) {
                waitingDialog.dismiss();
                isSending = false;
                ToastUtil.showToast(mContext, "发布失败");
            }
        });
    }

    private void setReadScoreViewContent(int score, TextView textView) {
        if (score < 50) {
            textView.setText("");
            textView.setBackgroundResource(R.mipmap.sen_score_lower60_new);
        } else if (score > 80) {
            textView.setText(score + "");
            textView.setBackgroundResource(R.drawable.sen_score_higher_80);
        } else {
            textView.setText(score + "");
            textView.setBackgroundResource(R.drawable.sen_score_60_80);
        }
    }

    private void setRequest() {
        //这里加入加载框
        clickViewHolder.ivAnim.setVisibility(View.VISIBLE);
//        Glide.with(mContext).load(R.drawable.ic_loading).asGif().into(clickViewHolder.ivAnim);
        Glide3Util.loadGif(mContext,R.drawable.ic_loading,0,clickViewHolder.ivAnim);
        clickViewHolder.senReadResult.setVisibility(View.GONE);

        if (isEvaluating) {
            ToastUtil.showToast(this.mContext, "正在评测中，请不要重复提交");
            return;
        }
        boolean isAmerican = SPconfig.Instance().loadBoolean(Config.ISAMEICAN, true);

        String currVoaId;
        if (isAmerican) {
            currVoaId = String.valueOf(clickVoaDetail.getVoaId());
        } else {
            currVoaId = String.valueOf(clickVoaDetail.getVoaId() * 10);
        }
        Map<String, String> textParams = new HashMap<String, String>();

        int uid = UserInfoManager.getInstance().getUserId();
        File file = new File(getMP4FileName());
        textParams.put("type", Constant.EVAL_TYPE);
        textParams.put("userId", uid + "");
        textParams.put("newsId", currVoaId);
        textParams.put("platform", AudioSendApi.platform);
        textParams.put("protocol", AudioSendApi.protocol);
        textParams.put("paraId", clickVoaDetail.paraId() + "");
        textParams.put("IdIndex", clickVoaDetail.idIndex() + "");

        String urlSentence = TextAttr.encode(clickVoaDetail.sentence());
        urlSentence = urlSentence.replaceAll("\\+", "%20");
        textParams.put("sentence", urlSentence);
        if (file != null && file.exists()) {
            try {
                isEvaluating = true;
                if (Constant.EvaluateCorrect) {
                    textParams.put("flg", "0");
                    textParams.put("wordId", "0");
                    textParams.put("appId", App.APP_ID + "");
                    EvaluateRequset.getInstance().post(Constant.Web.EVALUATE_URL_CORRECT, textParams, getMP4FileName(), handler);
                } else {
                    EvaluateRequset.getInstance().post(Constant.Web.EVALUATE_URL_NEW, textParams, getMP4FileName(), handler);
                }
            } catch (Exception e) {
                isEvaluating = false;
                e.printStackTrace();
                handler.sendEmptyMessage(14);
            }
        } else {
            handler.sendEmptyMessage(14);
        }
    }

    private void startPC() {
        isRecording = true;
        //设置回调
        if (onEvalListener!=null){
            onEvalListener.onEvalRecord(true);
        }

        int time;

        if (0 != (list.get(clickPosition)).endTiming()) {
            time = (int) ((list.get(clickPosition)).endTiming() * 1000.0D);
        } else {
            if (clickPosition == list.size() - 1) {
                time = (int) textPlayer.getDuration();
            } else {
                time = (int) ((list.get(this.clickPosition + 1)).timing() * 1000.0D);
            }
        }

        int totalTime = time - (int) (this.clickVoaDetail.timing() * 1000.0D);
        makeRootDirectory(Constant.getsimRecordAddr());
        mediaRecordHelper.setFilePath(getMP4FileName());
        mediaRecordHelper.recorder_Media();
        handler.sendEmptyMessage(1);

        if (totalTime < 2000) {
            handler.sendEmptyMessageDelayed(3, (long) (totalTime * 2.5D));
        } else {
            handler.sendEmptyMessageDelayed(3, (long) (totalTime * 1.5D));
        }
    }

    private void stopPC() {
        if (isRecording) {
            isRecording = false;
            mediaRecordHelper.stop_record();
            handler.sendEmptyMessage(2);
            setRequest();
        }
    }

    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int paramInt) {
        if (mContext == null) {
            mContext = parent.getContext();
            waitingDialog = WaittingDialog.showDialog(mContext);
        }

        FragmentEvalItemNewBinding binder = FragmentEvalItemNewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binder);
//        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_eval_fragment, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        VoaText detail = list.get(position);

        if (getItemCount() < 10) {
            viewHolder.textIndex.setText(detail.paraId() + " / " + getItemCount());
        } else {
            viewHolder.textIndex.setText(detail.paraId() + "/" + getItemCount());
        }
        viewHolder.senEn.setText(detail.sentence());
        if (TextUtils.isEmpty(detail.sentenceCn())) {
            viewHolder.senZh.setVisibility(View.GONE);
        } else {
            viewHolder.senZh.setVisibility(View.VISIBLE);
            viewHolder.senZh.setText(detail.sentenceCn());
        }

        viewHolder.bottomView.setVisibility(View.GONE);
        viewHolder.sepLine.setVisibility(View.GONE);
        viewHolder.wordCorrect.setVisibility(View.GONE);
        viewHolder.senIRead.setBackgroundResource(R.drawable.sen_i_read_new2);
        viewHolder.senIRead.setProgress(0);
//        viewHolder.senIRead.setImageResource(R.drawable.layer_record);
        if (clickPosition == position) {
            clickViewHolder = viewHolder;
            clickVoaDetail = detail;
            clickViewHolder.bottomView.setVisibility(View.VISIBLE);
            clickViewHolder.sepLine.setVisibility(View.VISIBLE);
        }
        // get record
        List<VoaSoundNew> voaRecord = mPresenter.getVoaSoundItemid(Long.parseLong(detail.getVoaId() + "" + detail.paraId()+""+detail.idIndex()));
        if ((voaRecord != null) && (voaRecord.size()>0)) {
//            Log.e(EvalFragment.TAG, "onBindViewHolder voaSound.size() " + voaRecord.size());
            VoaSoundNew voaSound = voaRecord.get(0);
//            Log.e(EvalFragment.TAG, "onBindViewHolder voaSound.filepath() " + voaSound.filepath());
            if ((voaSound != null) && (!TextUtils.isEmpty(voaSound.sound_url()))) {
                String[] floats = voaSound.wordscore().split(",");
                detail.readScore = voaSound.totalscore();
                detail.readResult = ResultParse.getSenResultLocal(floats, detail.sentence());
                detail.isRead = true;
                detail.evaluateBean = (new EvaluateBean(voaSound.sound_url()));
                detail.evaluateBean.setURL(voaSound.sound_url());
                detail.pathLocal = voaSound.filepath();
                Log.e(EvalFragment.TAG, "onBindViewHolder detail.readResult " + detail.readResult);
                clickVoaSound = voaSound;
                if (clickPosition == position) {
                    Log.e(EvalFragment.TAG, "onBindViewHolder VoaSound.rvc " + clickVoaSound.rvc());
                    int flag = 0;
                    StringBuilder fWord = new StringBuilder(16);
                    if (!TextUtils.isEmpty(voaSound.words())) {
                        Log.e(EvalFragment.TAG, "onBindViewHolder VoaSound.wordscore " + voaSound.wordscore());
                        try {
                            EvaluateBean evalBean = GsonUtils.toObject(voaSound.words(), EvaluateBean.class);
                            if (evalBean != null) {
                                detail.evaluateBean = evalBean;
                                for (EvaluateBean.WordsBean word: evalBean.getWords()) {
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
                        } catch (Exception var) { }
                    }
                    Log.e(EvalFragment.TAG, "onBindViewHolder flag ----- " + flag);
                    if (flag > 0) {
                        clickViewHolder.wordCorrect.setVisibility(View.VISIBLE);
                        if (flag == 1) {
                            clickViewHolder.tvChoose.setText(fWord + "单词发音有误");
                            ViewGroup.LayoutParams params = clickViewHolder.wordCorrect.getLayoutParams();
                            params.height = DensityUtil.dp2px(mActivity, 42);
                            if (fWord.length() < 4) {
                                params.width = DensityUtil.dp2px(mActivity, 180);
                                clickViewHolder.wordCorrect.setLayoutParams(params);
                            } else if (fWord.length() < 8) {
                                params.width = DensityUtil.dp2px(mActivity, 200);
                                clickViewHolder.wordCorrect.setLayoutParams(params);
                            } else if (fWord.length() < 12) {
                                params.width = DensityUtil.dp2px(mActivity, 220);
                                clickViewHolder.wordCorrect.setLayoutParams(params);
                            } else {
                                params.width = DensityUtil.dp2px(mActivity, 240);
                                clickViewHolder.wordCorrect.setLayoutParams(params);
                            }
                        } else {
                            clickViewHolder.tvChoose.setText(fWord + "等单词发音有误");
                            ViewGroup.LayoutParams params = clickViewHolder.wordCorrect.getLayoutParams();
                            params.height = DensityUtil.dp2px(mActivity, 42);
                            if (fWord.length() < 4) {
                                params.width = DensityUtil.dp2px(mActivity, 190);
                                clickViewHolder.wordCorrect.setLayoutParams(params);
                            } else if (fWord.length() < 8) {
                                params.width = DensityUtil.dp2px(mActivity, 210);
                                clickViewHolder.wordCorrect.setLayoutParams(params);
                            } else if (fWord.length() < 12) {
                                params.width = DensityUtil.dp2px(mActivity, 230);
                                clickViewHolder.wordCorrect.setLayoutParams(params);
                            } else {
                                params.width = DensityUtil.dp2px(mActivity, 250);
                                clickViewHolder.wordCorrect.setLayoutParams(params);
                            }
                        }
                        clickViewHolder.btCommit.setOnClickListener(v -> {
                            Log.e(EvalFragment.TAG, "EvalCorrectPage clicked.");
                            fragment = new EvalCorrectPage();
                            fragment.setUid(UserInfoManager.getInstance().getUserId());
                            fragment.setDataBean(detail.evaluateBean);
                            fragment.setVoaText(detail);
                            fragment.show(mActivity.getFragmentManager(), "EvalCorrectPage");
                            // stop the play
                            stopAllVoice(null);
                        });
                    } else {
                        // no data to process
                    }
                }
            }
        }
        if (detail.isRead) {
            viewHolder.textIndex.setBackgroundResource(R.drawable.index_green);
            viewHolder.senEn.setText(detail.readResult);
            viewHolder.senEn.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            viewHolder.senReadResult.setVisibility(View.VISIBLE);
            setReadScoreViewContent(detail.readScore, viewHolder.senReadResult);
            viewHolder.senReadPlay.setVisibility(View.VISIBLE);
            viewHolder.senReadSend.setVisibility(View.VISIBLE);
            if ((clickVoaSound != null) && !TextUtils.isEmpty(clickVoaSound.rvc())) {
                //因为在第一次提交之后，第二次进入会出现问题，因此每次需要提交才能分享
//                viewHolder.senReadSend.setVisibility(View.GONE);
//                if (App.APP_SHARE_HIDE > 0) {
//                    viewHolder.senReadShare.setVisibility(View.GONE);
//                } else {
//                    viewHolder.senReadShare.setVisibility(View.VISIBLE);
//                }
                if (TextUtils.isEmpty(tempIdMap.get(String.valueOf(clickPosition)))){
                    viewHolder.senReadSend.setVisibility(View.VISIBLE);
                    viewHolder.senReadShare.setVisibility(View.GONE);
                }else {
                    viewHolder.senReadSend.setVisibility(View.GONE);
                    clickVoaDetail.shuoshuoId = Integer.parseInt(tempIdMap.get(String.valueOf(clickPosition)));
                    viewHolder.senReadShare.setVisibility(View.VISIBLE);
                }
            } else {
                viewHolder.senReadShare.setVisibility(View.GONE);

                //附加条件进行处理
                if (TextUtils.isEmpty(tempIdMap.get(String.valueOf(clickPosition)))){
                    viewHolder.senReadSend.setVisibility(View.VISIBLE);
                    viewHolder.senReadShare.setVisibility(View.GONE);
                }else {
                    viewHolder.senReadSend.setVisibility(View.GONE);
                    clickVoaDetail.shuoshuoId = Integer.parseInt(tempIdMap.get(String.valueOf(clickPosition)));
                    viewHolder.senReadShare.setVisibility(View.VISIBLE);
                }
            }

        } else {
            viewHolder.textIndex.setBackgroundResource(R.drawable.index_gray);
            viewHolder.senEn.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            viewHolder.senReadPlay.setVisibility(View.GONE);
            viewHolder.senReadSend.setVisibility(View.GONE);
            viewHolder.senReadShare.setVisibility(View.GONE);
            viewHolder.senReadResult.setVisibility(View.GONE);
        }
        viewHolder.itemView.setOnClickListener(paramAnonymousView -> {
            //item切换
            if (isRecording ||isEvaluating) {
                ToastUtil.showToast(this.mContext, "正在录音评测中，请暂时不要选择其它条目。");
                return;
            }

            if (EvalAdapter.this.clickPosition != position) {
                if ((EvalAdapter.this.textPlayer != null) && (EvalAdapter.this.textPlayer.isPlaying())) {
                    EvalAdapter.this.textPlayer.pause();
                }
                stopAllVoice(null);
                clickPosition = position;

                //关闭外边的合成音频播放
                if (onEvalListener!=null){
                    onEvalListener.onMixStop();
                }

                clickViewHolder.bottomView.setVisibility(View.VISIBLE);
                clickViewHolder.sepLine.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
                if ((player != null) && (player.isPlaying())) {
                    player.pause();
                }
                handler.removeMessages(0);
            }
        });
        clickViewHolder.senPlay.setOnClickListener(view -> {
            //原文播放
            if ((textPlayer != null) && (textPlayer.isPlaying())) {
                textPlayer.pause();
            }

            //录音时不能播放
            if (isRecording){
                ToastUtil.showToast(mContext,"正在录音中～");
                return;
            }

            stopAllVoice(clickViewHolder.senPlay);

            //关闭外边的合成音频播放
            if (onEvalListener!=null){
                onEvalListener.onMixStop();
            }

            String soundUrl = Constant.getSoundWavUrl(mVoa.sound(), mVoa.voaId());
            Log.e(EvalFragment.TAG, "play Player is soundUrl " + soundUrl);
            if (player == null || player.getMediaPlayer() == null) {
                return;
            }
            if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                ToastUtil.showToast(mContext, "播放原文需要访问数据网络。");
                return;
            }
            if (player.isPlaying() && player.getMediaPlayer().isPlaying()) {
                handler.removeMessages(0);
                player.pause();
                clickViewHolder.senPlay.setBackgroundResource(R.mipmap.sen_play_new);
                clickViewHolder.senPlay.setProgress(0);
                return;
            }
            if (player.isAlreadyGetPrepared()) {
                player.seekTo(((int) clickVoaDetail.timing()  * 1000));
                player.start();
                handler.sendEmptyMessage(0);
                return;
            }
            mSubHandler.post(() -> {
                try {
                    Log.e(EvalFragment.TAG, "play Player is prepareAndPlay ");
                    player.initialize(Constant.getSoundMp3Url(mVoa.sound(), mVoa.voaId()));
                    player.prepareAndPlay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            player.setOnPreparedListener(mp -> {
//                Log.e(EvalFragment.TAG, "play Player is setOnPreparedListener ");
                player.seekTo((int) (clickVoaDetail.timing() * 1000.0D));
                handler.sendEmptyMessage(0);
            });
        });
        this.clickViewHolder.senIRead.setOnClickListener(paramAnonymousView -> {
            //录音
            List<Pair<String, Pair<String,String>>> pairList = new ArrayList<>();
            pairList.add(new Pair<>(Manifest.permission.RECORD_AUDIO,new Pair<>("麦克风权限","录制评测时朗读的音频，用于评测打分使用")));
            pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE,new Pair<>("存储权限","保存评测的音频文件，用于评测打分使用")));

            PermissionDialogUtil.getInstance().showMsgDialog(mContext, pairList, new PermissionDialogUtil.OnPermissionResultListener() {
                @Override
                public void onGranted(boolean isSuccess) {
                    if (isSuccess){
                        startRecordAndEval();
                    }
                }
            });
        });
        clickViewHolder.senReadPlay.setOnClickListener(view -> {
            //评测播放
            if ((textPlayer != null) && (textPlayer.isPlaying())) {
                textPlayer.pause();
            }

            if (isRecording){
                ToastUtil.showToast(mContext,"正在录音中～");
                return;
            }

            stopAllVoice(clickViewHolder.senReadPlay);

            //关闭外边的合成音频播放
            if (onEvalListener!=null){
                onEvalListener.onMixStop();
            }

            if (followPlayer == null) {
                return;
            }
            if (followPlayer.isPlaying()) {
                try {
                    followPlayer.pause();
                    handler.removeMessages(4);
                    clickViewHolder.senReadPlay.setBackgroundResource(R.mipmap.play_ok_new);
                    clickViewHolder.senReadPlay.setProgress(0);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            } else {

                if (TextUtils.isEmpty(clickVoaDetail.imgPath())) {
                    //没有存到本地
                }

                File file = new File(getMP4FileName());
                final String url;
                if (file.exists() && file.isFile()) {
                    if (TextUtils.isEmpty(clickVoaDetail.imgPath())) {
                        //没有存到本地
                        url = Constant.Web.EVAL_PREFIX + clickVoaDetail.evaluateBean.getURL();
                        Log.e(EvalFragment.TAG, "播放网络连接" + url);
                    } else {
                        url = getMP4FileName();
                        Log.e(EvalFragment.TAG, "播放网络连接" + "=== 本地");
                    }

                } else {
                    url = Constant.Web.EVAL_PREFIX + clickVoaDetail.evaluateBean.getURL();
                    Log.e(EvalFragment.TAG, "播放网络连接" + url);
                }
                followPlayer.initialize(url);
                followPlayer.prepareAndPlay();

            }


        });
        this.clickViewHolder.senReadSend.setOnClickListener(view -> {
            //发布单句排行
            if ((textPlayer != null) && (textPlayer.isPlaying())) {
                textPlayer.pause();
            }

            if (isRecording || isEvaluating){
                ToastUtil.showToast(mContext,"正在录音评测中～");
                return;
            }

            //关闭外边的合成音频播放
            if (onEvalListener!=null){
                onEvalListener.onMixStop();
            }

            stopAllVoice(null);
            send();
        });
        this.clickViewHolder.senReadShare.setOnClickListener(paramAnonymousView -> {
            //分享
            if (textPlayer != null && textPlayer.isPlaying()) {
                textPlayer.pause();
            }

            if (isRecording || isEvaluating){
                ToastUtil.showToast(mContext,"正在录音评测中～");
                return;
            }

            //关闭外边的合成音频播放
            if (onEvalListener!=null){
                onEvalListener.onMixStop();
            }

            stopAllVoice(null);
            String userName = UserInfoManager.getInstance().getUserName();
            String content = ((StudyActivity) mContext).tvCeterTop.getText().toString();
            String siteUrl = "http://voa." + Constant.Web.WEB_SUFFIX + "voa/play.jsp?id=" + clickVoaDetail.shuoshuoId
                    + "&addr=" + clickVoaDetail.evaluateBean.getURL() + "&apptype=" + Constant.EVAL_TYPE;
            String imageUrl = App.Url.APP_ICON_URL;
            String title = userName + "在爱语吧评测中获得了" + clickVoaDetail.readScore + "分";
            ShareUtils localShareUtils = new ShareUtils();
            localShareUtils.setMContext(mContext);
            localShareUtils.setVoaId(clickVoaDetail.getVoaId());
            if (ConfigData.openShare) {
                localShareUtils.showShare(mContext, "" + clickVoaDetail.shuoshuoId, imageUrl, siteUrl, title, content, localShareUtils.platformActionListener);
            } else {
                ToastUtil.showToast(mContext, "对不起，分享暂时不支持");
            }
        });
    }

    public void setMixSound(MixSound mixSound) {
        this.mixSound = mixSound;
    }

    public void setPlayer(IJKPlayer myplayer) {
        this.player = myplayer;
    }

    public void stopAllVoice(RoundProgressBar roundProgressBar) {
        //关闭原文音频
        if (clickViewHolder != null && (roundProgressBar != clickViewHolder.senPlay) && (player != null) && (player.isPlaying())) {
            handler.removeMessages(0);
            player.pause();
            clickViewHolder.senPlay.setBackgroundResource(R.mipmap.sen_play_new);
            clickViewHolder.senPlay.setProgress(0);
        }
        //关闭评测音频
        if (clickViewHolder != null && (roundProgressBar != clickViewHolder.senReadPlay) && (followPlayer != null) && (followPlayer.isPlaying())) {
            followPlayer.pause();
            handler.removeMessages(4);
            clickViewHolder.senReadPlay.setBackgroundResource(R.mipmap.play_ok_new);
            clickViewHolder.senReadPlay.setProgress(0);
        }
        //停止录音操作
        if (clickViewHolder != null
                && (roundProgressBar != clickViewHolder.senIRead)
                && (mediaRecordHelper.isRecording)) {
            mediaRecordHelper.stop_record();
            handler.sendEmptyMessage(2);
            handler.removeMessages(3);

            isRecording = false;
            isEvaluating = false;
        }
        mMainHandler.sendEmptyMessage(111);
//        EvalFragment evalFragment = ((StudyActivity) mContext).evalFragment;
//        if ((evalFragment != null) && (evalFragment.isAdded())) {
//            evalFragment.stopMixPlayer();
//        }

        //关闭评测接口
//        EvaluateRequset.getInstance().stopEval();
    }

    public interface MixSound {
        void reStart();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout bottomView;
        LinearLayout wordCorrect;
        private final TextView tvChoose;
        private final Button btCommit;
        TextView senEn;
        RoundProgressBar senIRead;
        RoundProgressBar senPlay;
        RoundProgressBar senReadPlay;
        TextView senReadResult;
        RoundProgressBar senReadSend;
        ImageView senReadShare;
        TextView senZh;
        ImageView sepLine;
        TextView textIndex;

        //增加动画
        ImageView ivAnim;

        MyViewHolder(FragmentEvalItemNewBinding itemView) {
            super(itemView.getRoot());
            bottomView = itemView.bottomView;
            textIndex = itemView.senIndex;
            senEn = itemView.senEn;
            senZh = itemView.senZh;
            wordCorrect = itemView.wordCorrect;
            tvChoose = itemView.chosnWord;
            btCommit = itemView.wordCommit;
            senPlay = itemView.senPlay;
            senPlay.setCricleProgressColor(mContext.getResources().getColor(R.color.eval_progress_color));
            senIRead = itemView.senIRead;
            senIRead.setCricleProgressColor(mContext.getResources().getColor(R.color.eval_progress_color));
            senReadPlay = itemView.senReadPlaying;
            senReadPlay.setCricleProgressColor(mContext.getResources().getColor(R.color.eval_progress_color));
            senReadSend = itemView.senReadSend;
            senReadShare = itemView.senReadCollect;
            senReadResult = itemView.senReadResult;
            sepLine = itemView.sepLine;

            ivAnim = itemView.senReadAnim;
        }
    }

    //用于保存临时的shuoshuoid
    private final Map<String,String> tempIdMap = new HashMap<>();

    //开启评测功能
    private void startRecordAndEval(){
        if ((textPlayer != null) && (textPlayer.isPlaying())) {
            textPlayer.pause();
        }
        stopAllVoice(clickViewHolder.senIRead);
//            mMainHandler.sendEmptyMessage(111);

        //关闭外边的合成音频播放
        if (onEvalListener!=null){
            onEvalListener.onMixStop();
        }

        if (canEvaluate()) {
            clickViewHolder.wordCorrect.setVisibility(View.GONE);
            if (!isRecording) {
                if (!isEvaluating) {
//                        tipDialog();
                    startPC();
                    return;
                }
                ToastUtil.showToast(mContext, "正在评测中，请不要重复提交");
                return;
            }
            handler.removeMessages(3);
            stopPC();
        }
    }

    /****************************接口回调*******************************/
    private OnEvalListener onEvalListener;

    public interface OnEvalListener{
        //评测录音状态
        void onEvalRecord(boolean isRecordAndEval);

        //合成音频关闭
        void onMixStop();
    }

    public void setOnEvalListener(OnEvalListener onEvalListener) {
        this.onEvalListener = onEvalListener;
    }
}
