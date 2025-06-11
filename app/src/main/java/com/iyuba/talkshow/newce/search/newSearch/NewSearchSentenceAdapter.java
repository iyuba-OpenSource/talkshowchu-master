package com.iyuba.talkshow.newce.search.newSearch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iyuba.module.toolbox.GsonUtils;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaSoundNew;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.databinding.ItemSearchSentenceBinding;
import com.iyuba.talkshow.newce.search.util.ExerciseFixTimerSingle;
import com.iyuba.talkshow.newce.search.util.ExerciseSingle;
import com.iyuba.talkshow.newce.search.view.ExercisePlayer;
import com.iyuba.talkshow.newce.study.eval.EvalFragment;
import com.iyuba.talkshow.newdata.AudioSendApi;
import com.iyuba.talkshow.newdata.Config;
import com.iyuba.talkshow.newdata.EvaluateBean;
import com.iyuba.talkshow.newdata.EvaluateRequset;
import com.iyuba.talkshow.newdata.SPconfig;
import com.iyuba.talkshow.newview.RoundProgressBar;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.talkshow.util.TextAttr;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.talkshow.util.iseutil.ResultParse;
import com.iyuba.wordtest.utils.RecordManager;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询-句子适配器
 */
public class NewSearchSentenceAdapter extends RecyclerView.Adapter<NewSearchSentenceAdapter.SentenceHolder> {

    private Context context;
    private List<VoaText> list;

    //选中的位置
    private int selectPosition = 0;
    //选中的holder
    private SentenceHolder selectHolder;
    //选中的数据
    private VoaText selectBean;
    //选中的voa
    private Voa selectVoa;
    //选中的评测结果
    private VoaSoundNew selectEval;

    //数据
    private NewSearchPresenter presenter;
    //播放器
    private ExercisePlayer exercisePlayer;
    //录音器
    private RecordManager recordManager;
    //当前时间
    private long timeStamp = System.currentTimeMillis();
    //当前正在播放的音频链接
    private String curPlayUrl;

    //是否正在播放音频
    private int playAudioState = 0;//0-无状态，1-音频播放，2-评测播放
    private static final int PLAY_STATE_NO = 0;
    private static final int PLAY_STATE_AUDIO = 1;
    private static final int PLAY_STATE_EVAL = 2;

    public NewSearchSentenceAdapter(Context context, List<VoaText> list, NewSearchPresenter presenter) {
        this.context = context;
        this.list = list;

        this.presenter = presenter;

        this.exercisePlayer = ExercisePlayer.getInstance(context);
    }

