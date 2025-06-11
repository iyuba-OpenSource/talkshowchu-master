package com.iyuba.wordtest.ui.listen.singleListen;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
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

import com.iyuba.wordtest.R;
import com.iyuba.wordtest.adapter.WordListenAdapter;
import com.iyuba.wordtest.databinding.FragmentWordSingleListenBinding;
import com.iyuba.wordtest.entity.TalkShowListen;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.entity.WordEntity;
import com.iyuba.wordtest.manager.WordManager;
import com.iyuba.wordtest.ui.listen.WordListenActivity;
import com.iyuba.wordtest.ui.listen.bean.WordListenShowBean;
import com.iyuba.wordtest.utils.DateUtil;
import com.iyuba.wordtest.utils.MediaUtils;
import com.iyuba.wordtest.utils.Share;
import com.iyuba.wordtest.utils.StorageUtil;
import com.iyuba.wordtest.utils.ToastUtil;
import com.iyuba.wordtest.wxapi.WordAppData;

import java.text.NumberFormat;
import java.util.List;

/**
 * @title: 单独拼写界面
 * @date: 2023/11/29 13:54
 * @description: 用于拼写单词测试
 */
public class WordSingleListenFragment extends Fragment implements WordSingleListenMvpView{

    //当前数据
    private int bookId;
    private int unitId;
    private String uid;

    //布局
    private FragmentWordSingleListenBinding binding;

    //数据
    private WordSingleListenPresenter presenter;

    private List<TalkShowWords> wordsList;
    private int errorMark = 0;//错误次数
    private int wordProgress = 0;//单词进度

    //适配器
    private WordListenAdapter listenAdapter;

    //样式
    private AlertDialog progressDialog = null;
    private MediaPlayer audioPlayer;
    private MediaPlayer ringPlayer;
    //    private SoundPool ringPlayer;
    private AnimationDrawable anim;

