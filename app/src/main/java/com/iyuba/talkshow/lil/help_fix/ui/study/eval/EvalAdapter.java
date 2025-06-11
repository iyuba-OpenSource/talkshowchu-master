package com.iyuba.talkshow.lil.help_fix.ui.study.eval;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.FragmentEvalItemNewBinding;
import com.iyuba.talkshow.lil.help_fix.data.bean.ChapterDetailBean;
import com.iyuba.talkshow.lil.help_fix.data.bean.EvalChapterBean;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.CommonDataManager;
import com.iyuba.talkshow.lil.help_fix.model.local.util.DBTransUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.newview.RoundProgressBar;
import com.iyuba.talkshow.util.iseutil.ResultParse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @title:
 * @date: 2023/5/24 10:30
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class EvalAdapter extends RecyclerView.Adapter<EvalAdapter.EvalHolder> {

    private Context context;
    private List<ChapterDetailBean> list;

    //当前选中的位置
    private int selectIndex = 0;
    //当前选中的句子数据
    private ChapterDetailBean selectDetailBean;
    //当前选中的样式
    private EvalHolder selectHolder;
    //当前选中的评测数据
    private EvalChapterBean selectEvalBean;

    //临时保存分享的url
    private Map<Integer,String> shareMap = new HashMap<>();

    public EvalAdapter(Context context, List<ChapterDetailBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public EvalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentEvalItemNewBinding binding = FragmentEvalItemNewBinding.inflate(LayoutInflater.from(context),parent,false);
        return new EvalHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EvalHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder==null){
            return;
        }

        //句子数据
        ChapterDetailBean detailBean = list.get(position);
        holder.index.setText(position+1+"/"+list.size());
        holder.sentenceCn.setText(detailBean.getSentenceCn());

        //评测数据
        EvalChapterBean evalBean = DBTransUtil.transEvalSingleChapterData(CommonDataManager.getEvalChapterDataFromDB(detailBean.getTypes(), detailBean.getVoaId(), detailBean.getParaId(), detailBean.getIndexId()));
        if (evalBean==null){
            holder.index.setBackgroundResource(R.drawable.index_gray);
            holder.sentence.setText(detailBean.getSentence());
            holder.eval.setVisibility(View.INVISIBLE);
            holder.publish.setVisibility(View.INVISIBLE);
            holder.score.setVisibility(View.INVISIBLE);
        }else {
            holder.index.setBackgroundResource(R.drawable.index_green);
            SpannableStringBuilder span = ResultParse.getSenResultLocal(getWordScore(evalBean.getWordList()),detailBean.getSentence());
            holder.sentence.setText(span);
            holder.publish.setVisibility(View.VISIBLE);
            holder.eval.setVisibility(View.VISIBLE);
            holder.score.setVisibility(View.VISIBLE);

            int score = (int) (evalBean.getTotalScore()*20);
            holder.score.setText(String.valueOf(score));
        }

        //分享按钮
        if (!TextUtils.isEmpty(shareMap.get(position))){
            holder.share.setVisibility(View.VISIBLE);
        }else {
            holder.share.setVisibility(View.INVISIBLE);
        }

        //选中数据
        if (selectIndex == position){
            selectDetailBean = detailBean;
            selectEvalBean = evalBean;
            selectHolder = holder;

            holder.spLine.setVisibility(View.VISIBLE);
            holder.bottomLayout.setVisibility(View.VISIBLE);
        }else {
            holder.spLine.setVisibility(View.GONE);
            holder.bottomLayout.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v->{
            if (selectIndex == position){
                return;
            }

            if (onEvalCallBackListener!=null){
                onEvalCallBackListener.switchItem(position);
            }
        });
        holder.play.setOnClickListener(v->{
            //播放
            if (onEvalCallBackListener!=null){
                long startTime = (long) (detailBean.getTiming()*1000L);
                long endTime = (long) (detailBean.getEndTiming()*1000L);

                onEvalCallBackListener.onPlayRead(startTime,endTime);
            }
        });
        holder.record.setOnClickListener(v->{
            //录音
            if (onEvalCallBackListener!=null){
                long startTime = (long) (detailBean.getTiming()*1000L);
                long endTime = (long) (detailBean.getEndTiming()*1000L);

                onEvalCallBackListener.onRecord(endTime-startTime, detailBean.getTypes(), detailBean.getVoaId(), detailBean.getParaId(), detailBean.getIndexId(), detailBean.getSentence());
            }
        });
        holder.eval.setOnClickListener(v->{
            //评测播放
            if (onEvalCallBackListener!=null){
                onEvalCallBackListener.onPlayEval(evalBean.getUrl(),evalBean.getFilepath());
            }
        });
        holder.publish.setOnClickListener(v->{
            //发布
            if (onEvalCallBackListener!=null){
                int score = (int) (evalBean.getTotalScore()*20);

                onEvalCallBackListener.onPublish(detailBean.getTypes(), detailBean.getVoaId(), detailBean.getParaId(), detailBean.getIndexId(),score,evalBean.getUrl());
            }
        });
        holder.share.setOnClickListener(v->{
            //分享
            if (onEvalCallBackListener!=null){
                int totalScore = (int) (evalBean.getTotalScore()*20);

                onEvalCallBackListener.onShare(detailBean.getSentence(),totalScore,evalBean.getUrl(),shareMap.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class EvalHolder extends RecyclerView.ViewHolder{

        private LinearLayout fixLayout;
        private TextView fixText;
        private Button fixBtn;

        private TextView index;
        private TextView sentence;
        private TextView sentenceCn;

        private ImageView spLine;
        private LinearLayout bottomLayout;
        private RoundProgressBar play;
        private RoundProgressBar record;
        private RoundProgressBar eval;
        private RoundProgressBar publish;
        private ImageView share;
        private TextView score;
        private ImageView loadView;

        public EvalHolder(FragmentEvalItemNewBinding binding){
            super(binding.getRoot());

            fixLayout = binding.wordCorrect;
            fixLayout.setVisibility(View.GONE);

            index = binding.senIndex;
            sentence = binding.senEn;
            sentenceCn = binding.senZh;

            spLine = binding.sepLine;
            bottomLayout = binding.bottomView;
            play = binding.senPlay;
            play.setCricleProgressColor(ResUtil.getInstance().getColor(R.color.colorPrimary));
            record = binding.senIRead;
            record.setCricleProgressColor(ResUtil.getInstance().getColor(R.color.colorPrimary));
            eval = binding.senReadPlaying;
            eval.setCricleProgressColor(ResUtil.getInstance().getColor(R.color.colorPrimary));
            publish = binding.senReadSend;
            share = binding.senReadCollect;
            score = binding.senReadResult;
            score.setBackgroundResource(R.drawable.circle_blue);
            loadView = binding.senReadAnim;
        }
    }

    /******************主要功能***********************/
    //刷新数据
    public void refreshData(List<ChapterDetailBean> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //刷新位置
    public void refreshIndex(int index){
        this.selectIndex = index;
        notifyDataSetChanged();
    }

    //刷新播放功能
    public void refreshReadPlay(boolean isPlay,long playedTime,long totalTime){
        if (isPlay){
            selectHolder.play.setBackgroundResource(R.mipmap.sen_stop_new);

            //数据判断
            /*totalTime = totalTime<0?0:totalTime;
            totalTime = totalTime>1?1:totalTime;

            playedTime = playedTime<0?0:playedTime;
            playedTime = playedTime>1?1:playedTime;*/

            selectHolder.play.setMax((int) totalTime);
            selectHolder.play.setProgress((int) playedTime);
        }else {
            selectHolder.play.setBackgroundResource(R.mipmap.sen_play_new);
            selectHolder.play.setProgress(0);
        }
    }

    //刷新评测功能
    public void refreshEvalPlay(boolean isPlay,long playTime,long totalTime){
        if (isPlay){
            selectHolder.eval.setMax((int) totalTime);
            selectHolder.eval.setProgress((int) playTime);
        }else {
            selectHolder.eval.setProgress(0);
        }
    }

    //刷新录音功能
    public void refreshRecord(boolean isPlay,int volumeDB){
        if (isPlay){
            selectHolder.record.setMax(100);
            selectHolder.record.setProgress(volumeDB);
        }else {
            selectHolder.record.setProgress(0);
        }
    }

    //刷新分享的数据
    public void refreshShare(String shareId){
        shareMap.put(selectIndex,shareId);
        notifyDataSetChanged();
    }

    /******************辅助功能***********************/
    //获取成绩的集合
    private String[] getWordScore(List<EvalChapterBean.WordBean> list){
        String[] scoreArray = null;
        if (list!=null&&list.size()>0){
            scoreArray = new String[list.size()];

            for (int i = 0; i < list.size(); i++) {
                EvalChapterBean.WordBean wordsBean = list.get(i);
                scoreArray[i] = wordsBean.getScore();
            }
        }
        return scoreArray;
    }

    /******************回调数据************************/
    private OnEvalCallBackListener onEvalCallBackListener;

    public interface OnEvalCallBackListener{
        //切换item
        void switchItem(int nextPosition);
        //播放原音
        void onPlayRead(long startTime,long endTime);
        //录音
        void onRecord(long time,String types,String voaId,String paraId,String idIndex,String sentence);
        //播放评测
        void onPlayEval(String playUrl,String playPath);
        //发布评测
        void onPublish(String types,String voaId,String paraId,String idIndex,int score,String evalUrl);
        //分享
        void onShare(String sentence,int totalScore,String audioUrl,String shareUrl);
    }

    public void setOnEvalCallBackListener(OnEvalCallBackListener onEvalCallBackListener) {
        this.onEvalCallBackListener = onEvalCallBackListener;
    }
}
