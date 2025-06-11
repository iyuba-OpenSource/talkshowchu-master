package com.iyuba.talkshow.newce.study.rank;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.RankOralBean;
import com.iyuba.talkshow.databinding.FragmentEvalrankItemBinding;
import com.iyuba.talkshow.newce.comment.CommentActivity;
import com.iyuba.talkshow.newdata.GlideUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * Created by carl shen on 2020/8/26
 * New Primary English, new study experience.
 */
public class EvalrankAdapter extends RecyclerView.Adapter<EvalrankAdapter.RankViewHolder> {

    private Context mContext;
    private final List<RankOralBean.DataBean> rankUserList;
    private Pattern p;
    private Matcher m;
    private int curVoaId;

    @Inject
    public EvalrankAdapter() {
        this.rankUserList = new ArrayList<>();
    }

    public void addData(List<RankOralBean.DataBean> voalist) {
        Log.e("EvalrankFragment", "addData -------- " + voalist.size());
        if (voalist != null) {
            rankUserList.clear();
            rankUserList.addAll(voalist);
        }
        try {
            this.rankUserList.remove(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        rankUserList.clear();
    }

    @NonNull
    @Override
    public RankViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        FragmentEvalrankItemBinding binder = FragmentEvalrankItemBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new RankViewHolder(binder);
//        return new RankViewHolder(LayoutInflater.from(mContext).inflate(R.layout.fragment_evalrankitem, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(RankViewHolder rankViewHolder, @SuppressLint("RecyclerView") int position) {
        RankOralBean.DataBean ru = rankUserList.get(position);
        Log.e("EvalrankFragment", "RankListAdapter ru.getImgSrc() " + ru.getImgSrc());
        String firstChar;
        firstChar = getFirstChar(ru.getName());
        switch (ru.getRanking()) {
            case 1:
                rankViewHolder.rankLogoText.setVisibility(View.INVISIBLE);
                rankViewHolder.rankLogoImage.setVisibility(View.VISIBLE);
                rankViewHolder.rankLogoImage.setImageResource(R.mipmap.rank_gold);

                if (ru.getImgSrc().equals("http://static1." + Constant.Web.WEB_SUFFIX + "uc_server/images/noavatar_middle.jpg")) {
                    rankViewHolder.userImage.setVisibility(View.INVISIBLE);
                    rankViewHolder.userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        rankViewHolder.userImageText.setBackgroundResource(R.mipmap.rank_blue);
                        rankViewHolder.userImageText.setText(firstChar);
                        if (ru.getName() != null && !"".equals(ru.getName()) && !"none".equals(ru.getName())
                                && !"null".equals(ru.getName()))
                            rankViewHolder.userName.setText(ru.getName());
                        else
                            rankViewHolder.userName.setText(ru.getUid() + "");
                        rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    } else {
                        rankViewHolder.userImageText.setBackgroundResource(R.mipmap.rank_green);
                        rankViewHolder.userImageText.setText(firstChar);
                        if (ru.getName() != null && !"".equals(ru.getName()) && !"none".equals(ru.getName())
                                && !"null".equals(ru.getName()))
                            rankViewHolder.userName.setText(ru.getName());
                        else
                            rankViewHolder.userName.setText(ru.getUid() + "");
                        rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    }
                } else {
                    rankViewHolder.userImage.setVisibility(View.VISIBLE);
                    rankViewHolder.userImageText.setVisibility(View.INVISIBLE);
                    GlideUtil.setImage(ru.getImgSrc(),mContext,R.mipmap.ic_user_default_x128,rankViewHolder.userImage);
                    if (ru.getName() != null && !"".equals(ru.getName()))
                        rankViewHolder.userName.setText(ru.getName());
                    else
                        rankViewHolder.userName.setText(ru.getUid() + "");
                    rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                }
                break;
            case 2:
                rankViewHolder.rankLogoText.setVisibility(View.INVISIBLE);
                rankViewHolder.rankLogoImage.setVisibility(View.VISIBLE);
                rankViewHolder.rankLogoImage.setImageResource(R.mipmap.rank_silvery);

                if (ru.getImgSrc().equals("http://static1." + Constant.Web.WEB_SUFFIX + "uc_server/images/noavatar_middle.jpg")) {
                    rankViewHolder.userImage.setVisibility(View.INVISIBLE);
                    rankViewHolder.userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        rankViewHolder.userImageText.setBackgroundResource(R.mipmap.rank_blue);
                        rankViewHolder.userImageText.setText(firstChar);
                        if (ru.getName() != null && !"".equals(ru.getName()) && !"none".equals(ru.getName())
                                && !"null".equals(ru.getName()))
                            rankViewHolder.userName.setText(ru.getName());
                        else
                            rankViewHolder.userName.setText(ru.getUid() + "");
                        rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    } else {
                        rankViewHolder.userImageText.setBackgroundResource(R.mipmap.rank_green);
                        rankViewHolder.userImageText.setText(firstChar);
                        if (ru.getName() != null && !"".equals(ru.getName()) && !"none".equals(ru.getName())
                                && !"null".equals(ru.getName()))
                            rankViewHolder.userName.setText(ru.getName());
                        else
                            rankViewHolder.userName.setText(ru.getUid() + "");
                        rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    }
                } else {
                    rankViewHolder.userImage.setVisibility(View.VISIBLE);
                    rankViewHolder.userImageText.setVisibility(View.INVISIBLE);
                    GlideUtil.setImage(ru.getImgSrc(),mContext,R.mipmap.ic_user_default_x128,rankViewHolder.userImage);

                    if (ru.getName() != null && !"".equals(ru.getName()) && !"none".equals(ru.getName())
                            && !"null".equals(ru.getName()))
                        rankViewHolder.userName.setText(ru.getName());
                    else
                        rankViewHolder.userName.setText(ru.getUid() + "");
                    rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                }
                break;
            case 3:
                rankViewHolder.rankLogoText.setVisibility(View.INVISIBLE);
                rankViewHolder.rankLogoImage.setVisibility(View.VISIBLE);
                rankViewHolder.rankLogoImage.setImageResource(R.mipmap.rank_copper);

                if (ru.getImgSrc().equals("http://static1." + Constant.Web.WEB_SUFFIX + "uc_server/images/noavatar_middle.jpg")) {
                    rankViewHolder.userImage.setVisibility(View.INVISIBLE);
                    rankViewHolder.userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        rankViewHolder.userImageText.setBackgroundResource(R.mipmap.rank_blue);
                        rankViewHolder.userImageText.setText(firstChar);
                        if (ru.getName() != null && !"".equals(ru.getName()) && !"none".equals(ru.getName())
                                && !"null".equals(ru.getName()))
                            rankViewHolder.userName.setText(ru.getName());
                        else
                            rankViewHolder.userName.setText(ru.getUid() + "");
                        rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    } else {
                        rankViewHolder.userImageText.setBackgroundResource(R.mipmap.rank_green);
                        rankViewHolder.userImageText.setText(firstChar);
                        if (ru.getName() != null && !"".equals(ru.getName()) && !"none".equals(ru.getName())
                                && !"null".equals(ru.getName()))
                            rankViewHolder.userName.setText(ru.getName());
                        else
                            rankViewHolder.userName.setText(ru.getUid() + "");
                        rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    }
                } else {
                    rankViewHolder.userImageText.setVisibility(View.INVISIBLE);
                    rankViewHolder.userImage.setVisibility(View.VISIBLE);

                    GlideUtil.setImage(ru.getImgSrc(),mContext,R.mipmap.ic_user_default_x128,rankViewHolder.userImage);

                    if (ru.getName() != null && !"".equals(ru.getName()))
                        rankViewHolder.userName.setText(ru.getName());
                    else
                        rankViewHolder.userName.setText(ru.getUid() + "");
                    rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                }
                break;
            default:
                rankViewHolder.rankLogoImage.setVisibility(View.INVISIBLE);
                rankViewHolder.rankLogoText.setVisibility(View.VISIBLE);
                rankViewHolder.rankLogoText.setText(""+ ru.getRanking());
                rankViewHolder.rankLogoText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                rankViewHolder.rankLogoText.setSingleLine(true);
                rankViewHolder.rankLogoText.setSelected(true);
//                rankLogoText.setFocusable(true);
//                rankLogoText.setFocusableInTouchMode(true);

