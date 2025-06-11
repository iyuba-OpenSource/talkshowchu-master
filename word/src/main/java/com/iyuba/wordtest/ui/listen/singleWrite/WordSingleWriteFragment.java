package com.iyuba.wordtest.ui.listen.singleWrite;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.mlkit.vision.digitalink.Ink;
import com.iyuba.wordtest.R;
import com.iyuba.wordtest.adapter.WordListenAdapter;
import com.iyuba.wordtest.databinding.FragmentWordSingleWriteBinding;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.entity.TalkShowWrite;
import com.iyuba.wordtest.entity.WordEntity;
import com.iyuba.wordtest.manager.WordManager;
import com.iyuba.wordtest.ui.listen.WordListenActivity;
import com.iyuba.wordtest.ui.listen.bean.WordListenShowBean;
import com.iyuba.wordtest.ui.listen.singleWrite.model.MLKitManager;
import com.iyuba.wordtest.ui.listen.singleWrite.model.MlKitModelEvent;
import com.iyuba.wordtest.ui.listen.singleWrite.model.MlKitModelSession;
import com.iyuba.wordtest.utils.DateUtil;
import com.iyuba.wordtest.utils.MediaUtils;
import com.iyuba.wordtest.utils.NetworkUtil;
import com.iyuba.wordtest.utils.Share;
import com.iyuba.wordtest.utils.StorageUtil;
import com.iyuba.wordtest.utils.ToastUtil;
import com.iyuba.wordtest.widget.CanvasView;
import com.iyuba.wordtest.wxapi.WordAppData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.NumberFormat;
import java.util.List;

/**
 * 单个单词手写界面
 *
 * 用于单个单词手写功能
 */
public class WordSingleWriteFragment extends Fragment implements WordSingleWriteMvpView {

    //当前数据
    private int bookId;
    private int unitId;
    private String uid;

    //布局
    private FragmentWordSingleWriteBinding binding;

    //数据
    private WordSingleWritePresenter presenter;

    //显示的所有单词数据
    private List<TalkShowWords> wordsList;
    //记录的错误次数(2次就下一个)
    private int errorMark = 0;//错误次数
    private int wordProgress = 0;//单词进度

    //播放器
    private MediaPlayer audioPlayer;//音频播放器
    private MediaPlayer ringPlayer;//音效播放器

    //动画
    private AnimationDrawable anim;

    //手写识别记录
    private Ink.Stroke.Builder strokeBuilder;
    private Ink.Builder inkBuilder = new Ink.Builder();
    //延迟操作的时间
    private long timer_delayOperateTime = 700L;

    //结果展示的适配器
    private WordListenAdapter listenAdapter;

    //结果展示弹窗
    private AlertDialog progressDialog = null;

