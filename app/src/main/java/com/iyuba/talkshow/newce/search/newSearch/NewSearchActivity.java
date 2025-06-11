package com.iyuba.talkshow.newce.search.newSearch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.databinding.ActivitySearchBinding;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Word_detail;
import com.iyuba.talkshow.newce.search.adapter.SearchVoaAdapter;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.wordtest.utils.LibRxTimer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * 搜索界面
 *
 * 搜索本地单词、例句、课文等
 */
public class NewSearchActivity extends BaseActivity implements NewSearchMvpView {

    @Inject
    NewSearchPresenter presenter;

    //句子
    private NewSearchSentenceAdapter sentenceAdapter;
    //文章
    private SearchVoaAdapter voaAdapter;

    //单词数据
    private Word_detail wordData;
    //搜索的关键词
    private String searchKeyWord;
    //播放器
    private ExoPlayer exoPlayer;

    //音频播放链接
    private String playUrl = "";
    //音频播放的起始位置
    private long  audioStartTime = 0;
    //原音播放的定时器
    private static final String timer_playAudio = "timer_playAudio";
    //评测播放的定时器
    private static final String timer_playEval = "timer_playEval";

    //布局样式
    private ActivitySearchBinding binding;

    //跳转
    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context, NewSearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        activityComponent().inject(this);
        presenter.attachView(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initPlayer();
        initClick();
        initData();
    }

