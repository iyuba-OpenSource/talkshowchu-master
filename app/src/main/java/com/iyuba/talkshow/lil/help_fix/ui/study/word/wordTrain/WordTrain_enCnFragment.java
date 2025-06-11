package com.iyuba.talkshow.lil.help_fix.ui.study.word.wordTrain;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.sdk.other.NetworkUtil;
import com.iyuba.talkshow.databinding.FragmentWordTrainEncnBinding;
import com.iyuba.talkshow.lil.help_fix.data.bean.WordBean;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.data.listener.OnSimpleClickListener;
import com.iyuba.talkshow.lil.help_fix.view.dialog.MultiButtonDialog;
import com.iyuba.talkshow.lil.help_fix.view.dialog.SingleButtonDialog;
import com.iyuba.talkshow.lil.help_mvp.base.BaseViewBindingFragment;
import com.iyuba.talkshow.lil.help_mvp.util.StackUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 汉英训练或者英汉训练
 * @date: 2023/8/15 16:53
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description: 根据标识符显示
 */
public class WordTrain_enCnFragment extends BaseViewBindingFragment<FragmentWordTrainEncnBinding> implements WordTrainView {

    private String showType;
    private String types;
    private String bookId;
    private int unitId;
    private int voaId;

    //音频是否加载完成
    private boolean isCanPlay = false;

    //当前单词的位置
    private int selectIndex = 0;
    //当前数据
    private Pair<WordBean,List<WordBean>> curBean;
    //当前需要练习的数据
    private List<Pair<WordBean,List<WordBean>>> pairList;

    private WordTrainAdapter trainAdapter;
    private WordTrainPresenter presenter;

    //播放器
    private ExoPlayer exoPlayer;
    //震动
    private Vibrator vibrator;
    //结果弹窗
    private SingleButtonDialog resultDialog;
    //进度弹窗
    private MultiButtonDialog progressDialog;

    //保存结果，用于显示（暂时保存，当前未用到）
    private List<Pair<WordBean, WordBean>> resultList = new ArrayList<>();
    //正确的单词数量
    private int rightCount = 0;
    //已经完成进度的单词数量
    private int progressCount = 0;

    /**
     *
     * @param showType 展示的类型-英汉、汉英
     * @param types 数据类型-中小学、新概念全四册、新概念青少版
     * @param bookId 书籍id
     * @param unitId unitId
     * @param voaId voaId
     * @return
     */
    public static WordTrain_enCnFragment getInstance(String showType,String types, String bookId, int unitId,int voaId){
        WordTrain_enCnFragment fragment = new WordTrain_enCnFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.showType,showType);
        bundle.putString(StrLibrary.types,types);
        bundle.putString(StrLibrary.bookId,bookId);
        bundle.putInt(StrLibrary.id,unitId);
        bundle.putInt(StrLibrary.voaId,voaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showType = getArguments().getString(StrLibrary.showType);
        types = getArguments().getString(StrLibrary.types);
        bookId = getArguments().getString(StrLibrary.bookId);
        unitId = getArguments().getInt(StrLibrary.id);
        voaId = getArguments().getInt(StrLibrary.voaId);

        presenter = new WordTrainPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        initPlayer();
        initClick();

        updateData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        pausePlay();
        closeResultDialog();
        closeProgressDialog();
    }