    public static WordSingleWriteFragment getInstance(int bookId, int unitId){
        WordSingleWriteFragment fragment = new WordSingleWriteFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(WordListenActivity.TAG_BOOKID, bookId);
        bundle.putInt(WordListenActivity.TAG_UNITID, unitId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWordSingleWriteBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new WordSingleWritePresenter();
        presenter.attachView(this);

        initView();
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        stopPlay();
        writeHandler.removeMessages(tag_recognizeText);
        presenter.detachView();
    }

    /*************************初始化************************/
    private void initData() {
        uid = WordManager.getInstance().userid;
        bookId = getArguments().getInt(WordListenActivity.TAG_BOOKID, 0);
        unitId = getArguments().getInt(WordListenActivity.TAG_UNITID, 0);

        //获取进度
        List<TalkShowWrite> listenList = presenter.getUnitWriteWordList(getActivity(), bookId, unitId, uid);
        wordsList = presenter.getUnitWordList(getActivity(), bookId, unitId);
        if (listenList != null && listenList.size() > 0) {
            //检查是否完成
            if (listenList.size() < wordsList.size()) {
                wordProgress = listenList.size()-1;
                showProgressDialog(listenList.size(), wordsList.size());
            } else {
                showScoreView();
            }
        } else {
            //先判断是否下载完成，然后判断是否真的存在
            updateModelUI(true,"正在加载识别模型～");
            if (!MlKitModelSession.getInstance().getModelDownloadState()){
                Log.d("手写模型处理", "模型显示没有下载");
                MLKitManager.getInstance().checkModelDownload(new MLKitManager.OnDigitalCallbackListener<String>() {
                    @Override
                    public void onSuccess(String showMsg) {
                        Log.d("手写模型处理", "显示模型存在，直接展示数据");

                        updateModelUI(false,null);
                        showListenView(0);
                    }

                    @Override
                    public void onFail(String showMsg) {
                        Log.d("手写模型处理", "显示模型不存在，需要手动加载");

                        updateModelUI(false,"加载识别模型失败，请重试～");
                    }
                });
            }
        }
    }

    private void initView(){
        //手写板
        binding.inputView.setOnHandWriteListener(new CanvasView.OnHandWriteListener() {
            @Override
            public void onDown(float touchX, float touchY, long touchTime) {
                strokeBuilder = Ink.Stroke.builder();
                strokeBuilder.addPoint(Ink.Point.create(touchX,touchY,touchTime));

                //关闭识别计时器
                writeHandler.removeMessages(tag_recognizeText);
            }

            @Override
            public void onMove(float touchX, float touchY, long touchTime) {
                strokeBuilder.addPoint(Ink.Point.create(touchX,touchY,touchTime));
            }

            @Override
            public void onUp(float touchX, float touchY, long touchTime) {
                strokeBuilder.addPoint(Ink.Point.create(touchX,touchY,touchTime));
                inkBuilder.addStroke(strokeBuilder.build());
                strokeBuilder = null;

                //开始识别手写内容
                writeHandler.sendEmptyMessageDelayed(tag_recognizeText,timer_delayOperateTime);
            }

            @Override
            public void onClear() {
                inkBuilder = new Ink.Builder();
            }
        });
        //再试一次
        binding.again.setOnClickListener(v->{
            //开启手写功能
            binding.inputView.setVisibility(View.VISIBLE);
            //关闭音频
            stopPlay();
            //展示单词数据
            binding.again.setVisibility(View.GONE);
            showListenView(wordProgress);
        });
        //下一个/查看答案
        binding.next.setOnClickListener(v -> {
            stopPlay();

            String showText = binding.next.getText().toString();
            if (showText.equals(getResources().getString(R.string.spell_next))) {
                //开启手写功能
                binding.inputView.setVisibility(View.VISIBLE);

                binding.again.setVisibility(View.GONE);
                presenter.insertSingleUnitWriteWord(getActivity(), getWriteData(wordProgress));
                errorMark = 0;
                showListenView(wordProgress + 1);
            } else if (showText.equals(getResources().getString(R.string.spell_again))) {
                //开启手写功能
                binding.inputView.setVisibility(View.VISIBLE);
                //展示单词
                showListenView(wordProgress);
            } else if (showText.equals(getResources().getString(R.string.spell_finish))) {
                //开启手写功能
                binding.inputView.setVisibility(View.VISIBLE);

                binding.again.setVisibility(View.GONE);
                presenter.insertSingleUnitWriteWord(getActivity(), getWriteData(wordProgress));
                errorMark = 0;
                showScoreView();
            }else if (showText.equals(getResources().getString(R.string.spell_answer))){
                //关闭手写功能
                binding.inputView.setVisibility(View.INVISIBLE);

                String editWord = formatWord(binding.showWriteView.getText().toString().trim());
                TalkShowWords words = wordsList.get(wordProgress);
                String rightWord = formatWord(words.word.trim());
                binding.showLayout.setVisibility(View.VISIBLE);
                binding.word.setText(words.word);

                Log.d("识别出的信息", "答案信息：("+rightWord+")--手写信息：("+editWord+")");

                if (errorMark<1){
                    if (editWord.equals(rightWord)){
                        binding.showWriteView.setBackgroundResource(R.drawable.shape_round_green_10);
                        playRingAudio(true);
                    }else {
                        binding.again.setVisibility(View.VISIBLE);
                        binding.showWriteView.setBackgroundResource(R.drawable.shape_round_red_10);
                        playRingAudio(false);
                    }
                }else {
                    if (editWord.equals(rightWord)){
                        binding.showWriteView.setBackgroundResource(R.drawable.shape_round_green_10);
                        playRingAudio(true);
                    }else {
                        binding.showWriteView.setBackgroundResource(R.drawable.shape_round_red_10);
                        playRingAudio(false);
                    }
                }

                if (wordProgress==wordsList.size()-1){
                    binding.next.setText(getResources().getString(R.string.spell_finish));
                }else {
                    binding.next.setText(getResources().getString(R.string.spell_next));
                }

                errorMark++;
            }
        });
        //提示信息
        binding.canvasTips.setOnClickListener(v->{
            new AlertDialog.Builder(getActivity())
                    .setTitle("画板技巧")
                    .setMessage("如存在单词或词组过长的情况，可换行书写，模型会自动识别并转换")
                    .create().show();
        });
    }

    //进度部分-显示弹窗
    private void showProgressDialog(int progress, int total) {
        binding.listenView.setVisibility(View.GONE);
        binding.scoreLayout.getRoot().setVisibility(View.GONE);

        progressDialog = new AlertDialog.Builder(getActivity()).create();
        progressDialog.show();
        progressDialog.setCancelable(false);

        Window window = progressDialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_word_progress);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = window.getAttributes();
            int width = getResources().getDisplayMetrics().widthPixels * 4 / 5;
            lp.width = width;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(lp);

            TextView titleView = window.findViewById(R.id.title);
            titleView.setText("温馨提示");
            TextView msgView = window.findViewById(R.id.msg);
            msgView.setText(Html.fromHtml("当前单元总单词数为<b>" + total + "</b>个，当前已经手写测试了<b>" + progress + "</b>个单词，是否继续完成测试？"));
            TextView resetView = window.findViewById(R.id.reset);
            resetView.setText("重新测试");
            resetView.setOnClickListener(v -> {
                progressDialog.dismiss();
                presenter.deleteUnitWriteWord(getActivity(), bookId, unitId, uid);
                showListenView(0);
            });
            TextView keepView = window.findViewById(R.id.keep);
            keepView.setText("继续测试");
            keepView.setOnClickListener(v -> {
                progressDialog.dismiss();
                showListenView(progress);
            });
        }
    }

    /*************************数据处理**********************/
    //展示部分
    private void showScoreView() {
        binding.listenView.setVisibility(View.GONE);
        binding.scoreLayout.getRoot().setVisibility(View.VISIBLE);
        //单元信息
        String unit = WordAppData.getInstance(getActivity()).getBookName() + "Unit" + unitId + "单元";
        String unitMsg = "恭喜你完成" + unit + "的单词手写测试！";
        int index = unitMsg.indexOf(unit);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(unitMsg);
        ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), index, index + unit.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        binding.scoreLayout.unit.setText(ssb);
        //总单词数量
        int totalCount = wordsList.size();
        binding.scoreLayout.totalCount.setText(String.valueOf(totalCount));
        //正确单词数量
        int rightCount = presenter.getUnitRightWriteWordList(getActivity(), bookId, unitId, uid).size();
        binding.scoreLayout.rightCount.setText(String.valueOf(rightCount));
        //得分
        float rightRate = rightCount * 1.0f / totalCount;
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        nf.setGroupingUsed(false);
        int rightRateInt = Integer.parseInt(nf.format(rightRate * 100));
        binding.scoreLayout.rightRate.setText(String.valueOf(rightRateInt));
        if (rightRateInt >= 60) {
            binding.scoreLayout.rightRate.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            binding.scoreLayout.rightRate.setTextColor(getResources().getColor(R.color.red));
        }

        //单词列表（显示正确和错误、点击查看详细内容）
        if (listenAdapter == null) {
            List<WordListenShowBean> showList = presenter.transResultShowData(presenter.getUnitWriteWordList(getActivity(), bookId, unitId, uid));
            listenAdapter = new WordListenAdapter(getActivity(), showList);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            binding.scoreLayout.recyclerView.setLayoutManager(manager);
            binding.scoreLayout.recyclerView.setAdapter(listenAdapter);
            binding.scoreLayout.recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        } else {
            List<WordListenShowBean> showList = presenter.transResultShowData(presenter.getUnitWriteWordList(getActivity(), bookId, unitId, uid));
            listenAdapter.refreshList(showList);
        }
        if (listenAdapter.getItemCount()>0){
            binding.scoreLayout.recyclerView.scrollToPosition(0);
        }
        //分享和重置
        binding.scoreLayout.reset.setOnClickListener(v -> {
            presenter.deleteUnitWriteWord(getActivity(), bookId, unitId, uid);
            showListenView(0);
        });
        binding.scoreLayout.share.setOnClickListener(v -> {
            Share.prepareMessage(getActivity(), WordAppData.getInstance(getActivity()).getAppNameCn(), "我在" + WordAppData.getInstance(getActivity()).getAppNameCn() + "的" + unit + "单词手写测试中获得了" + rightRateInt + "分，你也来试试吧");
        });
    }

    //听写部分
    private void showListenView(int progress) {
        binding.listenView.setVisibility(View.VISIBLE);
        binding.scoreLayout.getRoot().setVisibility(View.GONE);
        wordProgress = progress;

        //音频
        playWordAudio();
        binding.audio.setOnClickListener(v -> {
            playWordAudio();
        });
        //填空
        binding.showWriteView.setEnabled(true);
        binding.showWriteView.setText("");
        binding.showWriteView.setBackgroundResource(R.drawable.shape_round_gray_10);
        //按钮显示
        binding.next.setText(getResources().getString(R.string.spell_answer));
        //正确、错误样式
        binding.showLayout.setVisibility(View.INVISIBLE);
        //序号
        binding.index.setText((progress + 1) + "/" + wordsList.size());
    }

    /******************************音频操作************************/
    //播放单词音频
    private void playWordAudio() {
        try {
            TalkShowWords words = wordsList.get(wordProgress);
            if (TextUtils.isEmpty(words.audio)) {
                //这里通过接口查询数据
                presenter.searchWord(words.word);
                return;
            }

            if (audioPlayer == null) {
                audioPlayer = new MediaPlayer();
            }
            audioPlayer.reset();

            String folder = StorageUtil.getMediaDir(getActivity().getApplicationContext(), bookId, unitId).getAbsolutePath();
            if (StorageUtil.isAudioExist(folder, wordProgress)) {
                audioPlayer.setDataSource(folder + "/" + wordProgress + ".mp3");
            } else {
                if (MediaUtils.isConnected(getActivity())) {
                    audioPlayer.setDataSource(getActivity(), Uri.parse(words.audio));
                } else {
                    ToastUtil.showToast(getActivity(), "暂时没有这个单词的音频，请打开数据网络播放");
                    return;
                }
            }

            audioPlayer.prepareAsync();
            audioPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return false;
                }
            });
            audioPlayer.setOnPreparedListener(mp -> {
                audioPlayer.start();

                //动画效果
                binding.audio.setBackgroundResource(R.drawable.anim_listen_audio);
                anim = (AnimationDrawable) binding.audio.getBackground();
                anim.start();
            });
            audioPlayer.setOnCompletionListener(mp -> {
                if (anim != null) {
                    anim.stop();
                    anim = null;
                }
                binding.audio.setBackgroundResource(R.drawable.ic_wordtest_audio_big);
            });
        } catch (Exception e) {

        }
    }

    //播放音效音频
    private void playRingAudio(boolean isSuccess) {
        int resId = isSuccess ? R.raw.success_short : R.raw.fail_short;
        ringPlayer = MediaPlayer.create(getActivity(),resId);
        ringPlayer.start();
    }

    //停止播放
    private void stopPlay() {
        try {
            if (audioPlayer != null && audioPlayer.isPlaying()) {
                audioPlayer.stop();
                audioPlayer.release();
                audioPlayer = null;
            }
            if (ringPlayer != null && ringPlayer.isPlaying()) {
                ringPlayer.stop();
                ringPlayer.release();
                ringPlayer = null;
            }

            if (anim != null) {
                anim.stop();
            }
        } catch (Exception e) {
            audioPlayer = null;
            ringPlayer = null;
        }
    }

    /******************************手写功能**************************/
    //模型下载
    private void downloadModel(){
        updateModelUI(true,null);

        //先检查模型是否存在，存在则直接显示数据即可；不存在需要先下载模型才行
        MLKitManager.getInstance().checkModelDownload(new MLKitManager.OnDigitalCallbackListener<String>() {
            @Override
            public void onSuccess(String showMsg) {
                updateModelUI(false,null);
                showListenView(0);
            }

            @Override
            public void onFail(String showMsg) {
                MLKitManager.getInstance().downloadModel(new MLKitManager.OnDigitalCallbackListener<String>() {
                    @Override
                    public void onSuccess(String showMsg) {
                        updateModelUI(false,null);
                        showListenView(0);
                    }

                    @Override
                    public void onFail(String showMsg) {
                        updateModelUI(false,"加载识别模型失败，请重试～");
                    }
                });
            }
        });
    }

    //手写识别
    private void recognizeText(){
        MLKitManager.getInstance().recognize(inkBuilder.build(), new MLKitManager.OnDigitalCallbackListener<String>() {
            @Override
            public void onSuccess(String showMsg) {
                //这里可以直接写词组，需要分行写就行
                binding.showWriteView.setText(showMsg.trim());
                //重置手写板
                binding.inputView.clearCanvas();
            }

            @Override
            public void onFail(String showMsg) {
                binding.showWriteView.setText("识别出错，请重试");
                //重置手写板
                binding.inputView.clearCanvas();
            }
        });
    }

    //功能操作
    private static final int tag_recognizeText = 100;//文本识别

    @SuppressLint("HandlerLeak")
    private Handler writeHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case tag_recognizeText:
                    //文本识别
                    recognizeText();
                    break;
            }

            super.handleMessage(msg);
        }
    };

    /************************************其他方法**********************************/
    //更新模型加载显示
    private void updateModelUI(boolean isLoading,String showMsg){
        if (isLoading){
            binding.stateLayout.getRoot().setVisibility(View.VISIBLE);
            binding.stateLayout.progressLoading.setVisibility(View.VISIBLE);
            binding.stateLayout.showMsg.setText("正在加载识别模型～");
            binding.stateLayout.reLoadBtn.setVisibility(View.INVISIBLE);
        }else {
            if (TextUtils.isEmpty(showMsg)){
                binding.stateLayout.getRoot().setVisibility(View.GONE);
            }else {
                binding.stateLayout.getRoot().setVisibility(View.VISIBLE);
                binding.stateLayout.progressLoading.setVisibility(View.INVISIBLE);
                binding.stateLayout.showMsg.setText(showMsg);
                binding.stateLayout.reLoadBtn.setVisibility(View.VISIBLE);
                binding.stateLayout.reLoadBtn.setOnClickListener(v->{
                    if (!NetworkUtil.isConnected(getActivity())){
                        updateModelUI(false,"请链接网络后加载模型");
                        return;
                    }

                    downloadModel();
                });
            }
        }
    }

    //将单词样式规范处理下
    private String formatWord(String showWord){
        //将单词中的特殊符号处理下
        showWord = showWord.replace("?","");
        showWord = showWord.replace("!","");

        //将单词中的大小写处理下
        showWord = showWord.toLowerCase();

        return showWord;
    }

    //需要插入的单词数据
    private TalkShowWrite getWriteData(int progress) {
        TalkShowWrite listen = new TalkShowWrite();
        listen.book_id = bookId;
        listen.unit_id = unitId;
        listen.position = progress;
        listen.uid = WordManager.getInstance().userid;

        TalkShowWords words = wordsList.get(progress);
        listen.word = words.word;
        listen.porn = words.pron;
        listen.def = words.def;
        listen.audio = words.audio;

        String spellWord = binding.showWriteView.getText().toString().trim().toLowerCase();
        int status = formatWord(spellWord).equals(formatWord(words.word)) ? 1 : 0;
        listen.spell = spellWord;
        listen.status = status;
        listen.error_count = errorMark;
        listen.update_time = DateUtil.formatTime(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
        return listen;
    }

    /*********************************回调*******************************/
    //单词查询成功
    @Override
    public void wordSearchSuccess(WordEntity entity) {
        if (entity!=null&&!TextUtils.isEmpty(entity.audio)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TalkShowWords words = wordsList.get(wordProgress);
                    words.audio = entity.audio;

                    presenter.updateSingleWord(getActivity(),words);
                    wordsList = presenter.getUnitWordList(getActivity(), bookId, unitId);

                    playWordAudio();
                }
            }).start();
        }else {
            ToastUtil.showToast(getActivity(), "此单词暂无音频");
        }
    }

    //单词查询失败
    @Override
    public void wordSearchFail(String msg) {
        ToastUtil.showToast(getActivity(),msg);
    }

    //下载回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MlKitModelEvent event){
        if (event.getShowTag().equals(MlKitModelEvent.tag_success)){
            Log.d("手写模型处理", "接收模型加载成功的通知");

            updateModelUI(false,null);
            showListenView(0);
        }else if (event.getShowTag().equals(MlKitModelEvent.tag_fail)){
            Log.d("手写模型处理", "接收模型加载失败的通知");

            updateModelUI(false,"加载识别模型失败，请重试～");
        }
    }
}