    @NonNull
    @Override
    public SentenceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSearchSentenceBinding binding = ItemSearchSentenceBinding.inflate(LayoutInflater.from(context),parent,false);
        return new SentenceHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SentenceHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder==null){
            return;
        }

        //数据
        VoaText voaText = list.get(position);
        //序号
        holder.index.setText(String.valueOf(position+1));
        //句子
        holder.sentence.setText(voaText.sentence());
        //翻译
        holder.sentenceCn.setText(voaText.sentenceCn());
        //播放
        Voa curVoa = presenter.getVoa(voaText.getVoaId());
        if (curVoa!=null){
            holder.play.setVisibility(View.VISIBLE);
        }else {
            holder.play.setVisibility(View.INVISIBLE);
        }
        //评测播放
        VoaSoundNew voaSoundNew = presenter.getVoaSound(voaText.getVoaId(),voaText.paraId(),voaText.idIndex());
        if (voaSoundNew!=null){
            holder.read.setVisibility(View.VISIBLE);
            holder.score.setVisibility(View.VISIBLE);

            //显示分数
            holder.score.setVisibility(View.VISIBLE);
            holder.score.setText(String.valueOf(voaSoundNew.totalscore()));
            //显示句子
            String[] scoreArray = voaSoundNew.wordscore().split(",");
            holder.sentence.setText(ResultParse.getSenResultLocal(scoreArray,voaText.sentence()));
            //显示序号
            holder.index.setBackgroundResource(R.drawable.index_green);
        }else {
            holder.read.setVisibility(View.GONE);
            holder.read.setVisibility(View.GONE);

            holder.score.setVisibility(View.GONE);
            holder.index.setBackgroundResource(R.drawable.index_gray);
            holder.sentence.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }

        holder.itemView.setOnClickListener(v->{
            if (ExerciseSingle.getInstance().isRecord()){
                ToastUtil.showToast(context,"正在录音中");
                return;
            }

            if (ExerciseSingle.getInstance().isEval()){
                ToastUtil.showToast(context,"正在评测中");
                return;
            }

            if (onSentenceListener!=null){
                onSentenceListener.onStopPlay();
            }

            selectPosition = position;
            pausePlay();
            this.notifyDataSetChanged();
        });

        holder.play.setOnClickListener(v->{
            if (ExerciseSingle.getInstance().isRecord()){
                ToastUtil.showToast(context,"正在录音中");
                return;
            }

            if (ExerciseSingle.getInstance().isEval()){
                ToastUtil.showToast(context,"正在评测中");
                return;
            }

            if (onSentenceListener!=null){
                onSentenceListener.onStopPlay();
            }

            selectPosition = position;
            selectBean = voaText;
            selectVoa = curVoa;
            selectHolder = holder;
            selectEval = voaSoundNew;

            if (playAudioState!=PLAY_STATE_AUDIO){
                startPlay();
            }else {
                pausePlay();
            }

            //播放链接
//            String audioUrl = getPlayUrl();
//            //时间
//            int startTime = (int) (selectBean.timing()*1000);
//            int endTime = (int) (selectBean.endTiming()*1000);
//
//            if (onSentenceListener!=null){
//                onSentenceListener.onPlayAudio(audioUrl,startTime,endTime);
//            }
        });
        holder.record.setOnClickListener(v->{
            if (ExerciseSingle.getInstance().isEval()){
                ToastUtil.showToast(context,"正在评测中");
                return;
            }

            if (!PermissionX.isGranted(context, Manifest.permission.RECORD_AUDIO)
                    ||!PermissionX.isGranted(context,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                PermissionX.init((FragmentActivity) context)
                        .permissions(Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .request(new RequestCallback() {
                            @Override
                            public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                                if (allGranted){
                                    selectHolder.stateMsg.setText("授权成功，请进行录音评测");
                                }else {
                                    selectHolder.stateMsg.setText("授权失败，请进行手动授权");
                                }
                            }
                        });
                return;
            }

            if (onSentenceListener!=null){
                onSentenceListener.onStopPlay();
            }

            selectPosition = position;
            selectBean = voaText;
            selectVoa = curVoa;
            selectHolder = holder;
            selectEval = voaSoundNew;

            pausePlay();

//            if (ExerciseSingle.getInstance().isRecord()){
//                handler.sendEmptyMessage(RECORD_AND_EVAL);
//            }else {
//                startRecord();
//            }
        });
        holder.read.setOnClickListener(v->{
            if (ExerciseSingle.getInstance().isRecord()){
                ToastUtil.showToast(context,"正在录音中，请暂时不要选择其他条目");
                return;
            }

            if (ExerciseSingle.getInstance().isEval()){
                ToastUtil.showToast(context,"正在评测中，请暂时不要选择其他条目");
                return;
            }

            if (onSentenceListener!=null){
                onSentenceListener.onStopPlay();
            }

            selectPosition = position;
            selectBean = voaText;
            selectVoa = curVoa;
            selectHolder = holder;
            selectEval = voaSoundNew;

            if (playAudioState!=PLAY_STATE_EVAL){
                startEvalPlay();
            }else {
                pausePlay();
            }

//            String audioUrl = getEvalUrl();
//            if (onSentenceListener!=null){
//                onSentenceListener.onPlayEval(audioUrl);
//            }
        });

        if (selectPosition == position){
            holder.lineView.setVisibility(View.VISIBLE);
            holder.bottomView.setVisibility(View.VISIBLE);
        }else {
            holder.lineView.setVisibility(View.GONE);
            holder.bottomView.setVisibility(View.GONE);
        }

        //编写默认的数据
        if (selectPosition==position){
            selectPosition = position;
            selectBean = voaText;
            selectVoa = curVoa;
            selectHolder = holder;
            selectEval = voaSoundNew;
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class SentenceHolder extends RecyclerView.ViewHolder{

        private TextView index;
        private TextView sentence;
        private TextView sentenceCn;

        private ImageView lineView;
        private LinearLayout bottomView;

        private RoundProgressBar play;
        private RoundProgressBar record;
        private RoundProgressBar read;
        private TextView stateMsg;
        private TextView score;
        private ImageView loading;

        public SentenceHolder(ItemSearchSentenceBinding binding){
            super(binding.getRoot());

            index = binding.senIndex;
            sentence = binding.senEn;
            sentenceCn = binding.senZh;

            lineView = binding.sepLine;
            bottomView = binding.bottomView;

            play = binding.senPlay;
            play.setCricleProgressColor(context.getResources().getColor(R.color.eval_progress_color));
            record = binding.senIRead;
            record.setCricleProgressColor(context.getResources().getColor(R.color.eval_progress_color));
            read = binding.senReadPlaying;
            read.setCricleProgressColor(context.getResources().getColor(R.color.eval_progress_color));
            score = binding.senReadResult;

            stateMsg = binding.stateMsg;
            loading = binding.senReadAnim;
        }
    }

    //刷新数据
    public void refreshData(List<VoaText> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //开始播放
    private void startPlay(){
        if (exercisePlayer!=null&&exercisePlayer.isPlaying()){
            pausePlay();
        }

        if (!NetStateUtil.isConnected(context)){
            ToastUtil.showToast(context,"请链接网络后进行播放");
            return;
        }

        playAudioState = PLAY_STATE_AUDIO;

        try {
            selectHolder.stateMsg.setText("正在缓冲原音音频...");

            //播放链接
            String audioUrl = getPlayUrl();

            //时间
            int startTime = (int) (selectBean.timing()*1000);
            int endTime = (int) (selectBean.endTiming()*1000);

            exercisePlayer = ExercisePlayer.getInstance(context);
            exercisePlayer.setResource(audioUrl);
            exercisePlayer.setOnAudioPlayerListener(new ExercisePlayer.OnAudioPlayerListener() {
                @Override
                public void onPrepared() {
                    selectHolder.stateMsg.setText("正在播放原音音频...");
                    exercisePlayer.seekTo(startTime);
                    exercisePlayer.start();
                    //倒计时器
                    ExerciseFixTimerSingle.getInstance().startPlayerTimer(exercisePlayer, endTime, new ExerciseFixTimerSingle.OnTimerCallBackListener() {
                        @Override
                        public void onTick() {
                            handler.sendEmptyMessage(ANIM_PLAY);
                        }

                        @Override
                        public void onFinish() {
                            pausePlay();
                        }
                    });
                }

                @Override
                public void onCompletion() {
                    pausePlay();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            ExerciseFixTimerSingle.getInstance().stopTimer();
            selectHolder.stateMsg.setText("播放原音音频失败");
        }
    }

    //开始评测播放
    private void startEvalPlay(){
        if (exercisePlayer!=null&&exercisePlayer.isPlaying()){
            pausePlay();
        }

        if (!NetStateUtil.isConnected(context)){
            ToastUtil.showToast(context,"请链接网络后进行播放");
            return;
        }

        playAudioState = PLAY_STATE_EVAL;

        String audioUrl = getEvalUrl();

        try {
            selectHolder.stateMsg.setText("正在缓冲评测音频...");

            exercisePlayer = ExercisePlayer.getInstance(context);
            exercisePlayer.setResource(audioUrl);
            exercisePlayer.setOnAudioPlayerListener(new ExercisePlayer.OnAudioPlayerListener() {
                @Override
                public void onPrepared() {
                    selectHolder.stateMsg.setText("正在播放评测音频...");
                    exercisePlayer.seekTo(0);
                    exercisePlayer.start();

                    //定时器
                    ExerciseFixTimerSingle.getInstance().startPlayerTimer(exercisePlayer, exercisePlayer.getDuration(), new ExerciseFixTimerSingle.OnTimerCallBackListener() {
                        @Override
                        public void onTick() {
                            //动画
                            handler.sendEmptyMessage(ANIM_EVAL);
                        }

                        @Override
                        public void onFinish() {
                            pausePlay();
                        }
                    });
                }

                @Override
                public void onCompletion() {
                    pausePlay();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            ExerciseFixTimerSingle.getInstance().stopTimer();
            selectHolder.stateMsg.setText("播放评测音频失败");
        }
    }

    //暂停播放
    private void pausePlay(){
        selectHolder.stateMsg.setText("");

        exercisePlayer.pause();
        playAudioState = PLAY_STATE_NO;

        //关闭定时器
        ExerciseFixTimerSingle.getInstance().stopTimer();

        //动画
        selectHolder.play.setProgress(0);
        selectHolder.play.setBackgroundResource(R.mipmap.sen_play_new);
        removeHandler(ANIM_PLAY);

        selectHolder.read.setProgress(0);
        selectHolder.read.setBackgroundResource(R.mipmap.play_ok_new);
        removeHandler(ANIM_EVAL);
    }

    //开始录音
    private void startRecord(){
        if (!NetStateUtil.isConnected(context)){
            ToastUtil.showToast(context,"请链接网络后进行播放");
            return;
        }

        try {
            selectHolder.stateMsg.setText("录音中...");

            makeRootDirectory(Constant.getsimRecordAddr());
            String savePath = getMP3FileName();
            //创建文件
            File file = new File(savePath);
            if (file.exists()){
                file.delete();
            }else {
                if (!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
            }
            //创建空的文件
            file.createNewFile();

            ExerciseSingle.getInstance().setRecord(true);

            recordManager = new RecordManager(file);
            recordManager.startRecord();
            //动画
            handler.sendEmptyMessage(ANIM_RECORD);
            //延迟关闭
            int startTime = (int) (selectBean.timing()*1000);
            int endTime = (int) (selectBean.endTiming()*1000);
            int playTime = endTime-startTime+3000;
            handler.sendEmptyMessageDelayed(RECORD_AND_EVAL,playTime);
        }catch (Exception e){
            e.printStackTrace();
            selectHolder.stateMsg.setText("录音失败");
        }
    }

    //停止录音
    private void stopRecord(){
        if (selectHolder!=null){
            selectHolder.stateMsg.setText("");
            //动画
            selectHolder.record.setProgress(0);
            selectHolder.record.setBackgroundResource(R.drawable.sen_i_read_new2);
        }
        removeHandler(ANIM_RECORD);

        if (recordManager!=null){
            recordManager.stopRecord();
        }
        ExerciseSingle.getInstance().setRecord(false);

        //移除提交
        removeHandler(RECORD_AND_EVAL);

        //关闭定时器
        ExerciseFixTimerSingle.getInstance().stopTimer();
    }

    //提交评测
    private void submitEval(){
        selectHolder.stateMsg.setVisibility(View.GONE);
        selectHolder.score.setVisibility(View.GONE);
        selectHolder.loading.setVisibility(View.VISIBLE);
        Glide.with(context).asGif().load(R.drawable.ic_loading).into(selectHolder.loading);
        selectHolder.stateMsg.setText("正在提交评测中...");

        String audioPath = getMP3FileName();
        File file = new File(audioPath);
        if (file.exists()){
            ExerciseSingle.getInstance().setEval(true);

            Map<String,String> map = new HashMap<>();
            map.put("type", Constant.EVAL_TYPE);
            map.put("userId", String.valueOf(presenter.getUid()));
            map.put("newsId", String.valueOf(selectVoa.voaId()));
            map.put("platform", AudioSendApi.platform);
            map.put("protocol", AudioSendApi.protocol);
            map.put("paraId", String.valueOf(selectBean.paraId()));
            map.put("IdIndex", String.valueOf(selectBean.idIndex()));

            String urlSentence = TextAttr.encode(selectBean.sentence());
            urlSentence = urlSentence.replaceAll("\\+", "%20");
            map.put("sentence", urlSentence);

            try {
                map.put("flg", "0");
                map.put("wordId", "0");
                map.put("appId", App.APP_ID+"");
                EvaluateRequset.getInstance().post(Constant.Web.EVALUATE_URL_CORRECT, map, audioPath, handler);
            }catch (Exception e){
                e.printStackTrace();
                handler.sendEmptyMessage(EVAL_FAIL);
            }
        }else {
            handler.sendEmptyMessage(EVAL_FAIL);
        }
    }

    //完成评测
    private void stopEval(String result){
        EvaluateBean evaluateBean = GsonUtils.toObject(result,EvaluateBean.class);
        ExerciseSingle.getInstance().setEval(false);

        //获取单词分数
        StringBuffer wordsScore = new StringBuffer();
        for (int i = 0; i < evaluateBean.getWords().size(); i++) {
            EvaluateBean.WordsBean wordsBean = evaluateBean.getWords().get(i);
            wordsScore.append(wordsBean.getScore());

            if (i<evaluateBean.getWords().size()-1){
                wordsScore.append(",");
            }
        }

        //单词分数
        String scoreArrayStr = wordsScore.toString();
        //总分数
        int totalScore = (int) (Double.parseDouble(evaluateBean.getTotal_score())*20.00);

        //保存到数据库
        VoaSoundNew voaSoundNew = VoaSoundNew.builder()
                .setUid(presenter.getUid())
                .setVoa_id(selectBean.getVoaId())
                .setItemid(Long.parseLong(selectBean.getVoaId()+""+selectBean.paraId()+""+selectBean.idIndex()))
                .setSound_url(evaluateBean.getURL())
                .setFilepath(getMP3FileName())
                .setRvc("")
                .setTotalscore(totalScore)
                .setWordscore(scoreArrayStr)
                .setTime(String.valueOf(timeStamp))
                .setWords(result)
                .build();
        presenter.saveVoaSoundResult(voaSoundNew);
        //刷新数据
        this.notifyDataSetChanged();
    }

    //移除handler
    private void removeHandler(int msg){
        if (handler.hasMessages(msg)){
            handler.removeMessages(msg);
        }
    }

    private static final int ANIM_PLAY = 1;//播放
    private static final int ANIM_RECORD = 2;//录音
    private static final int ANIM_EVAL = 3;//评测
    private static final int RECORD_AND_EVAL = 4;//录音后评测
    private static final int PAUSE_PLAY = 5;//暂停播放
    private static final int EVAL_FAIL = 14;//评测失败
    private static final int EVAL_SUCCESS = 15;//评测成功
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case ANIM_PLAY:
                    //播放
                    RoundProgressBar playBar = selectHolder.play;
                    playBar.setBackgroundResource(R.mipmap.sen_stop_new);
                    int startTime = (int) (selectBean.timing()*1000);
                    int endTime = (int) (selectBean.endTiming()*1000);
                    int totalTime = endTime-startTime;
                    int curTime = exercisePlayer.getCurPosition() - startTime;
                    if (curTime<0){
                        curTime = 0;
                    }

                    playBar.setMax(totalTime);
                    playBar.setProgress(curTime);

                    Log.d("显示进度", "all--"+totalTime+"--cur--"+curTime);
                    break;
                case ANIM_RECORD:
                    //录音动画
                    RoundProgressBar recordBar = selectHolder.record;
                    recordBar.setMax(100);
                    int progress = (int) recordManager.getVolume();
                    recordBar.setProgress(progress);

                    handler.sendEmptyMessageDelayed(ANIM_RECORD,100L);
                    break;
                case ANIM_EVAL:
                    //评测播放动画
                    RoundProgressBar readBar = selectHolder.read;
                    readBar.setBackgroundResource(R.mipmap.sen_stop_new);
                    int readAllTime = exercisePlayer.getDuration();
                    int readCurTime = exercisePlayer.getCurPosition();
                    if (readCurTime<0){
                        readCurTime = 0;
                    }

                    if (readAllTime<0){
                        readAllTime = 0;
                    }

                    readBar.setMax(readAllTime);
                    readBar.setProgress(readCurTime);

                    Log.d("显示进度", "all--"+readAllTime+"--cur--"+readCurTime);
                    break;
                case RECORD_AND_EVAL:
                    //停止录音并且提交评测
                    stopRecord();

                    submitEval();
                    break;
                case PAUSE_PLAY:
                    //暂停播放
                    pausePlay();
                    break;
                case EVAL_FAIL:
                    //评测失败
                    selectHolder.stateMsg.setText("提交评测失败");
                    selectHolder.loading.setVisibility(View.GONE);
                    selectHolder.score.setVisibility(View.VISIBLE);
                    ExerciseSingle.getInstance().setEval(false);
                    break;
                case EVAL_SUCCESS:
                    //评测成功
                    selectHolder.stateMsg.setText("评测完成");
                    selectHolder.loading.setVisibility(View.GONE);
                    selectHolder.score.setVisibility(View.VISIBLE);

                    stopEval((String) msg.obj);
                    break;
            }
        }
    };

    //停止播放和录音
    public void stopAudio(){
        pausePlay();
        stopRecord();
//        refreshAudioPlay(0,0,true);
//        refreshEvalPlay(0,0,true);

        ExerciseSingle.getInstance().setEval(false);
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

    //获取文件夹路径
    private String getMP3FileName() {
        VoaText mVoaText = list.get(selectPosition);
        if (mVoaText != null) {
            File saveFile = StorageUtil.getAccRecordFile(context, mVoaText.getVoaId(), timeStamp, mVoaText.paraId());
            Log.e(EvalFragment.TAG, "getMP4FileName saveFile.getAbsolutePath() " + saveFile.getAbsolutePath());
            return saveFile.getAbsolutePath();
        }
        boolean isAmerican = SPconfig.Instance().loadBoolean(Config.ISAMEICAN, true);
        if (isAmerican)
            return Constant.getsimRecordAddr() + "/" + selectBean.getVoaId() + selectBean.paraId() + ".mp3";
        else
            return Constant.getsimRecordAddr() + "/" + (selectBean.getVoaId() * 10) + selectBean.paraId() + ".mp3";
    }

    //需要播放的原音链接
    private String getPlayUrl(){
        String audioUrl = Constant.getSoundMp3Url(selectVoa.sound(),selectVoa.voaId());
        //获取名称
//        int index = selectVoa.sound().lastIndexOf("/");
//        if (index==-1){
//            return audioUrl;
//        }
//
//        String audioName = audioUrl.substring(index+1);
//        String audioPath = ExerciseUtil.getExerciseAudioPath(audioName,"/search/audio/");
//        File file = new File(audioPath);
//        if (file.exists()){
//            return audioPath;
//        }
        return audioUrl;
    }

    //需要播放的评测链接
    private String getEvalUrl(){
        String audioUrl = "";

        if (!TextUtils.isEmpty(selectEval.filepath())){
            String evalAudioPath = selectEval.filepath();

            File file = new File(evalAudioPath);
            if (file.exists()){
                audioUrl = evalAudioPath;
            }else {
                audioUrl = Constant.Web.EVAL_PREFIX+selectEval.sound_url();
            }
        }else {
            audioUrl = Constant.Web.EVAL_PREFIX+selectEval.sound_url();
        }

        return audioUrl;
    }

    /************************************新的操作方式************************/
    //回调接口
    private OnSentenceListener onSentenceListener;

    public interface OnSentenceListener{
        //选中条目
//        void onSelectItem(int position);
        //播放原音
//        void onPlayAudio(String audioUrl,long startTime,long endTime);
        //录制音频
//        void onRecord();
        //播放评测
//        void onPlayEval(String audioUrl);

        //停止播放
        void onStopPlay();
    }

    public void setOnSentenceListener(OnSentenceListener onSentenceListener) {
        this.onSentenceListener = onSentenceListener;
    }

//    //刷新原音播放显示
//    public void refreshAudioPlay(long progressTime,long totalTime,boolean isFinish){
//        if (selectHolder==null){
//            return;
//        }
//
//        if (totalTime<=0){
//            totalTime = 0;
//        }
//        if (progressTime<=0){
//            progressTime = 0;
//        }
//
//        if (isFinish){
//            selectHolder.play.setBackgroundResource(R.mipmap.sen_play_new);
//            selectHolder.play.setMax(0);
//            selectHolder.play.setProgress(0);
//        }else {
//            selectHolder.play.setBackgroundResource(R.mipmap.sen_stop_new);
//            selectHolder.play.setMax((int) totalTime);
//            selectHolder.play.setProgress((int) progressTime);
//        }
//    }
//
//    //刷新评测播放显示
//    public void refreshEvalPlay(long progressTime,long totalTime,boolean isFinish){
//        if (selectHolder==null){
//            return;
//        }
//
//        if (totalTime<=0){
//            totalTime = 0;
//        }
//        if (progressTime<=0){
//            progressTime = 0;
//        }
//
//        if (isFinish){
//            selectHolder.read.setBackgroundResource(R.mipmap.play_ok_new);
//            selectHolder.read.setMax(0);
//            selectHolder.read.setProgress(0);
//        }else {
//            selectHolder.read.setBackgroundResource(R.mipmap.sen_stop_new);
//            selectHolder.read.setMax((int) totalTime);
//            selectHolder.read.setProgress((int) progressTime);
//        }
//    }
}