    /***********************初始化*********************/
    private void initList(){
        binding.toolbar.getRoot().setVisibility(View.GONE);
        pairList = presenter.getRandomWordShowData(types,bookId,unitId,voaId);

        trainAdapter = new WordTrainAdapter(getActivity(),new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(trainAdapter);
        trainAdapter.setListener(new OnSimpleClickListener<Pair<Integer, WordBean>>() {
            @Override
            public void onClick(Pair<Integer, WordBean> pair) {
                //设置显示
                trainAdapter.refreshAnswer(pair.first,curBean.first.getWord());
                //判断当前是否正确
                if (pair.second.getWord().equals(curBean.first.getWord())){
                    //一致
                    startPlay();
                    //增加正确数量
                    rightCount++;
                }else {
                    //不一致
                    showVibrate();
                    //这里要求加上播放音频，感觉怪怪的，不知道用户能否分辨出正确读音还是错误读音
                    startPlay();
                }
                //写入进度
                progressCount++;
                //保存结果
                resultList.add(new Pair<>(curBean.first, pair.second));
                //显示下一个
                binding.next.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initPlayer(){
        exoPlayer = new ExoPlayer.Builder(getActivity()).build();
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        //加载完成
                        isCanPlay = true;
                        break;
                    case Player.STATE_ENDED:
                        //播放完成
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                isCanPlay = false;
                ToastUtil.showToast(getActivity(),"当前单词音频加载错误，请点击单词再次播放");
            }
        });
    }

    private void initClick(){
        binding.next.setOnClickListener(v->{
            pausePlay();
            stopVibrate();

            String showText = binding.next.getText().toString();
            if (showText.equals("查看结果")){
                showResultDialog();
            }else {
                selectIndex++;
                updateData();

                if (selectIndex>=pairList.size()-1){
                    binding.next.setText("查看结果");
                }
            }
        });
    }

    /*************************刷新显示***************************/
    private void updateData(){
        curBean = pairList.get(selectIndex);

        if (showType.equals(TypeLibrary.WordTrainType.Train_enToCn)){
            binding.word.setText(curBean.first.getWord());
            trainAdapter.refreshData(curBean.second, TypeLibrary.TextShowType.CN);
        }else if (showType.equals(TypeLibrary.WordTrainType.Train_cnToEn)){
            binding.word.setText(curBean.first.getDef());
            trainAdapter.refreshData(curBean.second, TypeLibrary.TextShowType.EN);
        }
        binding.next.setVisibility(View.INVISIBLE);

        binding.progress.setMax(pairList.size());
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            binding.progress.setProgress(selectIndex+1,true);
        }else {
            binding.progress.setProgress(selectIndex+1);
        }
        binding.progressText.setText((selectIndex+1)+"/"+pairList.size());

        isCanPlay = false;
        Log.d("单词训练000", curBean.first.getWord()+"--"+curBean.first.getWordAudioUrl());
        preparePlay(curBean.first.getWordAudioUrl());
    }

    /*********************辅助功能**********************/
    //加载音频
    private void preparePlay(String audioUrl){
        Log.d("单词训练", audioUrl==null?"无链接":audioUrl.toString());
        if (!NetworkUtil.isConnected(getActivity())){
            ToastUtil.showToast(getActivity(),"请链接网络后重试");
            return;
        }

        if (exoPlayer!=null&&!TextUtils.isEmpty(audioUrl)){
            exoPlayer.setPlayWhenReady(false);
            MediaItem mediaItem = MediaItem.fromUri(audioUrl);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
        }
    }

    //播放音频
    private void startPlay(){
        if (exoPlayer!=null&&isCanPlay){
            exoPlayer.play();
        }
    }

    //暂停播放
    private void pausePlay(){
        if (exoPlayer!=null&&exoPlayer.isPlaying()){
            exoPlayer.pause();
        }
    }

    //显示震动
    private void showVibrate(){
        stopVibrate();
        if (vibrator==null){
            vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        }
        vibrator.vibrate(300L);
    }

    //停止震动
    private void stopVibrate(){
        if (vibrator!=null){
            vibrator.cancel();
        }
    }

    //显示结果弹窗
    private void showResultDialog(){
        String msg = "正确数量："+rightCount+"\n正确率："+(rightCount*100/pairList.size())+"%"+"\n总数量："+pairList.size();

        if (resultDialog==null){
            resultDialog = new SingleButtonDialog(getActivity());
            resultDialog.create();
        }
        resultDialog.setTitle("训练结果");
        resultDialog.setMsg(msg);
        resultDialog.setButton("确定", new SingleButtonDialog.OnSingleClickListener() {
            @Override
            public void onClick() {
                StackUtil.getInstance().finishCur();
            }
        });
        resultDialog.show();
    }

    //关闭结果弹窗
    private void closeResultDialog(){
        if (resultDialog!=null){
            resultDialog.dismiss();
        }
    }

    //显示进度弹窗
    private void showProgressDialog(){
        if (progressDialog==null){
            progressDialog = new MultiButtonDialog(getActivity());
            progressDialog.create();
        }
        String msg = "当前已完成"+progressCount+"个单词，还有"+(pairList.size()-progressCount)+"个单词需要训练，是否退出当前训练？";
        progressDialog.setTitle("训练进度");
        progressDialog.setMsg(msg);
        progressDialog.setButton("继续训练", "立即退出", new MultiButtonDialog.OnMultiClickListener() {
            @Override
            public void onAgree() {
                StackUtil.getInstance().finishCur();
            }

            @Override
            public void onDisagree() {

            }
        });
        progressDialog.show();
    }

    //关闭进度弹窗
    private void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    //退出提示
    public boolean showExistDialog(){
        if (progressCount!=0&&progressCount<pairList.size()){
            showProgressDialog();
            return true;
        }

        closeResultDialog();
        return false;
    }
}
