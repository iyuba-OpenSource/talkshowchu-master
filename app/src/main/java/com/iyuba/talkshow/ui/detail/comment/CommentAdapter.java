package com.iyuba.talkshow.ui.detail.comment;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.Comment;
import com.iyuba.talkshow.data.remote.CommentService;
import com.iyuba.talkshow.databinding.ItemTextCommentBinding;
import com.iyuba.talkshow.databinding.ItemVoiceCommentBinding;
import com.iyuba.talkshow.event.AudioEvent;
import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util;
import com.iyuba.talkshow.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.BaseViewHolder> {

    private static final int TYPE_TEXT = 1;
    private static final int TYPE_VOICE = 2;

    private List<Comment> mList;
    private CommentCallback mCallback;

    private Comment mPlayingComment;
    private MediaPlayer mMediaPlayer;

    private OnReplyListener mOnReplyListener;
    private OnItemClickListener mOnItemClickListener;

    @Inject
    public CommentAdapter() {
        this.mList = new ArrayList<>();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_TEXT:
                ItemTextCommentBinding textCommentBinding = ItemTextCommentBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent,false);
                return new TextViewHolder(textCommentBinding);
            case TYPE_VOICE:
                ItemVoiceCommentBinding voidCommentBinding = ItemVoiceCommentBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent,false);
                return new VoiceViewHolder(voidCommentBinding);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof TextViewHolder){
            holder.setItem(mList.get(position));
        }else {
            holder.setItem(mList.get(position));
            ((VoiceViewHolder)holder).setClick();
        }
    }

    public void setList(List<Comment> mList) {
        this.mList = mList;
    }

    public void setCallback(CommentCallback mCallback) {
        this.mCallback = mCallback;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mMediaPlayer = mediaPlayer;
    }

    public void setOnReplyListener(OnReplyListener onReplyListener) {
        this.mOnReplyListener = onReplyListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).shuoShuoType() == CommentService.SendComment.Param.Value.SHUOSHUO_TYPE_VOICE ? TYPE_VOICE : TYPE_TEXT;
    }

    class TextViewHolder extends BaseViewHolder {

        private final TextView tvContent;

        public TextViewHolder(ItemTextCommentBinding itemView) {
            super(itemView);
            tvContent = itemView.tvContent;
        }

        @Override
        public void setItem(Comment comment) {
            super.setItem(comment);
            tvContent.setText(comment.shuoShuo());
        }
    }

    private void playMediaComment(Comment comment) {
        mPlayingComment = comment;
        String url = Constant.Url.COMMENT_VOICE_BASE + mPlayingComment.shuoShuo();
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopMediaComment() {
        mMediaPlayer.pause();
        mMediaPlayer.seekTo(0);
    }

    class VoiceViewHolder extends BaseViewHolder {
        private final ImageView ivContent;

        public VoiceViewHolder(ItemVoiceCommentBinding itemView) {
            super(itemView);
            ivContent = itemView.ivContent;
        }

        public void setClick(){
            ivContent.setOnClickListener(v -> clickPlayVoiceComment());
        }
        void clickPlayVoiceComment() {

            EventBus.getDefault().post(new AudioEvent(AudioEvent.State.INTERRUPTED));
            mMediaPlayer.setOnCompletionListener(mp -> {
                if(mPlayingComment == mComment) {
                    mPlayingComment = null;
                }
                ((AnimationDrawable) ivContent.getDrawable()).stop();
            });

            mMediaPlayer.setOnPreparedListener(mp -> {
                mMediaPlayer.start();
                if(mPlayingComment == mComment) {
                    ((AnimationDrawable) ivContent.getDrawable()).start();
                }
            });

            if(mPlayingComment == null) {
                playMediaComment(mComment);
            } else {
                if(mPlayingComment != mComment) {
                    if(mMediaPlayer.isPlaying()) stopMediaComment();
                    playMediaComment(mComment);
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public void setItem(Comment comment) {
            super.setItem(comment);
            if (mPlayingComment == comment && mMediaPlayer.isPlaying()) {
                ((AnimationDrawable) ivContent.getDrawable()).start();
            } else {
                ((AnimationDrawable) ivContent.getDrawable()).stop();
            }
        }
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {

        protected Comment mComment;
        private final ImageView ivPhoto;
        private final ImageView vipIconIv;
        private final TextView tvUsername;
        private final TextView tvTime;
        private final TextView tvAgreeCount;
        private final TextView tvAgainstCount;

        public BaseViewHolder(ViewBinding itemView)
        {
            super(itemView.getRoot());
            if (itemView instanceof  ItemTextCommentBinding){
                ivPhoto = ((ItemTextCommentBinding) itemView).ivPhoto;
                vipIconIv = ((ItemTextCommentBinding) itemView).vipIconIv;
                tvUsername = ((ItemTextCommentBinding) itemView).tvUsername;
                tvTime = ((ItemTextCommentBinding) itemView).tvTime;
                tvAgreeCount = ((ItemTextCommentBinding) itemView).tvAgreeCount;
                tvAgainstCount = ((ItemTextCommentBinding) itemView).tvAgainstCount;
            }else {
                ivPhoto = ((ItemVoiceCommentBinding)itemView).ivPhoto;
                vipIconIv = ((ItemVoiceCommentBinding)itemView).vipIconIv;
                tvUsername = ((ItemVoiceCommentBinding)itemView).tvUsername;
                tvTime = ((ItemVoiceCommentBinding)itemView).tvTime;
                tvAgreeCount = ((ItemVoiceCommentBinding)itemView).tvAgreeCount;
                tvAgainstCount = ((ItemVoiceCommentBinding)itemView).tvAgainstCount;
            }
        }

//        @OnClick(R.id.iv_photo)
        void clickPhoto() {
            mCallback.onPhotoClick(mComment);
        }

//        @OnClick(R.id.iv_agree)
        void clickAgree() {
            mCallback.onAgreeClick(mComment.id());
        }

//        @OnClick(R.id.iv_against)
        void clickAgainst() {
            mCallback.onAgainstClick(mComment.id());
        }

//        @OnClick(R.id.btn_reply)
        void clickReply() {
            if(mOnReplyListener != null) {
                mOnReplyListener.onReply(mComment.userName());
            }
        }

//        @OnClick(R.id.item_layout)
        void onItemClickListener() {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick();
            }
        }

        public void setItem(Comment comment) {
            mComment = comment;
            Context context = itemView.getContext();
            /*Glide.with(context)
                    .load(comment.imgSrc())
                    .transform(new CircleTransform(context))
                    .placeholder(R.drawable.default_avatar)
                    .into(ivPhoto);*/
            Glide3Util.loadCircleImg(context,comment.imgSrc(),R.drawable.default_avatar,ivPhoto);
            if(comment.vip() == CommentService.GetComment.Result.VIP_STATUS) {
                vipIconIv.setVisibility(View.VISIBLE);
            } else {
                vipIconIv.setVisibility(View.GONE);
            }
            tvUsername.setText(comment.userName());
            tvTime.setText(TimeUtil.dateTransform(comment.createDate()));
            tvAgreeCount.setText(String.valueOf(comment.agreeCount()));
            tvAgainstCount.setText(String.valueOf(comment.againstCount()));
        }

    }

    interface CommentCallback {
        void onPhotoClick(Comment comment);

        void onAgreeClick(int id);

        void onAgainstClick(int id);
    }

    interface OnReplyListener {
        void onReply(String targetUsername);
    }

    interface OnItemClickListener {
        void onItemClick();
    }
}
