package com.iyuba.talkshow.lil.help_fix.ui.study.rank.rank_detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.ActivityItemCommentBinding;
import com.iyuba.talkshow.lil.help_fix.data.bean.EvalRankDetailBean;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.CommonDataManager;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.AgreeEntity;
import com.iyuba.talkshow.lil.help_fix.util.ImageUtil;
import com.iyuba.talkshow.lil.help_mvp.util.DateUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/25 15:45
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class RankDetailAdapter extends RecyclerView.Adapter<RankDetailAdapter.RankDetailHolder> {

    private Context context;
    private List<EvalRankDetailBean> list;

    //头像
    private String userPicUrl;
    //名称
    private String userName;
    //查询的id
    private String userId;

    //选中的位置
    private int selectIndex = -1;
    //选中的位置
    private RankDetailHolder selectHolder;
    //是否是数据刷新操作
    private boolean isDataRefresh = false;

    public RankDetailAdapter(Context context, List<EvalRankDetailBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RankDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityItemCommentBinding binding = ActivityItemCommentBinding.inflate(LayoutInflater.from(context),parent,false);
        return new RankDetailHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RankDetailHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder==null){
            return;
        }

        //禁止复用
        holder.setIsRecyclable(false);

        EvalRankDetailBean detailBean = list.get(position);
        ImageUtil.loadCircleImg(userPicUrl,0,holder.userPic);
        holder.userName.setText(userName);
        if (TextUtils.isEmpty(detailBean.getSentence())){
            holder.sentence.setText("");
        }else {
            holder.sentence.setText(detailBean.getSentence());
        }
        if (detailBean.getShuoshuotype()==4){
            holder.index.setBackgroundResource(R.mipmap.ic_bg_select_green);
            holder.index.setText("合成");
        }else if (detailBean.getShuoshuotype()==2){
            holder.index.setBackgroundResource(R.mipmap.ic_bg_select_blue);
            holder.index.setText(String.valueOf(position+1));
        }

        holder.share.setVisibility(View.VISIBLE);

        holder.itemTime.setText(getShowTime(detailBean.getCreateDate()));
        holder.agreeText.setText(String.valueOf(detailBean.getAgreeCount()));
        holder.score.setText(String.valueOf(detailBean.getScore()));
        holder.audio.setImageResource(R.mipmap.ic_play_3);

        //点赞查询
        AgreeEntity agreeData = CommonDataManager.getAgreeDataFromDB(String.valueOf(UserInfoManager.getInstance().getUserId()), userId,detailBean.getTypes(),detailBean.getVoaId(),String.valueOf(detailBean.getId()));
        if (agreeData!=null){
            holder.agreePic.setImageResource(R.mipmap.ic_agree_press);
            if (isDataRefresh){
                holder.agreeText.setText(String.valueOf(detailBean.getAgreeCount()));
            }else {
                holder.agreeText.setText(String.valueOf(detailBean.getAgreeCount()+1));
            }
        }else {
            holder.agreePic.setImageResource(R.mipmap.ic_agree);
        }

        holder.agreePic.setOnClickListener(v->{
            isDataRefresh = false;

            if (onRankDetailCallBackListener!=null){
                onRankDetailCallBackListener.onAgree(detailBean);
            }
        });
        holder.audio.setOnClickListener(v->{
            boolean isSame = false;
            if (selectIndex==position){
                isSame = true;
            }

            selectIndex = position;
            selectHolder = holder;

            if (onRankDetailCallBackListener!=null){
                onRankDetailCallBackListener.playAudio(isSame,detailBean);
            }
        });
        holder.share.setOnClickListener(v->{
            if (onRankDetailCallBackListener!=null){
                onRankDetailCallBackListener.onShare(detailBean);
            }
        });


        //这里根据类型判断
        //新概念、中小学存在分享，但是小说系列没有这个分享
        //点赞是通用的，这个没啥问题
        if (detailBean.getTypes().equals(TypeLibrary.BookType.bookworm)
                ||detailBean.getTypes().equals(TypeLibrary.BookType.newCamstory)
                ||detailBean.getTypes().equals(TypeLibrary.BookType.newCamstoryColor)){
            holder.share.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class RankDetailHolder extends RecyclerView.ViewHolder{

        private ImageView userPic;
        private TextView userName;
        private ImageView audio;
        private TextView itemTime;
        private TextView index;
        private TextView sentence;

        private TextView score;
        private ImageView agreePic;
        private TextView agreeText;
        private ImageView share;

        public RankDetailHolder(ActivityItemCommentBinding binding){
            super(binding.getRoot());

            userPic = binding.commentImage;
            userName = binding.commentName;
            audio = binding.commentBodyVoiceIcon;
            itemTime = binding.commentTime;
            index = binding.senIndex;
            sentence = binding.commentText;

            score = binding.commentScore;
            agreePic = binding.agree;
            agreeText = binding.agreeText;
            share = binding.ivCommentShare;
        }
    }

    //刷新数据
    public void refreshData(List<EvalRankDetailBean> refreshList){
        isDataRefresh = true;

        this.list = refreshList;
        notifyDataSetChanged();
    }

    //刷新播放动画
    public void refreshPlayAnim(boolean isPlay){
        if (selectHolder==null){
            return;
        }

        if (isPlay){
            selectHolder.audio.setImageResource(R.drawable.anim_rank_detail_play);
            AnimationDrawable animation = (AnimationDrawable) selectHolder.audio.getDrawable();
            animation.start();
        }else {
            selectHolder.audio.setImageResource(R.mipmap.ic_play_3);
        }
    }

    //设置用户名称和头像
    public void refreshUserNameAndPic(String userName,String userPic,String userId){
        this.userName = userName;
        this.userPicUrl = userPic;
        this.userId = userId;
    }

    //获取时间显示
    private String getShowTime(String time){
        //将时间转换成long型
        long showTimeLong = DateUtil.toDateLong(time,DateUtil.YMDHMS);
        //转成ymd样式
        String showTimeStr = DateUtil.toDateStr(showTimeLong,DateUtil.YMD);
        //和今天进行核对
        String curTimeStr = DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMD);
        if (showTimeStr.equals(curTimeStr)){
            //换成hm样式
            return DateUtil.toDateStr(showTimeLong,DateUtil.HM);
        }else {
            return showTimeStr;
        }
    }

    //回调接口
    private OnRankDetailCallBackListener onRankDetailCallBackListener;

    public interface OnRankDetailCallBackListener{
        //音频
        void playAudio(boolean isSame,EvalRankDetailBean detailBean);
        //点赞
        void onAgree(EvalRankDetailBean detailBean);
        //分享
        void onShare(EvalRankDetailBean detailBean);
    }

    public void setOnRankDetailCallBackListener(OnRankDetailCallBackListener onRankDetailCallBackListener) {
        this.onRankDetailCallBackListener = onRankDetailCallBackListener;
    }
}