    private void initClick(){
        binding.backToolbar.setOnClickListener(v->{
            finish();
        });
        binding.deleteToolbar.setOnClickListener(v->{
            binding.inputToolbar.setText("");
            updateUI(false,"请输入单词查询");
        });
        binding.inputToolbar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    //空格直接提示
                    searchKeyWord = binding.inputToolbar.getText().toString().trim();
                    if (TextUtils.isEmpty(searchKeyWord)){
                        showToastShort("请输入需要查询的内容");
                        return true;
                    }


                    updateUI(true,null);
                    //查询单词
                    presenter.searchWordByNet(searchKeyWord);
                    //查询句子
                    presenter.searchSentence(searchKeyWord);
                    //查询文章
                    presenter.searchVoa(searchKeyWord);
                    return true;
                }
                return false;
            }
        });
        binding.audio.setOnClickListener(v->{
            if (sentenceAdapter!=null){
                sentenceAdapter.stopAudio();
            }

            if (!wordData.audio.equals(playUrl)){
                stopPlay();
            }

            playUrl = wordData.audio;

            if (TextUtils.isEmpty(playUrl)){
                ToastUtil.showToast(this,"未查询到单词音频");
                return;
            }

            audioStartTime = 0;
            startPlay(playUrl);
        });
    }

    private void initData(){
        updateUI(false,"请输入单词查询");

        //句子列表
        sentenceAdapter = new NewSearchSentenceAdapter(this,new ArrayList<>(),presenter);
        binding.sentenceShow.setLayoutManager(new LinearLayoutManager(this));
        binding.sentenceShow.setAdapter(sentenceAdapter);
        binding.sentenceShow.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        binding.sentenceMore.setOnClickListener(v->{
//            SearchListActivity.start(this,SearchListActivity.TAG_SENTENCE,searchKeyWord);
        });
        /*sentenceAdapter.setOnSentenceListener(new NewSearchSentenceAdapter.OnSentenceListener() {
            @Override
            public void onSelectItem(int position) {
                stopPlay();
                sentenceAdapter.refreshAudioPlay(0,0,true);
                sentenceAdapter.refreshEvalPlay(0,0,true);
            }

            @Override
            public void onPlayAudio(String audioUrl, long startTime, long endTime) {
                //先停止刷新上一个的样式
                sentenceAdapter.refreshEvalPlay(0,0,true);
                if (!playUrl.equals(audioUrl)){
                    stopPlay();
                }

                //设置开始时间
                audioStartTime = startTime;
                playUrl = audioUrl;

                if (exoPlayer!=null){
                    if (exoPlayer.isPlaying()){
                        stopPlay();
                        LibRxTimer.getInstance().cancelTimer(timer_playAudio);
                        sentenceAdapter.refreshAudioPlay(0,0,true);
                    }else {
                        startPlay(audioUrl);
                        LibRxTimer.getInstance().multiTimerInMain(timer_playAudio, 0, 100L, new LibRxTimer.RxActionListener() {
                            @Override
                            public void onAction(long number) {
                                //计算当前进度和结束进度
                                long totalTime = endTime - startTime;
                                long progressTime = exoPlayer.getCurrentPosition() - startTime;
                                //刷新显示
                                sentenceAdapter.refreshAudioPlay(progressTime,totalTime,false);

                                if (exoPlayer.getCurrentPosition()>=endTime){
                                    stopPlay();
                                    LibRxTimer.getInstance().cancelTimer(timer_playAudio);
                                    sentenceAdapter.refreshAudioPlay(0,0,true);
                                }
                            }
                        });
                    }
                }else {
                    ToastUtil.showToast(NewSearchActivity.this,"播放器未进行初始化");
                }
            }

            @Override
            public void onRecord() {
                stopPlay();
                sentenceAdapter.refreshAudioPlay(0,0,true);
                sentenceAdapter.refreshEvalPlay(0,0,true);
            }

            @Override
            public void onPlayEval(String audioUrl) {
                //先停止刷新上一个的样式
                sentenceAdapter.refreshAudioPlay(0,0,true);
                if (!playUrl.equals(audioUrl)){
                    stopPlay();
                }

                //设置开始播放时间
                audioStartTime = 0;
                playUrl = audioUrl;

                if (exoPlayer!=null){
                    if (exoPlayer.isPlaying()){
                        stopPlay();
                        sentenceAdapter.refreshEvalPlay(0,0,true);
                        LibRxTimer.getInstance().cancelTimer(timer_playEval);
                    }else {
                        startPlay(audioUrl);
                        LibRxTimer.getInstance().multiTimerInMain(timer_playEval, 0, 100L, new LibRxTimer.RxActionListener() {
                            @Override
                            public void onAction(long number) {
                                //计算当前进度和结束进度
                                long totalTime = exoPlayer.getDuration();
                                long progressTime = exoPlayer.getCurrentPosition();
                                //刷新显示
                                sentenceAdapter.refreshAudioPlay(progressTime,totalTime,false);
                            }
                        });
                    }
                }else {
                    ToastUtil.showToast(NewSearchActivity.this,"播放器未进行初始化");
                }
            }
        });*/
        sentenceAdapter.setOnSentenceListener(new NewSearchSentenceAdapter.OnSentenceListener() {
            @Override
            public void onStopPlay() {
                stopPlay();
            }
        });

        //文章列表
        voaAdapter = new SearchVoaAdapter(this,new ArrayList<>());
        binding.voaShow.setLayoutManager(new LinearLayoutManager(this));
        binding.voaShow.setAdapter(voaAdapter);
        binding.voaShow.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        binding.voaMore.setOnClickListener(v->{
//            SearchListActivity.start(this,SearchListActivity.TAG_VOA,searchKeyWord);
        });
        voaAdapter.setOnVoaListener(new SearchVoaAdapter.OnVoaListener() {
            @Override
            public void onStopPlay() {
                stopPlay();
            }
        });
    }

    @Override
    public void showWord(String msg,Word_detail detail) {
        if (detail==null){
            updateUI(false,msg);
            return;
        }

        //设置单词数据
        wordData = detail;

        updateUI(false,null);

        binding.wordLayoutNew.setVisibility(View.VISIBLE);
        binding.word.setText(detail.key);
        if (TextUtils.isEmpty(detail.pron)){
            binding.pron.setText("");
        }else {
            binding.pron.setText("["+detail.pron+"]");
        }
        binding.def.setText(detail.def);

        //预先设置
        if (TextUtils.isEmpty(detail.audio)){
            binding.audio.setVisibility(View.GONE);
        }else {
            binding.audio.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showSentence(List<VoaText> evalList) {
        if (evalList!=null&&evalList.size()>0){
            binding.sentenceLayout.setVisibility(View.VISIBLE);

            //这里直接下载所有的音频
//            preDownloadAudio(evalList,false);

            //这里只显示前三个
            List<VoaText> showList = new ArrayList<>();
            if (evalList.size()>3){
                binding.sentenceMore.setVisibility(View.VISIBLE);

                showList.add(evalList.get(0));
                showList.add(evalList.get(1));
                showList.add(evalList.get(2));
            }else {
                binding.sentenceMore.setVisibility(View.GONE);

                showList.addAll(evalList);
            }

            sentenceAdapter.refreshData(showList);
            binding.sentenceShow.setItemViewCacheSize(showList.size());
        }else {
            binding.sentenceLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void showVoa(List<Voa> voaList) {
        if (voaList!=null&&voaList.size()>0){
            binding.voaLayout.setVisibility(View.VISIBLE);

            //这里只显示前三个
            List<Voa> showList = new ArrayList<>();
            if (voaList.size()>3){
                binding.voaMore.setVisibility(View.VISIBLE);

                showList.add(voaList.get(0));
                showList.add(voaList.get(1));
                showList.add(voaList.get(2));
            }else {
                binding.voaMore.setVisibility(View.GONE);

                showList.addAll(voaList);
            }

            voaAdapter.refreshData(showList);
            binding.voaShow.setItemViewCacheSize(showList.size());
        }else {
            binding.voaLayout.setVisibility(View.GONE);
        }
    }

    //显示查询进度
    private void updateUI(boolean isLoading,String msg){
        if (isLoading){
            binding.loadingLayout.setVisibility(View.VISIBLE);
            binding.inputToolbar.setEnabled(false);
            binding.msg.setText("正在查询单词数据～");
        }else {
            if (!TextUtils.isEmpty(msg)){
                binding.loadingLayout.setVisibility(View.VISIBLE);
                binding.proLoading.setVisibility(View.GONE);
                binding.msg.setText(msg);
                binding.inputToolbar.setEnabled(true);
            }else {
                binding.inputToolbar.setEnabled(true);
                binding.loadingLayout.setVisibility(View.GONE);
            }
        }
    }

    //预下载音频文件
    private void preDownloadAudio(List<VoaText> list, boolean isMustUpdate) {
        //检查音频数据
        Map<String,String> audioMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            VoaText bean = list.get(i);

            //查询对应的voa数据
            Voa curVoa = presenter.getVoa(bean.getVoaId());
            if (curVoa!=null&&!TextUtils.isEmpty(curVoa.sound())) {
                String audioUrl = Constant.getSoundWavUrl(curVoa.sound(),curVoa.voaId());

                //先判断是否存在，然后进行保存
                if (audioMap.get(audioUrl)==null){
                    audioMap.put(audioUrl,audioUrl);
                }
            }
        }

        //音频存在则下载
        if (audioMap.keySet().size()>0){
            //下载地址
            List<String> audioList = new ArrayList<>();
            for (String url:audioMap.keySet()){
                audioList.add(url);
            }

            //下载
            presenter.downloadCount = 0;
            presenter.recycleDownloadAudio(audioList,isMustUpdate);
        }
    }

    //初始化播放器
    private void initPlayer(){
        exoPlayer = new ExoPlayer.Builder(this).build();
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        startPlay(null);
                        return;
                    case Player.STATE_ENDED:
                        //停止播放
                        /*if (sentenceAdapter!=null){
                            sentenceAdapter.refreshAudioPlay(0,0,true);
                            sentenceAdapter.refreshEvalPlay(0,0,true);
                        }*/
                        return;
                }
            }



            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(NewSearchActivity.this,"播放器初始化失败");
            }
        });
    }

    //播放音频
    private void startPlay(String url){
        if (!TextUtils.isEmpty(url)){
            MediaItem mediaItem = MediaItem.fromUri(url);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
        }else {
            if (audioStartTime>0){
                exoPlayer.seekTo(audioStartTime);
            }
            exoPlayer.play();
        }
    }

    //停止播放
    private void stopPlay(){
        if (exoPlayer!=null&&exoPlayer.isPlaying()){
            exoPlayer.pause();
        }

        LibRxTimer.getInstance().cancelTimer(timer_playAudio);
        LibRxTimer.getInstance().cancelTimer(timer_playEval);
    }
}
