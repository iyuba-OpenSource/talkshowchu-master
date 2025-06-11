package com.iyuba.talkshow.newce.comment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.data.model.RankWork;
import com.iyuba.talkshow.data.model.Thumb;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.data.remote.ThumbsService;
import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.newdata.MyIjkPlayer;
import com.iyuba.talkshow.newdata.OnPlayStateChangedListener;
import com.iyuba.talkshow.newdata.Player;
import com.iyuba.talkshow.newdata.ShareUtils;
import com.iyuba.talkshow.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by carl shen on 2020/8/26
 * New Primary English, new study experience.
 */
public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.MyViewHolder> {
    private Context mContext;
    private boolean playingVoice = false;
    private Player mediaPlayer;
    private ImageView tempVoice;
    private int voiceCount;
    private int voiceId;
    private int uid = 0;
    private String userName = "";
    private int type;
    private String dataBean = "";
    private List<RankWork> textDetailTemp = new ArrayList<>();
    @Inject
    public CommentPresenter mPresenter;

    @Inject
    public CommentListAdapter() {
    }

    public void setUserName(int id, String bean) {
        uid = id;
        userName = bean;
        notifyDataSetChanged();
    }

    public void setImgSrc(String bean) {
        dataBean = bean;
        notifyDataSetChanged();
    }

    public void setData(List<RankWork> Comments) {
        textDetailTemp = Comments;
    }

    public void addList(List<RankWork> Comments) {
        textDetailTemp.addAll(Comments);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_item_comment, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, final int position) {
        final RankWork curItem = textDetailTemp.get(position);
        /*Glide.with(mContext)
                .load(dataBean)
                .asBitmap()
                .signature(new StringSignature(System.currentTimeMillis()+""))
                .transform(new CircleTransform(mContext))
                .placeholder(R.drawable.default_avatar)
                .into(viewHolder.image);*/
        Glide3Util.loadCircleImg(mContext,dataBean,R.drawable.default_avatar,viewHolder.image);
        if (curItem.shuoshuotype == 4) {
            viewHolder.sen_index.setBackgroundResource(R.mipmap.ic_bg_select_blue);
            viewHolder.sen_index.setText("合成");
            viewHolder.comment_score.setText(curItem.score + "分");
            viewHolder.comment_text.setVisibility(View.GONE);
        } else if (curItem.shuoshuotype == 2) {
            viewHolder.sen_index.setBackgroundResource(R.mipmap.ic_bg_select_green);
            if (curItem.paraid > 0) {
                viewHolder.sen_index.setText(curItem.paraid + "");
            } else {
                viewHolder.sen_index.setText(curItem.idIndex + "");
            }
            viewHolder.comment_score.setText(curItem.score + "分");
            viewHolder.comment_text.setVisibility(View.VISIBLE);
            List<VoaText> detail;
            if (curItem.paraid > 0) {
                detail = mPresenter.getVoaTextByParaId(curItem.TopicId, curItem.paraid);
            } else {
                detail = mPresenter.getVoaTextByParaId(curItem.TopicId, curItem.idIndex);
            }
            if (detail != null && detail.size() > 0) {
                VoaText item = detail.get(0);
                viewHolder.comment_text.setText(item.sentence().trim());
            }
        }
        Log.e("CommentActivity", "onBindViewHolder curItem.paraid " + curItem.paraid);
        Thumb thumb = mPresenter.getCommentThumb(UserInfoManager.getInstance().getUserId(), curItem.id);
        if (thumb != null && thumb.action > 0) {
            viewHolder.agreeView.setImageResource(R.mipmap.ic_agree_press);
        } else {
            viewHolder.agreeView.setImageResource(R.mipmap.ic_agree);
        }
        // 点赞部分
        viewHolder.agreeText.setText(String.valueOf(curItem.agreeCount));
        viewHolder.agreeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!UserInfoManager.getInstance().isLogin()){
                    NewLoginUtil.startToLogin(mContext);
                    return;
                }

                Thumb newThumb = mPresenter.getCommentThumb(UserInfoManager.getInstance().getUserId(), curItem.id);
                if (newThumb != null && newThumb.action > 0) {
                    ToastUtil.showToast(mContext, "您已经评论过该条了");
                    return;
                }

                viewHolder.agreeView.setImageResource(R.mipmap.ic_agree_press);
                viewHolder.agreeText.setText(String.valueOf(curItem.agreeCount + 1));
                mPresenter.doAgreeThumb(curItem.id);
            }
        });
        // 是在播放，显示动画
        if (playingVoice && curItem.id == voiceId) {
            voiceAnimation(viewHolder.comment_body_voice_icon);
        } else {// 否则停止
            viewHolder.comment_body_voice_icon.setImageResource(R.mipmap.ic_play_3);
            voiceStopAnimation(viewHolder.comment_body_voice_icon);
        }
        if (TextUtils.isEmpty(userName)) {
            viewHolder.name.setText(uid);
        } else {
            viewHolder.name.setText(userName);
        }
        viewHolder.time.setText(curItem.CreateDate.substring(0, 10));

        //分享
        viewHolder.iv_comment_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = "爱语吧语音评测";
                String siteUrl = "http://voa." + Constant.Web.WEB_SUFFIX + "voa/play.jsp?id=" + curItem.id
                        + "&addr=" + curItem.ShuoShuo + "&apptype=" + Constant.EVAL_TYPE;
                String imageUrl = App.Url.APP_ICON_URL;
                String title = viewHolder.name.getText().toString() + "在爱语吧语音评测中获得了" + curItem.score + "分";
                ShareUtils localShareUtils = new ShareUtils();
                if (ConfigData.openShare) {
                    localShareUtils.showShare(mContext, "" + curItem.id, imageUrl, siteUrl, title, content, localShareUtils.defaultPlatformActionListener);
                } else {
                    ToastUtil.showToast(mContext, "对不起，分享暂时不支持");
                }
            }
        });
        if (ConfigData.openShare) {
            viewHolder.iv_comment_share.setVisibility(View.VISIBLE);
        }else {
            viewHolder.iv_comment_share.setVisibility(View.GONE);
        }
        // 点击语音评论进行播放
        viewHolder.comment_body_voice_icon.setOnClickListener(v -> {
            pauseTextPlayer();
            playingVoice = true;//
            if (tempVoice != null) {// 播放之前先停止其他的播放
                handler.removeMessages(1);
                tempVoice.setImageResource(R.mipmap.ic_play_3);
            }
            voiceId = curItem.id;
         /*   if (type != 2)
                playVoice("http://daxue." + Constant.IYUBA_CN + "appApi/" + curItem.shuoshuo);// 播放
            else*/
            String playurl = ThumbsService.ENDPOINT + curItem.ShuoShuo;
            Log.e("CommentActivity", "onBindViewHolder playurl " + playurl);
            playVoice(playurl);
            voiceAnimation(v.findViewById(R.id.comment_body_voice_icon));// 播放的动画
        });
        viewHolder.comment_body_voice_icon.setOnLongClickListener(v -> {
            if (uid != UserInfoManager.getInstance().getUserId()) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("确认删除这个评测？");
            builder.setNegativeButton("取消", (dialog, which) -> { dialog.dismiss(); });
            builder.setPositiveButton("删除", (dialog, which) -> { dialog.dismiss();
                Log.e("CommentActivity", "setOnLongClickListener curItem.id " + curItem.id);
                mPresenter.doDeleteThumb(curItem.id);
            });
            builder.create().show();
            return true;
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return textDetailTemp.size();
    }


    private int checkAgree(String commentId, int uid) {
        return 0;
//        return new CommentAgreeOp(mContext).findDataByAll(commentId, uid);
    }

    // 播放语音
    private void playVoice(String url) {

        if (mediaPlayer == null) {
            mediaPlayer = new Player(mContext,
                    new OnPlayStateChangedListener() {
                        @Override
                        public void playSuccess() {

                        }

                        @Override
                        public void playFaild() {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void playCompletion() {
                            // TODO Auto-generated method stub
                            playingVoice = false;
                            handler.removeMessages(1, tempVoice);
                            tempVoice.setImageResource(R.mipmap.ic_play_3);

                        }

                        @Override
                        public void playPause() {

                        }

                        @Override
                        public void playStart() {

                        }

                        @Override
                        public void bufferingUpdate(int progress) {

                        }
                    });
        } else {
            stopVoices();
        }
        mediaPlayer.playUrl(url);
    }

    // 播放语音评论之前先在这里reset播放器
    public void stopVoices() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
    }

    // 播放动画，参数为要显示变化的imageview
    private void voiceAnimation(View v) {
        voiceStopAnimation(v);
        voiceCount = 0;
        tempVoice = (ImageView) v;
        handler.obtainMessage(1, tempVoice).sendToTarget();
    }

    // 停止播放动画
    private void voiceStopAnimation(View v) {
        handler.removeMessages(1, v);
    }

    private void pauseTextPlayer() {
        MyIjkPlayer player = MyIjkPlayer.getInstance();
        if (player != null && player.isPlaying()) {
            player.pause();
        }


    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    notifyDataSetChanged();
                    break;
                case 1:
                    // 通过不断切换图片来表示动画
                    if (voiceCount % 3 == 0) {
                        ((ImageView) msg.obj)
                                .setImageResource(R.mipmap.ic_play_1);
                    } else if (voiceCount % 3 == 1) {
                        ((ImageView) msg.obj)
                                .setImageResource(R.mipmap.ic_play_2);
                    } else if (voiceCount % 3 == 2) {
                        ((ImageView) msg.obj)
                                .setImageResource(R.mipmap.ic_play_3);
                    }
                    voiceCount++;
                    handler.sendMessageDelayed(handler.obtainMessage(1, msg.obj),
                            500);
                    break;
                case 2:
                    ToastUtil.showToast(mContext, "网络异常，请稍后再试");
                    break;
                case 3:
                    ToastUtil.showToast(mContext, "您已经评论过该条了");
                    break;
                case 4:
                    ToastUtil.showToast(mContext, "点赞成功");
                    break;
                case 5:
                    ToastUtil.showToast(mContext, "鄙视成功");
                    break;
            }
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;// 头像图片
        ImageView agreeView;// 点赞按钮
        ImageView againstView;// 点踩按钮
        TextView agreeText; // 多少赞
        TextView againstText; // 多少踩
        ImageView comment_body_voice_icon;// 显示正在播放的
        TextView name; // 用户名
        TextView time; // 发布时间
        TextView comment_text;
        TextView sen_index;
        TextView comment_score;
        ImageView iv_comment_share;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.comment_image);
            agreeView = itemView.findViewById(R.id.agree);
            againstView = itemView.findViewById(R.id.against);
            agreeText = itemView.findViewById(R.id.agree_text);
            againstText = itemView.findViewById(R.id.against_text);
            comment_body_voice_icon = itemView.findViewById(R.id.comment_body_voice_icon);
            name = itemView.findViewById(R.id.comment_name);
            time = itemView.findViewById(R.id.comment_time);
            comment_text = itemView.findViewById(R.id.comment_text);
            sen_index = itemView.findViewById(R.id.sen_index);
            comment_score = itemView.findViewById(R.id.comment_score);
            iv_comment_share = itemView.findViewById(R.id.iv_comment_share);
        }
    }
}