                if (ru.getImgSrc().equals("http://static1." + Constant.Web.WEB_SUFFIX + "uc_server/images/noavatar_middle.jpg")) {
                    rankViewHolder.userImage.setVisibility(View.INVISIBLE);
                    rankViewHolder.userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        rankViewHolder.userImageText.setBackgroundResource(R.mipmap.rank_blue);
                        rankViewHolder.userImageText.setText(firstChar);
                        if (!TextUtils.isEmpty(ru.getName()) && !"none".equals(ru.getName())
                                && !"null".equals(ru.getName()))
                            rankViewHolder.userName.setText(ru.getName());
                        else
                            rankViewHolder.userName.setText(ru.getUid() + "");
                        rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    } else {
                        rankViewHolder.userImageText.setBackgroundResource(R.mipmap.rank_green);
                        rankViewHolder.userImageText.setText(firstChar);
                        if (!TextUtils.isEmpty(ru.getName()) && !"none".equals(ru.getName())
                                && !"null".equals(ru.getName()))
                            rankViewHolder.userName.setText(ru.getName());
                        else
                            rankViewHolder.userName.setText(ru.getUid() + "");
                        rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    }
                } else {
                    rankViewHolder.userImageText.setVisibility(View.INVISIBLE);
                    rankViewHolder.userImage.setVisibility(View.VISIBLE);

                    GlideUtil.setImage(ru.getImgSrc(),mContext,R.mipmap.ic_user_default_x128,rankViewHolder.userImage);
                    if (!TextUtils.isEmpty(ru.getName()) && !"none".equals(ru.getName())
                            && !"null".equals(ru.getName()))
                        rankViewHolder.userName.setText(ru.getName());
                    else
                        rankViewHolder.userName.setText("" + ru.getUid() + "");
                    rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                }
                break;

        }
        rankViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    RankOralBean.DataBean rankUser = rankUserList.get(position);
                    Intent intent = new Intent();
                    intent.putExtra("uid", rankUser.getUid());
                    intent.putExtra("voaId", curVoaId);
                    intent.putExtra("userName", rankUser.getName());
                    intent.putExtra("userPic", rankUser.getImgSrc());
                    intent.setClass(mContext, CommentActivity.class);
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rankUserList.size();
    }

    private String getFirstChar(String name) {
        String subString;
        for (int i = 0; i < name.length(); i++) {
            subString = name.substring(i, i + 1);

            p = Pattern.compile("[0-9]*");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是数字", Toast.LENGTH_SHORT).show();
                return subString;
            }

            p = Pattern.compile("[a-zA-Z]");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                return subString;
            }

            p = Pattern.compile("[\u4e00-\u9fa5]");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是汉字", Toast.LENGTH_SHORT).show();
                return subString;
            }
        }

        return "A";
    }


    class RankViewHolder extends RecyclerView.ViewHolder {
        ImageView rankLogoImage, userImage;
        TextView rankLogoText, userImageText, userName, userInfo;

        public RankViewHolder(@NonNull FragmentEvalrankItemBinding itemView) {
            super(itemView.getRoot());
            rankLogoImage = itemView.rankLogoImage;
            rankLogoText = itemView.rankLogoText;
            userImage = itemView.userImage;
            userImageText = itemView.userImageText;
            userName = itemView.rankUserName;
            userInfo = itemView.rankUserInfo;
        }
    }

    public void setCurVoaId(int voaId) {
        this.curVoaId = voaId;
    }
}
