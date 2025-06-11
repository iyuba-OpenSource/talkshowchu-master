package com.iyuba.talkshow.ui.rank.listen;

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
import com.iyuba.talkshow.databinding.ItemRankBinding;
import com.iyuba.talkshow.newdata.GlideUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * Created by carl shen on 2021/7/26
 * New Primary English, new study experience.
 */
public class RankListenAdapter extends RecyclerView.Adapter<RankListenAdapter.RankViewHolder> {
    private Context mContext;
    private List<RankListenBean.DataBean> rankUserList;
    private Pattern p;
    private Matcher m;

    @Inject
    public RankListenAdapter() {
        this.rankUserList = new ArrayList<>();
    }

    public void setData(List<RankListenBean.DataBean> voalist) {
        Log.e("RankOralFragment", "setData -------- ");
        rankUserList = voalist;
    }
    public void addData(List<RankListenBean.DataBean> voalist) {
        Log.e("RankOralFragment", "addData -------- ");
        rankUserList.addAll(voalist);
    }

    @Override
    public RankViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        ItemRankBinding binder = ItemRankBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new RankViewHolder(binder);
    }

    @Override
    public void onBindViewHolder(RankViewHolder rankViewHolder, final int position) {
        RankListenBean.DataBean ru = rankUserList.get(position);
//        Log.e("RankOralFragment", "RankListAdapter ru.imgSrc " + ru.imgSrc);
        String firstChar = getFirstChar(ru.name);
        switch (ru.ranking) {
            case 1:
                rankViewHolder.rankLogoText.setVisibility(View.INVISIBLE);
                rankViewHolder.rankLogoImage.setVisibility(View.VISIBLE);
                rankViewHolder.rankLogoImage.setImageResource(R.mipmap.rank_gold);

                if (ru.imgSrc.equals("http://static1." + Constant.Web.WEB_SUFFIX + "uc_server/images/noavatar_middle.jpg")) {
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
                    GlideUtil.setImage(ru.imgSrc,mContext, R.mipmap.ic_user_default_x128,rankViewHolder.userImage);
                }
                break;
            case 2:
                rankViewHolder.rankLogoText.setVisibility(View.INVISIBLE);
                rankViewHolder.rankLogoImage.setVisibility(View.VISIBLE);
                rankViewHolder.rankLogoImage.setImageResource(R.mipmap.rank_silvery);

                if (ru.imgSrc.equals("http://static1." + Constant.Web.WEB_SUFFIX + "uc_server/images/noavatar_middle.jpg")) {
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
                    GlideUtil.setImage(ru.imgSrc,mContext, R.mipmap.ic_user_default_x128,rankViewHolder.userImage);
                }
                break;
            case 3:
                rankViewHolder.rankLogoText.setVisibility(View.INVISIBLE);
                rankViewHolder.rankLogoImage.setVisibility(View.VISIBLE);
                rankViewHolder.rankLogoImage.setImageResource(R.mipmap.rank_copper);

                if (ru.imgSrc.equals("http://static1." + Constant.Web.WEB_SUFFIX + "uc_server/images/noavatar_middle.jpg")) {
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

                    GlideUtil.setImage(ru.imgSrc,mContext, R.mipmap.ic_user_default_x128,rankViewHolder.userImage);
                }
                break;
            default:
                rankViewHolder.rankLogoImage.setVisibility(View.INVISIBLE);
                rankViewHolder.rankLogoText.setVisibility(View.VISIBLE);
                rankViewHolder.rankLogoText.setText(""+ ru.ranking);
                //这里根据名次修改大小
                if (ru.ranking>=10000){
                    rankViewHolder.rankLogoText.setTextSize(10);
                }else if (ru.ranking>=1000){
                    rankViewHolder.rankLogoText.setTextSize(12);
                }else if (ru.ranking>=100){
                    rankViewHolder.rankLogoText.setTextSize(14);
                }else{
                    rankViewHolder.rankLogoText.setTextSize(16);
                }
                rankViewHolder.rankLogoText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                rankViewHolder.rankLogoText.setSingleLine(true);
                rankViewHolder.rankLogoText.setSelected(true);
//                rankLogoText.setFocusable(true);
//                rankLogoText.setFocusableInTouchMode(true);

                if (ru.imgSrc.equals("http://static1." + Constant.Web.WEB_SUFFIX + "uc_server/images/noavatar_middle.jpg")) {
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

                    GlideUtil.setImage(ru.imgSrc,mContext, R.mipmap.ic_user_default_x128,rankViewHolder.userImage);
                }
                break;
        }

        //共同部分
        if (!TextUtils.isEmpty(ru.name) && !"none".equalsIgnoreCase(ru.name) && !"null".equalsIgnoreCase(ru.name)) {
            rankViewHolder.userName.setText(ru.name);
        }else {
            rankViewHolder.userName.setText(ru.uid + "");
        }
        rankViewHolder.userInfo.setText("文章数: " + ru.totalEssay);
        rankViewHolder.userTime.setText( ((int) ru.totalTime/60) + "分钟");
        rankViewHolder.userWord.setText("单词数: " + ru.totalWord);

//        rankViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    RankOralBean.DataBean rankUser = (RankOralBean.DataBean) rankUserList.get(position);
//                    Intent intent = new Intent();
//                    intent.putExtra("uid", rankUser.uid);
//                    intent.putExtra("voaId", curVoaId);
//                    intent.putExtra("userName", rankUser.name);
//                    intent.putExtra("userPic", rankUser.imgSrc);
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

    private String getFirstChar(String name) {
        if (TextUtils.isEmpty(name)) {
            return "";
        }
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
            userTime = itemView.rankUserTime;
            userWord = itemView.rankUserWord;
        }
    }

}
