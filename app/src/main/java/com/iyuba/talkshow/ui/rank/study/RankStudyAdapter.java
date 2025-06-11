package com.iyuba.talkshow.ui.rank.study;

import android.content.Context;
import android.text.TextUtils;
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
 * @desction:
 * @date: 2023/2/9 15:51
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class RankStudyAdapter extends RecyclerView.Adapter<RankStudyAdapter.StudyHolder> {

    private Context mContext;
    private List<RankListenBean.DataBean> rankUserList;
    private Pattern p;
    private Matcher m;

    @Inject
    public RankStudyAdapter() {
        this.rankUserList = new ArrayList<>();
    }

    @NonNull
    @Override
    public StudyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        ItemRankBinding binder = ItemRankBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new StudyHolder(binder);
    }

    @Override
    public void onBindViewHolder(@NonNull StudyHolder holder, int position) {
        RankListenBean.DataBean ru = rankUserList.get(position);
        String firstChar = getFirstChar(ru.name);
        switch (ru.ranking) {
            case 1:
                holder.rankLogoText.setVisibility(View.INVISIBLE);
                holder.rankLogoImage.setVisibility(View.VISIBLE);
                holder.rankLogoImage.setImageResource(R.mipmap.rank_gold);

                if (ru.imgSrc.equals("http://static1." + Constant.Web.WEB_SUFFIX + "uc_server/images/noavatar_middle.jpg")) {
                    holder.userImage.setVisibility(View.INVISIBLE);
                    holder.userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        holder.userImageText.setBackgroundResource(R.mipmap.rank_blue);
                    } else {
                        holder.userImageText.setBackgroundResource(R.mipmap.rank_green);
                    }
                    holder.userImageText.setText(firstChar);
                } else {
                    holder.userImage.setVisibility(View.VISIBLE);
                    holder.userImageText.setVisibility(View.INVISIBLE);
                    GlideUtil.setImage(ru.imgSrc,mContext, R.mipmap.ic_user_default_x128,holder.userImage);
                }
                break;
            case 2:
                holder.rankLogoText.setVisibility(View.INVISIBLE);
                holder.rankLogoImage.setVisibility(View.VISIBLE);
                holder.rankLogoImage.setImageResource(R.mipmap.rank_silvery);

                if (ru.imgSrc.equals("http://static1." + Constant.Web.WEB_SUFFIX + "uc_server/images/noavatar_middle.jpg")) {
                    holder.userImage.setVisibility(View.INVISIBLE);
                    holder.userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        holder.userImageText.setBackgroundResource(R.mipmap.rank_blue);
                    } else {
                        holder.userImageText.setBackgroundResource(R.mipmap.rank_green);
                    }
                    holder.userImageText.setText(firstChar);
                } else {
                    holder.userImage.setVisibility(View.VISIBLE);
                    holder.userImageText.setVisibility(View.INVISIBLE);
                    GlideUtil.setImage(ru.imgSrc,mContext, R.mipmap.ic_user_default_x128,holder.userImage);
                }
                break;
            case 3:
                holder.rankLogoText.setVisibility(View.INVISIBLE);
                holder.rankLogoImage.setVisibility(View.VISIBLE);
                holder.rankLogoImage.setImageResource(R.mipmap.rank_copper);

                if (ru.imgSrc.equals("http://static1." + Constant.Web.WEB_SUFFIX + "uc_server/images/noavatar_middle.jpg")) {
                    holder.userImage.setVisibility(View.INVISIBLE);
                    holder.userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        holder.userImageText.setBackgroundResource(R.mipmap.rank_blue);
                    } else {
                        holder.userImageText.setBackgroundResource(R.mipmap.rank_green);
                    }
                    holder.userImageText.setText(firstChar);
                } else {
                    holder.userImageText.setVisibility(View.INVISIBLE);
                    holder.userImage.setVisibility(View.VISIBLE);

                    GlideUtil.setImage(ru.imgSrc,mContext, R.mipmap.ic_user_default_x128,holder.userImage);
                }
                break;
            default:
                holder.rankLogoImage.setVisibility(View.INVISIBLE);
                holder.rankLogoText.setVisibility(View.VISIBLE);
                holder.rankLogoText.setText(""+ ru.ranking);
                holder.rankLogoText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                holder.rankLogoText.setSingleLine(true);
                holder.rankLogoText.setSelected(true);
//                rankLogoText.setFocusable(true);
//                rankLogoText.setFocusableInTouchMode(true);

                if (ru.imgSrc.equals("http://static1." + Constant.Web.WEB_SUFFIX + "uc_server/images/noavatar_middle.jpg")) {
                    holder.userImage.setVisibility(View.INVISIBLE);
                    holder.userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        holder.userImageText.setBackgroundResource(R.mipmap.rank_blue);
                    } else {
                        holder.userImageText.setBackgroundResource(R.mipmap.rank_green);
                    }
                    holder.userImageText.setText(firstChar);
                } else {
                    holder.userImageText.setVisibility(View.INVISIBLE);
                    holder.userImage.setVisibility(View.VISIBLE);

                    GlideUtil.setImage(ru.imgSrc,mContext, R.mipmap.ic_user_default_x128,holder.userImage);
                }
                break;
        }

        //共通数据
        if (!TextUtils.isEmpty(ru.name) && !"none".equalsIgnoreCase(ru.name) && !"null".equalsIgnoreCase(ru.name)) {
            holder.userName.setText(ru.name);
        }else {
            holder.userName.setText(ru.uid + "");
        }
        holder.userTime.setText("文章数: " + ru.totalEssay);
        holder.userInfo.setText("时长："+((int) ru.totalTime/60) + "分钟");
        holder.userWord.setText("单词数: " + ru.totalWord);
    }

    @Override
    public int getItemCount() {
        if (rankUserList == null) {
            return 0;
        }
        return rankUserList.size();
    }

    public void setData(List<RankListenBean.DataBean> voalist) {
        rankUserList = voalist;
    }
    public void addData(List<RankListenBean.DataBean> voalist) {
        rankUserList.addAll(voalist);
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

    class StudyHolder extends RecyclerView.ViewHolder{

        ImageView rankLogoImage, userImage;
        TextView rankLogoText, userImageText, userName;
        TextView userTime, userInfo, userWord;

        public StudyHolder(ItemRankBinding itemView){
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
