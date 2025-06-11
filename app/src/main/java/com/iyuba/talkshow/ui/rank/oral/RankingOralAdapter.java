package com.iyuba.talkshow.ui.rank.oral;

import android.content.Context;
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
import com.iyuba.talkshow.data.model.RankListenBean;
import com.iyuba.talkshow.data.model.RankOralBean;
import com.iyuba.talkshow.databinding.ItemRankBinding;
import com.iyuba.talkshow.newdata.GlideUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;


/**
 * Created by carl shen on 2021/7/26
 * New Primary English, new study experience.
 */
public class RankingOralAdapter extends RecyclerView.Adapter<RankingOralAdapter.RankViewHolder> {
    private Context mContext;
    private List<RankOralBean.DataBean> rankUserList;
    private Pattern p;
    private Matcher m;

    @Inject
    public RankingOralAdapter() {
        this.rankUserList = new ArrayList<>();
    }

    public void setData(List<RankOralBean.DataBean> voalist) {
        Log.e("RankFragment", "setData -------- ");
        rankUserList = voalist;
    }
    public void addData(List<RankOralBean.DataBean> voalist) {
        Log.e("RankFragment", "addData -------- ");
        rankUserList.addAll(voalist);
    }

    @NonNull
    @Override
    public RankViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        ItemRankBinding binder = ItemRankBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new RankViewHolder(binder);
    }

    @Override
    public void onBindViewHolder(RankViewHolder rankViewHolder, final int position) {
        RankOralBean.DataBean ru = rankUserList.get(position);
        Log.e("RankFragment", "RankListAdapter ru.getImgSrc() " + ru.getImgSrc());
        String firstChar;
        firstChar = getFirstChar(ru.getName());
        rankViewHolder.userWord.setVisibility(View.GONE);
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
                    } else {
                        rankViewHolder.userImageText.setBackgroundResource(R.mipmap.rank_green);
                    }
                    rankViewHolder.userImageText.setText(firstChar);
                } else {
                    rankViewHolder.userImage.setVisibility(View.VISIBLE);
                    rankViewHolder.userImageText.setVisibility(View.INVISIBLE);
                    GlideUtil.setImage(ru.getImgSrc(),mContext, R.mipmap.ic_user_default_x128,rankViewHolder.userImage);
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
                    } else {
                        rankViewHolder.userImageText.setBackgroundResource(R.mipmap.rank_green);
                    }
                    rankViewHolder.userImageText.setText(firstChar);
                } else {
                    rankViewHolder.userImage.setVisibility(View.VISIBLE);
                    rankViewHolder.userImageText.setVisibility(View.INVISIBLE);
                    GlideUtil.setImage(ru.getImgSrc(),mContext, R.mipmap.ic_user_default_x128,rankViewHolder.userImage);
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
                    } else {
                        rankViewHolder.userImageText.setBackgroundResource(R.mipmap.rank_green);
                    }
                    rankViewHolder.userImageText.setText(firstChar);
                } else {
                    rankViewHolder.userImageText.setVisibility(View.INVISIBLE);
                    rankViewHolder.userImage.setVisibility(View.VISIBLE);

                    GlideUtil.setImage(ru.getImgSrc(),mContext, R.mipmap.ic_user_default_x128,rankViewHolder.userImage);
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
                    } else {
                        rankViewHolder.userImageText.setBackgroundResource(R.mipmap.rank_green);
                    }
                    rankViewHolder.userImageText.setText(firstChar);
                } else {
                    rankViewHolder.userImageText.setVisibility(View.INVISIBLE);
                    rankViewHolder.userImage.setVisibility(View.VISIBLE);

                    GlideUtil.setImage(ru.getImgSrc(),mContext, R.mipmap.ic_user_default_x128,rankViewHolder.userImage);
                }
                break;

        }

        //共同部分
        if (!TextUtils.isEmpty(ru.getName()) && !"none".equalsIgnoreCase(ru.getName()) && !"null".equalsIgnoreCase(ru.getName())) {
            rankViewHolder.userName.setText(ru.getName());
        }else {
            rankViewHolder.userName.setText("" + ru.getUid());
        }
        rankViewHolder.userInfo.setText("总分数: " + ru.getScores());
        rankViewHolder.userWord.setVisibility(View.VISIBLE);
        rankViewHolder.userWord.setText("句子数: " + ru.getCount());
        rankViewHolder.userTime.setText("平均分："+getAverage(ru.getScores(),ru.getCount()));
//        rankViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    RankingListBean.DataBean rankUser = (RankingListBean.DataBean) rankUserList.get(position);
//                    Intent intent = new Intent();
//                    intent.putExtra("uid", rankUser.getUid());
//                    intent.putExtra("voaId", curVoaId);
//                    intent.putExtra("userName", rankUser.getName());
//                    intent.putExtra("userPic", rankUser.getImgSrc());
//                    intent.setClass(mContext, CommentActivity.class);
//                    mContext.startActivity(intent);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        if (rankUserList == null) {
            return 0;
        }
        return rankUserList.size();
    }

    private String getAverage(int score, int count) {
        if (count == 0) {
            return "0分";
        }

        double avgScore = score*1.0f/count;
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(avgScore)+"分";
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
        TextView rankLogoText, userImageText, userName;
        TextView userTime, userInfo, userWord;

        public RankViewHolder(@NonNull ItemRankBinding itemView) {
            super(itemView.getRoot());
            rankLogoImage = itemView.rankLogoImage;
            rankLogoText = itemView.rankLogoText;
            userImage = itemView.userImage;
            userImageText = itemView.userImageText;
            userName = itemView.rankUserName;
            userInfo = itemView.rankUserInfo;
            userTime = itemView.rankUserWord;
            userWord = itemView.rankUserTime;
        }
    }
}
