//package com.iyuba.wordtest.ui.listen.singleListen;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.AnimationDrawable;
//import android.graphics.drawable.ColorDrawable;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.Html;
//import android.text.Spannable;
//import android.text.SpannableStringBuilder;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.text.style.ForegroundColorSpan;
//import android.view.Gravity;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.iyuba.wordtest.R;
//import com.iyuba.wordtest.adapter.WordListenAdapter;
//import com.iyuba.wordtest.databinding.ActivityWordListenBinding;
//import com.iyuba.wordtest.entity.TalkShowListen;
//import com.iyuba.wordtest.entity.TalkShowWords;
//import com.iyuba.wordtest.entity.WordEntity;
//import com.iyuba.wordtest.manager.WordManager;
//import com.iyuba.wordtest.ui.TalkshowWordListActivity;
//import com.iyuba.wordtest.utils.DateUtil;
//import com.iyuba.wordtest.utils.MediaUtils;
//import com.iyuba.wordtest.utils.Share;
//import com.iyuba.wordtest.utils.StorageUtil;
//import com.iyuba.wordtest.utils.ToastUtil;
//import com.iyuba.wordtest.wxapi.WordAppData;
//import com.jaeger.library.StatusBarUtil;
//
//import java.text.NumberFormat;
//import java.util.List;
//
///**
// * @desction: 单词听写
// * @date: 2023/2/7 10:18
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// */
//public class WordSingleListenActivity extends AppCompatActivity implements WordSingleListenMvpView {
//
//    //当前数据
//    private int bookId;
//    private int unitId;
//    private String uid;
//
//    //布局
//    private ActivityWordListenBinding binding;
//
//    //数据
//    private WordSingleListenPresenter presenter;
//
//    private List<TalkShowWords> wordsList;
//    private int errorMark = 0;//错误次数
//    private int wordProgress = 0;//单词进度
//
//    //适配器
//    private WordListenAdapter listenAdapter;
//
//    //样式
//    private AlertDialog progressDialog = null;
//    private MediaPlayer audioPlayer;
//        private MediaPlayer ringPlayer;
////    private SoundPool ringPlayer;
//    private AnimationDrawable anim;
//
//    public static void start(Context context, int bookId, int unitId) {
//        Intent intent = new Intent();
//        intent.setClass(context, WordSingleListenActivity.class);
//        intent.putExtra(TalkshowWordListActivity.BOOKID, bookId);
//        intent.putExtra(TalkshowWordListActivity.UNIT, unitId);
//        context.startActivity(intent);
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityWordListenBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
//        presenter = new WordSingleListenPresenter();
//        presenter.attachView(this);
//
//        initView();
//        initData();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        stopPlay();
//
//        if (audioPlayer != null) {
//            audioPlayer = null;
//        }
//        if (ringPlayer != null) {
//            ringPlayer = null;
//        }
//    }
//
//    private void initView() {
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("单词听写");
//    }
//
//    private void initData() {
//        uid = WordManager.getInstance().userid;
//        bookId = getIntent().getIntExtra(TalkshowWordListActivity.BOOKID, 0);
//        unitId = getIntent().getIntExtra(TalkshowWordListActivity.UNIT, 0);
//
//        //获取进度
//        List<TalkShowListen> listenList = presenter.getUnitListenWordList(this, bookId, unitId, uid);
//        wordsList = presenter.getUnitWordList(this, bookId, unitId);
//        if (listenList != null && listenList.size() > 0) {
//            //检查是否完成
//            if (listenList.size() < wordsList.size()) {
//                showProgressDialog(listenList.size(), wordsList.size());
//            } else {
//                showScoreView();
//            }
//        } else {
//            showListenView(0);
//        }
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            finish();
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    /******展示部分******/
//    private void showScoreView() {
//        binding.listenView.setVisibility(View.GONE);
//        binding.scoreLayout.setVisibility(View.VISIBLE);
//        //单元信息
//        String unit = WordAppData.getInstance(this).getBookName() + "Unit" + unitId + "单元";
//        String unitMsg = "恭喜你完成" + unit + "的单词听写！";
//        int index = unitMsg.indexOf(unit);
//        SpannableStringBuilder ssb = new SpannableStringBuilder();
//        ssb.append(unitMsg);
//        ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), index, index + unit.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//        binding.unit.setText(ssb);
//        //总单词数量
//        int totalCount = wordsList.size();
//        binding.totalCount.setText(String.valueOf(totalCount));
//        //正确单词数量
//        int rightCount = presenter.getUnitRightListenWordList(this, bookId, unitId, uid).size();
//        binding.rightCount.setText(String.valueOf(rightCount));
//        //得分
//        float rightRate = rightCount * 1.0f / totalCount;
//        NumberFormat nf = NumberFormat.getInstance();
//        nf.setMaximumFractionDigits(0);
//        nf.setGroupingUsed(false);
//        int rightRateInt = Integer.parseInt(nf.format(rightRate * 100));
//        binding.rightRate.setText(String.valueOf(rightRateInt));
//        if (rightRateInt >= 60) {
//            binding.rightRate.setTextColor(getResources().getColor(R.color.colorPrimary));
//        } else {
//            binding.rightRate.setTextColor(getResources().getColor(R.color.red));
//        }
//
//        //单词列表（显示正确和错误、点击查看详细内容）
//        if (listenAdapter == null) {
//            listenAdapter = new WordListenAdapter(this, presenter.getUnitListenWordList(this, bookId, unitId, uid));
//            LinearLayoutManager manager = new LinearLayoutManager(this);
//            binding.recyclerView.setLayoutManager(manager);
//            binding.recyclerView.setAdapter(listenAdapter);
//            binding.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//        } else {
//            listenAdapter.refreshList(presenter.getUnitListenWordList(this, bookId, unitId, uid));
//        }
//        if (listenAdapter.getItemCount()>0){
//            binding.recyclerView.scrollToPosition(0);
//        }
//        //分享和重置
//        binding.reset.setOnClickListener(v -> {
//            presenter.deleteUnitListenWord(this, bookId, unitId, uid);
//            showListenView(0);
//        });
//        binding.share.setOnClickListener(v -> {
//            Share.prepareMessage(this, WordAppData.getInstance(this).getAppNameCn(), "我在" + WordAppData.getInstance(this).getAppNameCn() + "的" + unit + "单词听写中获得了" + rightRateInt + "分，你也来试试吧");
//        });
//    }
//
//    /******听写部分******/
//    private void showListenView(int progress) {
//        binding.listenView.setVisibility(View.VISIBLE);
//        binding.scoreLayout.setVisibility(View.GONE);
//        wordProgress = progress;
//
//        //音频
//        playWordAudio();
//        binding.audio.setOnClickListener(v -> {
//            playWordAudio();
//        });
//        //正确、错误音效
//        //填空
//        binding.edit.setEnabled(true);
//        binding.edit.setText("");
//        binding.edit.setBackgroundResource(R.drawable.shape_round_gray_10);
//        binding.next.setText(getResources().getString(R.string.spell_answer));
////        binding.edit.removeTextChangedListener(textWatcher);
////        binding.edit.addTextChangedListener(textWatcher);
//        //正确、错误样式
//        binding.showLayout.setVisibility(View.INVISIBLE);
//        //序号
//        binding.index.setText((progress + 1) + "/" + wordsList.size());
//        //再试一次/下一个/查看答案
//        binding.again.setOnClickListener(v->{
//            stopPlay();
//
//            binding.again.setVisibility(View.GONE);
//            showListenView(progress);
//        });
//        binding.next.setOnClickListener(v -> {
//            stopPlay();
//
//            String showText = binding.next.getText().toString();
//            if (showText.equals(getResources().getString(R.string.spell_next))) {
//                binding.again.setVisibility(View.GONE);
//                presenter.insertSingleUnitListenWord(this, getListenData(progress));
//                errorMark = 0;
//                showListenView(progress + 1);
//            } else if (showText.equals(getResources().getString(R.string.spell_again))) {
//                showListenView(progress);
//            } else if (showText.equals(getResources().getString(R.string.spell_finish))) {
//                binding.again.setVisibility(View.GONE);
//                presenter.insertSingleUnitListenWord(this, getListenData(progress));
//                errorMark = 0;
//                showScoreView();
//            }else if (showText.equals(getResources().getString(R.string.spell_answer))){
//                binding.edit.setEnabled(false);
//
//                String editWord = binding.edit.getText().toString().trim().toLowerCase().replaceAll(" ","");
//                TalkShowWords words = wordsList.get(wordProgress);
//                String rightWord = words.word.trim().toLowerCase().replaceAll(" ","");
//                binding.showLayout.setVisibility(View.VISIBLE);
//                binding.word.setText(words.word);
//
//                if (errorMark<1){
//                    if (editWord.equals(rightWord)){
//                        binding.edit.setBackgroundResource(R.drawable.shape_round_green_10);
//                        playRingAudio(true);
//                    }else {
//                        binding.again.setVisibility(View.VISIBLE);
//                        binding.edit.setBackgroundResource(R.drawable.shape_round_red_10);
//                        playRingAudio(false);
//                    }
//                }else {
//                    if (editWord.equals(rightWord)){
//                        binding.edit.setBackgroundResource(R.drawable.shape_round_green_10);
//                        playRingAudio(true);
//                    }else {
//                        binding.edit.setBackgroundResource(R.drawable.shape_round_red_10);
//                        playRingAudio(false);
//                    }
//                }
//
//                if (wordProgress==wordsList.size()-1){
//                    binding.next.setText(getResources().getString(R.string.spell_finish));
//                }else {
//                    binding.next.setText(getResources().getString(R.string.spell_next));
//                }
//
//                errorMark++;
//            }
//        });
//    }
//
//    //输入框回调
//    private TextWatcher textWatcher = new TextWatcher() {
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            TalkShowWords words = wordsList.get(wordProgress);
//            String curWord = words.word.trim().toLowerCase().replace(" ","");
//            String spellWord = s.toString().trim().toLowerCase().replace("","");
//
//            if (curWord.length() == spellWord.length()) {
//                binding.edit.setEnabled(false);
//
//                binding.showLayout.setVisibility(View.VISIBLE);
//                binding.word.setText(words.word);
//
//                if (errorMark < 1) {
//                    //第一次
//                    if (spellWord.equals(curWord)) {
//                        if (wordProgress == wordsList.size() - 1) {
//                            binding.next.setText(getResources().getString(R.string.spell_finish));
//                        } else {
//                            binding.next.setText(getResources().getString(R.string.spell_next));
//                        }
//                    } else {
//                        binding.next.setText(getResources().getString(R.string.spell_again));
//                    }
//                } else {
//                    //第二次及以上
//                    if (wordProgress == wordsList.size() - 1) {
//                        binding.next.setText(getResources().getString(R.string.spell_finish));
//                    } else {
//                        binding.next.setText(getResources().getString(R.string.spell_next));
//                    }
//                }
//
//                if (spellWord.equals(curWord)) {
//                    binding.edit.setBackgroundResource(R.drawable.shape_round_green_10);
//                    playRingAudio(true);
//                } else {
//                    errorMark++;
//                    binding.edit.setBackgroundResource(R.drawable.shape_round_red_10);
//                    playRingAudio(false);
//                }
//            } else {
//                binding.edit.setBackgroundResource(R.drawable.shape_round_red_10);
//                binding.next.setText(getResources().getString(R.string.spell_answer));
//            }
//        }
//    };
//
//    //需要插入的单词数据
//    private TalkShowListen getListenData(int progress) {
//        TalkShowListen listen = new TalkShowListen();
//        listen.book_id = bookId;
//        listen.unit_id = unitId;
//        listen.position = progress;
//        listen.uid = WordManager.getInstance().userid;
//
//        TalkShowWords words = wordsList.get(progress);
//        listen.word = words.word;
//        listen.porn = words.pron;
//        listen.def = words.def;
//        listen.audio = words.audio;
//
//        String spellWord = binding.edit.getText().toString().trim().toLowerCase();
//        int status = spellWord.equals(words.word.toLowerCase()) ? 1 : 0;
//        listen.spell = spellWord;
//        listen.status = status;
//        listen.error_count = errorMark;
//        listen.update_time = DateUtil.formatTime(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
//        return listen;
//    }
//
//    /******进度部分******/
//    //显示弹窗
//    private void showProgressDialog(int progress, int total) {
//        binding.listenView.setVisibility(View.GONE);
//        binding.scoreLayout.setVisibility(View.GONE);
//
//        progressDialog = new AlertDialog.Builder(this).create();
//        progressDialog.show();
//        progressDialog.setCancelable(false);
//
//        Window window = progressDialog.getWindow();
//        if (window != null) {
//            window.setContentView(R.layout.dialog_word_progress);
//            window.setGravity(Gravity.CENTER);
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//            WindowManager.LayoutParams lp = window.getAttributes();
//            int width = getResources().getDisplayMetrics().widthPixels * 4 / 5;
//            lp.width = width;
//            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            window.setAttributes(lp);
//
//            TextView titleView = window.findViewById(R.id.title);
//            titleView.setText("温馨提示");
//            TextView msgView = window.findViewById(R.id.msg);
//            msgView.setText(Html.fromHtml("当前单元总单词数为<b>" + total + "</b>个，当前已经听写完成<b>" + progress + "</b>个单词，是否继续听写单词？"));
//            TextView resetView = window.findViewById(R.id.reset);
//            resetView.setText("重新听写");
//            resetView.setOnClickListener(v -> {
//                progressDialog.dismiss();
//                presenter.deleteUnitListenWord(this, bookId, unitId, uid);
//                showListenView(0);
//            });
//            TextView keepView = window.findViewById(R.id.keep);
//            keepView.setText("继续听写");
//            keepView.setOnClickListener(v -> {
//                progressDialog.dismiss();
//                showListenView(progress);
//            });
//        }
//    }
//
//    //播放单词音频
//    private void playWordAudio() {
//        try {
//            TalkShowWords words = wordsList.get(wordProgress);
//            if (TextUtils.isEmpty(words.audio)) {
//                //这里通过接口查询数据
//                presenter.searchWord(words.word);
//                return;
//            }
//
//            if (audioPlayer == null) {
//                audioPlayer = new MediaPlayer();
//            }
//            audioPlayer.reset();
//
//            String folder = StorageUtil.getMediaDir(getApplicationContext(), bookId, unitId).getAbsolutePath();
//            if (StorageUtil.isAudioExist(folder, wordProgress)) {
//                audioPlayer.setDataSource(folder + "/" + wordProgress + ".mp3");
//            } else {
//                if (MediaUtils.isConnected(this)) {
//                    audioPlayer.setDataSource(this, Uri.parse(words.audio));
//                } else {
//                    ToastUtil.showToast(this, "暂时没有这个单词的音频，请打开数据网络播放");
//                    return;
//                }
//            }
//
//            audioPlayer.prepareAsync();
//            audioPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                @Override
//                public boolean onError(MediaPlayer mp, int what, int extra) {
////                    Log.d("错误", "onError: --audio--");
//                    return false;
//                }
//            });
//            audioPlayer.setOnPreparedListener(mp -> {
//                audioPlayer.start();
//
//                //动画效果
//                binding.audio.setBackgroundResource(R.drawable.anim_listen_audio);
//                anim = (AnimationDrawable) binding.audio.getBackground();
//                anim.start();
//            });
//            audioPlayer.setOnCompletionListener(mp -> {
//                if (anim != null) {
//                    anim.stop();
//                    anim = null;
//                }
//                binding.audio.setBackgroundResource(R.drawable.ic_wordtest_audio_big);
//            });
//        } catch (Exception e) {
//
//        }
//    }
//
//    //播放音效音频
//    private void playRingAudio(boolean isSuccess) {
//        int resId = isSuccess ? R.raw.success_short : R.raw.fail_short;
//        ringPlayer = MediaPlayer.create(this,resId);
//        ringPlayer.start();
//
////        ringPlayer = SoundUtil.playSound(this, resId);
//    }
//
//    //停止播放
//    private void stopPlay() {
//        try {
//            if (audioPlayer != null && audioPlayer.isPlaying()) {
//                audioPlayer.stop();
//                audioPlayer.release();
//                audioPlayer = null;
//            }
//            if (ringPlayer != null && ringPlayer.isPlaying()) {
//                ringPlayer.stop();
//                ringPlayer.release();
//                ringPlayer = null;
//            }
////            if (ringPlayer!=null){
////                ringPlayer.release();
////                ringPlayer = null;
////            }
//
//            if (anim != null) {
//                anim.stop();
//            }
//        } catch (Exception e) {
//            audioPlayer = null;
//            ringPlayer = null;
//        }
//    }
//
//    @Override
//    public void showSearchResult(WordEntity entity) {
//        if (entity!=null&&!TextUtils.isEmpty(entity.audio)){
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    TalkShowWords words = wordsList.get(wordProgress);
//                    words.audio = entity.audio;
//
//                    presenter.updateSingleWord(WordSingleListenActivity.this,words);
//                    wordsList = presenter.getUnitWordList(WordSingleListenActivity.this, bookId, unitId);
//
//                    playWordAudio();
//                }
//            }).start();
//        }else {
//            ToastUtil.showToast(this, "此单词暂无音频");
//        }
//    }
//
//    @Override
//    public void showText(String msg) {
//        ToastUtil.showToast(this,msg);
//    }
//}
