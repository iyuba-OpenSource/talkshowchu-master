package com.iyuba.talkshow.lil.help_fix.ui.study.rank.rank_detail;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.sdk.other.NetworkUtil;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.FragmentFixRankDetailBinding;
import com.iyuba.talkshow.lil.help_fix.data.bean.ChapterDetailBean;
import com.iyuba.talkshow.lil.help_fix.data.bean.EvalRankDetailBean;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.util.FixUtil;
import com.iyuba.talkshow.lil.help_fix.util.ShareUtil;
import com.iyuba.talkshow.lil.help_fix.view.dialog.LoadingDialog;
import com.iyuba.talkshow.lil.help_mvp.base.BaseViewBindingFragment;
import com.iyuba.talkshow.lil.help_mvp.util.StackUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @title: 排行榜详情界面
 * @date: 2023/5/25 13:57
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class RankDetailFragment extends BaseViewBindingFragment<FragmentFixRankDetailBinding> implements RankDetailView{

    //类型
    private String types;
    //voaId
    private String voaId;
    //用户id
    private String userId;
    //用户名称
    private String userName;
    //用户头像
    private String userPicUrl;

    private RankDetailPresenter presenter;
    private RankDetailAdapter detailAdapter;

    //播放器
    private ExoPlayer audioPlayer;
    //加载弹窗
    private LoadingDialog loadingDialog;

    public static RankDetailFragment getInstance(String types, String voaId,String userId,String userName,String userPicUrl){
        RankDetailFragment fragment = new RankDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.types, types);
        bundle.putString(StrLibrary.voaid, voaId);
        bundle.putString(StrLibrary.userId,userId);
        bundle.putString(StrLibrary.username,userName);
        bundle.putString(StrLibrary.userPic,userPicUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        types = getArguments().getString(StrLibrary.types);
        voaId = getArguments().getString(StrLibrary.voaid);
        userId = getArguments().getString(StrLibrary.userId);
        userName = getArguments().getString(StrLibrary.username);
        userPicUrl = getArguments().getString(StrLibrary.userPic);

        presenter = new RankDetailPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();
        initList();
        initPlayer();

        binding.refreshLayout.autoRefresh();
    }

    @Override
    public void onPause() {
        super.onPause();

        pausePlay();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.detachView();
    }

    private void initToolbar(){
        binding.buttonBack.setVisibility(View.VISIBLE);
        binding.buttonBack.setImageResource(R.mipmap.img_back);
        binding.buttonBack.setOnClickListener(v->{
            StackUtil.getInstance().finishCur();
        });
        binding.title.setText(userName+" 的评测数据");
    }

    private void initList(){
        binding.refreshLayout.setEnableRefresh(true);
        binding.refreshLayout.setEnableLoadMore(false);
        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        binding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(getActivity())){
                    ToastUtil.showToast(getActivity(),"请链接网络后重试～");
                    return;
                }

                presenter.getRankDetailData(types,voaId,userId);
            }
        });

        detailAdapter = new RankDetailAdapter(getActivity(),new ArrayList<>());
        detailAdapter.refreshUserNameAndPic(userName,userPicUrl,userId);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(detailAdapter);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        detailAdapter.setOnRankDetailCallBackListener(new RankDetailAdapter.OnRankDetailCallBackListener() {
            @Override
            public void playAudio(boolean isSame, EvalRankDetailBean detailBean) {
                if (!NetworkUtil.isConnected(getActivity())){
                    ToastUtil.showToast(getActivity(),"请链接网络后重试~");
                    return;
                }

                String playUrl = fixMargeAudioUrl(detailBean.getShuoShuo());

                if (isSame){
                    if (audioPlayer!=null&&audioPlayer.isPlaying()){
                        pausePlay();
                    }else {
                        startPlay(playUrl);
                    }
                }else {
                    startPlay(playUrl);
                }
            }

            @Override
            public void onAgree(EvalRankDetailBean detailBean) {
                if (!NetworkUtil.isConnected(getActivity())){
                    ToastUtil.showToast(getActivity(),"请链接网络后重试~");
                    return;
                }

                if (!UserInfoManager.getInstance().isLogin()){
                    ToastUtil.showToast(getActivity(),"请登录后进行点赞");
                    NewLoginUtil.startToLogin(getActivity());
                    return;
                }

                if (presenter.isAgreeEvalSentence(userId,types,voaId,String.valueOf(detailBean.getId()))){
                    ToastUtil.showToast(getActivity(),"您已经点赞过了");
                    return;
                }

                //进行点赞操作
                startLoading("正在点赞评测的句子");
                presenter.agreeEvalData(String.valueOf(UserInfoManager.getInstance().getUserId()), userId,types,voaId,String.valueOf(detailBean.getId()));
            }

            @Override
            public void onShare(EvalRankDetailBean detailBean) {
                //分享内容
                String content = getResources().getString(R.string.app_name);
                String title = userName+"在"+getResources().getString(R.string.app_name)+"的评测中获得"+detailBean.getScore()+"分";

                ShareUtil.getInstance().shareEval(getActivity(),types,voaId,String.valueOf(detailBean.getId()),detailBean.getShuoShuo(),UserInfoManager.getInstance().getUserId(),title,content);
            }
        });
    }

    private void initPlayer(){
        audioPlayer = new ExoPlayer.Builder(getActivity()).build();
        audioPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        //准备
                        audioPlayer.play();
                        detailAdapter.refreshPlayAnim(true);
                        break;
                    case Player.STATE_ENDED:
                        //完成
                        pausePlay();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(getActivity(),"播放音频出错～");
            }
        });
    }

    /*****************音频播放*******************/
    //播放音频
    private void startPlay(String audioUrl){
        pausePlay();

        MediaItem mediaItem = MediaItem.fromUri(audioUrl);
        audioPlayer.setMediaItem(mediaItem);
        audioPlayer.prepare();
    }

    //暂停播放
    private void pausePlay(){
        if (audioPlayer!=null&&audioPlayer.isPlaying()){
            audioPlayer.pause();
        }

        detailAdapter.refreshPlayAnim(false);
    }

    /******************回调数据********************/
    @Override
    public void showRankEvalDetailData(List<EvalRankDetailBean> list) {
        if (list!=null){
            binding.refreshLayout.finishRefresh(true);

            if (list.size()>0){
                List<EvalRankDetailBean> sortList = transAndSortData(list);
                detailAdapter.refreshData(sortList);
            }else {
                ToastUtil.showToast(getActivity(),"暂无该用户的评测数据");
            }
        }else {
            ToastUtil.showToast(getActivity(),"获取该用户的评测数据失败～");
        }
    }

    @Override
    public void refreshAgreeData(boolean isSuccess) {
        stopLoading();

        if (isSuccess){
            //刷新数据
            presenter.getRankDetailData(types,voaId,userId);
        }else {
            ToastUtil.showToast(getActivity(),"点赞评测句子失败，请重试～");
        }
    }

    /*****************辅助数据******************/
    //将数据补充完成，并且排序完成
    private List<EvalRankDetailBean> transAndSortData(List<EvalRankDetailBean> list){
        List<EvalRankDetailBean> newList = new ArrayList<>();

        //获取本地数据库章节内容进行处理
        Map<String,String> detailMap = new HashMap<>();
        List<ChapterDetailBean> detailList = presenter.getChapterDetail(types,voaId);
        for (int i = 0; i < detailList.size(); i++) {
            ChapterDetailBean detailBean = detailList.get(i);
            detailMap.put(detailBean.getParaId()+"-"+detailBean.getIndexId(),detailBean.getSentence());
        }

        //获取评测数据进行处理
        Map<String,EvalRankDetailBean> evalMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            EvalRankDetailBean detailBean = list.get(i);
            String key = detailBean.getParaid()+"-"+detailBean.getIdIndex();
            if (evalMap.get(key)==null){//这里避免多个相同的key数据显示
                evalMap.put(key,detailBean);
            }
        }

        //合成数据
        Map<String,EvalRankDetailBean> margeMap = new HashMap<>();
        for (String key:evalMap.keySet()){
            String sentence = detailMap.get(key);
            EvalRankDetailBean detailBean = evalMap.get(key);
            if (sentence!=null){
                detailBean.setSentence(sentence);
            }
            margeMap.put(key,detailBean);
        }

        for (String key:margeMap.keySet()){
            EvalRankDetailBean detailBean = margeMap.get(key);
            if (detailBean!=null){
                newList.add(detailBean);
            }
        }

        //把第一个放上
        String key = "0-0";
        if (evalMap.get(key)!=null){
            if (newList.size()>0){
                newList.set(0,evalMap.get(key));
            }else {
                newList.add(evalMap.get(key));
            }
        }

        return newList;
    }

    //显示加载弹窗
    private void startLoading(String msg){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.create();
        }
        loadingDialog.setMsg(msg);
        loadingDialog.show();
    }

    //关闭加载弹窗
    private void stopLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    //获取合成音频的音频链接
    private String fixMargeAudioUrl(String suffix){
        String margeAudioUrl = "";

        if (TextUtils.isEmpty(suffix)){
            return margeAudioUrl;
        }

        switch (types){
//            case TypeLibrary.BookType.junior_primary:
//            case TypeLibrary.BookType.junior_middle:
//                //中小学
//                margeAudioUrl = FixUtil.fixJuniorEvalAudioUrl(suffix);
//                break;
            case TypeLibrary.BookType.bookworm:
            case TypeLibrary.BookType.newCamstory:
            case TypeLibrary.BookType.newCamstoryColor:
                //小说
                margeAudioUrl = FixUtil.fixNovelEvalAudioUrl(suffix);
                break;
        }

        return margeAudioUrl;
    }
}
