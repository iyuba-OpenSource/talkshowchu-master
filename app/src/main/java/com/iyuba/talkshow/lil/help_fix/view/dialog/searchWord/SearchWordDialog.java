package com.iyuba.talkshow.lil.help_fix.view.dialog.searchWord;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.DialogWordSearchBinding;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Word_detail;
import com.iyuba.talkshow.lil.help_fix.util.ScreenUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.wordtest.db.WordOp;
import com.iyuba.wordtest.entity.WordEntity;

/**
 * @title: 单词查询的弹窗
 * @date: 2023/6/13 10:38
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class SearchWordDialog extends AlertDialog{

    private Context context;
    private String word;

    private DialogWordSearchBinding binding;
    //收藏单词的数据库
    private WordOp wordOp;
    //播放器
    private ExoPlayer exoPlayer;
    //数据
    private SearchWordPresenter presenter;
    //查询出的单词数据
    private Word_detail detail;

    public SearchWordDialog(@NonNull Context context, String word) {
        super(context);
        this.context = context;
        this.word = word;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding = DialogWordSearchBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());

        //初始化数据
        wordOp = new WordOp(context);
        presenter = new SearchWordPresenter();
        //设置取消
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismiss();
            }
        });
        //显示样式
        showSearchLoading();
    }

    @Override
    public void show() {
        super.show();

        int width = ScreenUtil.getScreenW(context);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (width*0.8);
        getWindow().setAttributes(lp);
    }

    @Override
    public void dismiss() {
        super.dismiss();

        if (presenter!=null){
            presenter.close();
        }
    }

    @Override
    public void cancel() {
        super.cancel();

        if (presenter!=null){
            presenter.close();
        }
    }

    /********************视频播放和暂停********************/
    //播放音频
    private void startPlay(){
        if (TextUtils.isEmpty(detail.audio)){
            ToastUtil.showToast(context,"单词音频不存在~");
            return;
        }

        MediaItem mediaItem = MediaItem.fromUri(detail.audio);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
    }

    //暂停音频
    private void pausePlay(){
        if (exoPlayer!=null&&exoPlayer.isPlaying()){
            exoPlayer.stop();
        }
    }

    /********************界面显示******************/
    //显示单词查询界面
    private void showSearchLoading(){
        binding.loadingLayout.setVisibility(View.VISIBLE);
        binding.dataLayout.setVisibility(View.GONE);

        binding.progress.setVisibility(View.VISIBLE);
        binding.msg.setText("正在查询单词～");
        binding.title.setText("单词查询");
        binding.collect.setVisibility(View.INVISIBLE);

        presenter.searchWord(word, new SearchWordPresenter.OnSearchWordListener() {
            @Override
            public void onSearch(Word_detail detail) {
                if (detail!=null){
                    showWordDetail(detail);
                }else {
                    binding.progress.setVisibility(View.INVISIBLE);
                    binding.msg.setText("查询单词失败~");
                }
            }
        });
    }

    //显示单词内容界面
    private void showWordDetail(Word_detail detail){
        if (TextUtils.isEmpty(detail.key)){
            dismiss();
            ToastUtil.showToast(context,"未查询到该单词的内容");
            return;
        }

        this.detail = detail;
        binding.loadingLayout.setVisibility(View.GONE);
        binding.dataLayout.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(detail.key)){
            binding.word.setText(detail.key);

            //查询单词是否存在
            binding.collect.setVisibility(View.VISIBLE);
            if (isWordCollect(detail.key)){
                binding.collect.setImageResource(R.drawable.ic_word_collected);
            }else {
                binding.collect.setImageResource(R.drawable.ic_word_nocollect);
            }
        }else {
            binding.collect.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(detail.pron)){
            binding.pron.setText("["+detail.pron+"]");
        }

        if (!TextUtils.isEmpty(detail.def)){
            binding.def.setText(detail.def);
        }

        if (TextUtils.isEmpty(detail.audio)){
            binding.play.setVisibility(View.INVISIBLE);
        }else {
            binding.play.setVisibility(View.VISIBLE);
        }

        exoPlayer = new ExoPlayer.Builder(context).build();
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        //准备
                        exoPlayer.play();
                        break;
                    case Player.STATE_ENDED:
                        //结束
                        exoPlayer.stop();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(context,"单词音频播放异常("+error.errorCode+")");
            }
        });

        binding.collect.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                NewLoginUtil.startToLogin(context);
                return;
            }

            collectWord();
        });
        binding.close.setOnClickListener(v->{
            pausePlay();
            dismiss();
        });
        binding.play.setOnClickListener(v->{
            if (exoPlayer==null){
                ToastUtil.showToast(context,"未初始化播放器");
                return;
            }

            if (exoPlayer.isPlaying()){
                pausePlay();
            }else {
                startPlay();
            }
        });
    }

    /*********************************辅助功能***********************/
    //查询单词是否存在收藏中
    private boolean isWordCollect(String word){
        return wordOp.isExsitsWord(word, UserInfoManager.getInstance().getUserId());
    }

    //收藏单词
    private void collectWord(){
        presenter.collectWord(detail.key, !isWordCollect(detail.key), new SearchWordPresenter.OnCollectWordListener() {
            @Override
            public void onCollect(boolean isCollect, boolean isSuccess) {
                String msg = "取消收藏单词";
                if (isCollect){
                    msg = "收藏单词";
                }

                if (!isSuccess){
                    ToastUtil.showToast(context,msg+"失败");
                    return;
                }

                if (isCollect){
//                    WordCollectEntity entity = new WordCollectEntity();
//                    entity.userId = UserInfoManager.getInstance().getUserId();
//                    entity.word = detail.key;
//                    entity.porn = detail.pron;
//                    entity.def = detail.def;
//                    entity.audioUrl = detail.audio;
//                    entity.updateTime = System.currentTimeMillis();
//                    CommonDataManager.saveCollectWordToDB(entity);
                    WordEntity wordEntity = new WordEntity();
                    wordEntity.key = detail.key;
                    wordEntity.voa = 0;
                    wordEntity.unit = 0;
                    wordEntity.book = 0;
                    wordEntity.pron = detail.pron;
                    wordEntity.def = detail.def;
                    wordEntity.audio = detail.audio;
                    wordOp.insertWord(wordEntity,UserInfoManager.getInstance().getUserId());

                    binding.collect.setImageResource(R.drawable.ic_word_collected);
                }else {
//                    CommonDataManager.deleteCollectWordFromDB(UserInfoManager.getInstance().getUserId(),detail.key);
                    wordOp.deleteWord(detail.key,UserInfoManager.getInstance().getUserId());

                    binding.collect.setImageResource(R.drawable.ic_word_nocollect);
                }
                ToastUtil.showToast(context,msg+"成功");
            }
        });
    }
}