    public static WordSingleListenFragment getInstance(int bookId, int unitId){
        WordSingleListenFragment fragment = new WordSingleListenFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(WordListenActivity.TAG_BOOKID, bookId);
        bundle.putInt(WordListenActivity.TAG_UNITID, unitId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWordSingleListenBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new WordSingleListenPresenter();
        presenter.attachView(this);

        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        stopPlay();

        if (audioPlayer != null) {
            audioPlayer = null;
        }
        if (ringPlayer != null) {
            ringPlayer = null;
        }
    }

    /*************************初始化************************/
    private void initData() {
        uid = WordManager.getInstance().userid;
        bookId = getArguments().getInt(WordListenActivity.TAG_BOOKID, 0);
        unitId = getArguments().getInt(WordListenActivity.TAG_UNITID, 0);

        //获取进度
        List<TalkShowListen> listenList = presenter.getUnitListenWordList(getActivity(), bookId, unitId, uid);
        wordsList = presenter.getUnitWordList(getActivity(), bookId, unitId);
        if (listenList != null && listenList.size() > 0) {
            //检查是否完成
            if (listenList.size() < wordsList.size()) {
                showProgressDialog(listenList.size(), wordsList.size());
            } else {
                showScoreView();
            }
        } else {
            showListenView(0);
        }
    }

    /*************************数据处理**********************/
    //展示部分
    private void showScoreView() {
        binding.listenView.setVisibility(View.GONE);
        binding.scoreLayout.getRoot().setVisibility(View.VISIBLE);
        //单元信息
        String unit = WordAppData.getInstance(getActivity()).getBookName() + "Unit" + unitId + "单元";
        String unitMsg = "恭喜你完成" + unit + "的单词拼写测试！";
        int index = unitMsg.indexOf(unit);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(unitMsg);
        ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), index, index + unit.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        binding.scoreLayout.unit.setText(ssb);
        //总单词数量
        int totalCount = wordsList.size();
        binding.scoreLayout.totalCount.setText(String.valueOf(totalCount));
        //正确单词数量
        int rightCount = presenter.getUnitRightListenWordList(getActivity(), bookId, unitId, uid).size();
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
            List<WordListenShowBean> showList = presenter.transResultShowData(presenter.getUnitListenWordList(getActivity(), bookId, unitId, uid));
            listenAdapter = new WordListenAdapter(getActivity(), showList);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            binding.scoreLayout.recyclerView.setLayoutManager(manager);
            binding.scoreLayout.recyclerView.setAdapter(listenAdapter);
            binding.scoreLayout.recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        } else {
            List<WordListenShowBean> showList = presenter.transResultShowData(presenter.getUnitListenWordList(getActivity(), bookId, unitId, uid));
            listenAdapter.refreshList(showList);
        }
        if (listenAdapter.getItemCount()>0){
            binding.scoreLayout.recyclerView.scrollToPosition(0);
        }
        //分享和重置
        binding.scoreLayout.reset.setOnClickListener(v -> {
            presenter.deleteUnitListenWord(getActivity(), bookId, unitId, uid);
            showListenView(0);
        });
        binding.scoreLayout.share.setOnClickListener(v -> {
            Share.prepareMessage(getActivity(), WordAppData.getInstance(getActivity()).getAppNameCn(), "我在" + WordAppData.getInstance(getActivity()).getAppNameCn() + "的" + unit + "单词拼写测试中获得了" + rightRateInt + "分，你也来试试吧");
        });
    }

    //拼写部分
    private void showListenView(int progress) {
        binding.listenView.setVisibility(View.VISIBLE);
        binding.scoreLayout.getRoot().setVisibility(View.GONE);
        wordProgress = progress;

        //音频
        playWordAudio();
        binding.audio.setOnClickListener(v -> {
            playWordAudio();
        });
        //正确、错误音效
        //填空
        binding.edit.setEnabled(true);
        binding.edit.setText("");
        binding.edit.setBackgroundResource(R.drawable.shape_round_gray_10);
        binding.next.setText(getResources().getString(R.string.spell_answer));
//        binding.edit.removeTextChangedListener(textWatcher);
//        binding.edit.addTextChangedListener(textWatcher);
        //正确、错误样式
        binding.showLayout.setVisibility(View.INVISIBLE);
        //序号
        binding.index.setText((progress + 1) + "/" + wordsList.size());
        //再试一次/下一个/查看答案
        binding.again.setOnClickListener(v->{
            stopPlay();

            binding.again.setVisibility(View.GONE);
            showListenView(progress);
        });
        binding.next.setOnClickListener(v -> {
            stopPlay();

            String showText = binding.next.getText().toString();
            if (showText.equals(getResources().getString(R.string.spell_next))) {
                binding.again.setVisibility(View.GONE);
                presenter.insertSingleUnitListenWord(getActivity(), getListenData(progress));
                errorMark = 0;
                showListenView(progress + 1);
            } else if (showText.equals(getResources().getString(R.string.spell_again))) {
                showListenView(progress);
            } else if (showText.equals(getResources().getString(R.string.spell_finish))) {
                binding.again.setVisibility(View.GONE);
                presenter.insertSingleUnitListenWord(getActivity(), getListenData(progress));
                errorMark = 0;
                showScoreView();
            }else if (showText.equals(getResources().getString(R.string.spell_answer))){
                binding.edit.setEnabled(false);

                String editWord = binding.edit.getText().toString().trim().toLowerCase().replaceAll(" ","");
                TalkShowWords words = wordsList.get(wordProgress);
                String rightWord = words.word.trim().toLowerCase().replaceAll(" ","");
                binding.showLayout.setVisibility(View.VISIBLE);
                binding.word.setText(words.word);

                if (errorMark<1){
                    if (editWord.equals(rightWord)){
                        binding.edit.setBackgroundResource(R.drawable.shape_round_green_10);
                        playRingAudio(true);
                    }else {
                        binding.again.setVisibility(View.VISIBLE);
                        binding.edit.setBackgroundResource(R.drawable.shape_round_red_10);
                        playRingAudio(false);
                    }
                }else {
                    if (editWord.equals(rightWord)){
                        binding.edit.setBackgroundResource(R.drawable.shape_round_green_10);
                        playRingAudio(true);
                    }else {
                        binding.edit.setBackgroundResource(R.drawable.shape_round_red_10);
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
    }

    //输入框回调
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            TalkShowWords words = wordsList.get(wordProgress);
            String curWord = words.word.trim().toLowerCase().replace(" ","");
            String spellWord = s.toString().trim().toLowerCase().replace("","");

            if (curWord.length() == spellWord.length()) {
                binding.edit.setEnabled(false);

                binding.showLayout.setVisibility(View.VISIBLE);
                binding.word.setText(words.word);

                if (errorMark < 1) {
                    //第一次
                    if (spellWord.equals(curWord)) {
                        if (wordProgress == wordsList.size() - 1) {
                            binding.next.setText(getResources().getString(R.string.spell_finish));
                        } else {
                            binding.next.setText(getResources().getString(R.string.spell_next));
                        }
                    } else {
                        binding.next.setText(getResources().getString(R.string.spell_again));
                    }
                } else {
                    //第二次及以上
                    if (wordProgress == wordsList.size() - 1) {
                        binding.next.setText(getResources().getString(R.string.spell_finish));
                    } else {
                        binding.next.setText(getResources().getString(R.string.spell_next));
                    }
                }

                if (spellWord.equals(curWord)) {
                    binding.edit.setBackgroundResource(R.drawable.shape_round_green_10);
                    playRingAudio(true);
                } else {
                    errorMark++;
                    binding.edit.setBackgroundResource(R.drawable.shape_round_red_10);
                    playRingAudio(false);
                }
            } else {
                binding.edit.setBackgroundResource(R.drawable.shape_round_red_10);
                binding.next.setText(getResources().getString(R.string.spell_answer));
            }
        }
    };

    //需要插入的单词数据
    private TalkShowListen getListenData(int progress) {
        TalkShowListen listen = new TalkShowListen();
        listen.book_id = bookId;
        listen.unit_id = unitId;
        listen.position = progress;
        listen.uid = WordManager.getInstance().userid;

        TalkShowWords words = wordsList.get(progress);
        listen.word = words.word;
        listen.porn = words.pron;
        listen.def = words.def;
        listen.audio = words.audio;

        String spellWord = binding.edit.getText().toString().trim().toLowerCase();
        int status = spellWord.equals(words.word.toLowerCase()) ? 1 : 0;
        listen.spell = spellWord;
        listen.status = status;
        listen.error_count = errorMark;
        listen.update_time = DateUtil.formatTime(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
        return listen;
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
            msgView.setText(Html.fromHtml("当前单元总单词数为<b>" + total + "</b>个，当前已经拼写完成<b>" + progress + "</b>个单词，是否继续拼写单词？"));
            TextView resetView = window.findViewById(R.id.reset);
            resetView.setText("重新拼写");
            resetView.setOnClickListener(v -> {
                progressDialog.dismiss();
                presenter.deleteUnitListenWord(getActivity(), bookId, unitId, uid);
                showListenView(0);
            });
            TextView keepView = window.findViewById(R.id.keep);
            keepView.setText("继续拼写");
            keepView.setOnClickListener(v -> {
                progressDialog.dismiss();
                showListenView(progress);
            });
        }
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
//                    Log.d("错误", "onError: --audio--");
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

//        ringPlayer = SoundUtil.playSound(this, resId);
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
//            if (ringPlayer!=null){
//                ringPlayer.release();
//                ringPlayer = null;
//            }

            if (anim != null) {
                anim.stop();
            }
        } catch (Exception e) {
            audioPlayer = null;
            ringPlayer = null;
        }
    }

    /***********************数据回调*********************/
    @Override
    public void showSearchResult(WordEntity entity) {
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

    @Override
    public void showText(String msg) {
        ToastUtil.showToast(getActivity(),msg);
    }
}
