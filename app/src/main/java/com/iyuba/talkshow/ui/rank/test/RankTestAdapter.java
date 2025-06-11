package com.iyuba.talkshow.ui.rank.test;

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
import com.iyuba.talkshow.data.model.RankTestBean;
import com.iyuba.talkshow.databinding.ItemRankBinding;
import com.iyuba.talkshow.newdata.GlideUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * @desction:
 * @date: 2023/2/9 17:38
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class RankTestAdapter extends RecyclerView.Adapter<RankTestAdapter.TestHolder> {

    private Context context;
    private List<RankTestBean.DataBean> list;
    private Pattern p;
    private Matcher m;

    @Inject
    public RankTestAdapter(){
        this.list = new ArrayList<>();
    }

    public void setData(List<RankTestBean.DataBean> voalist) {
        list = voalist;
    }
    public void addData(List<RankTestBean.DataBean> voalist) {
        list.addAll(voalist);
    }

    @NonNull
    @Override
    public TestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ItemRankBinding binding = ItemRankBinding.inflate(LayoutInflater.from(context),parent,false);
        return new TestHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TestHolder holder, int position) {
        RankTestBean.DataBean dataBean = list.get(position);
        String firstChar;
        firstChar = getFirstChar(dataBean.getName());
        holder.userWord.setVisibility(View.GONE);
        switch (dataBean.getRanking()) {
            case 1:
                holder.rankLogoText.setVisibility(View.INVISIBLE);
                holder.rankLogoImage.setVisibility(View.VISIBLE);
                holder.rankLogoImage.setImageResource(R.mipmap.rank_gold);

                if (dataBean.getImgSrc().equals("http://static1." + Constant.Web.WEB_SUFFIX + "uc_server/images/noavatar_middle.jpg")) {
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
                    GlideUtil.setImage(dataBean.getImgSrc(),context, R.mipmap.ic_user_default_x128,holder.userImage);
                }
                break;
            case 2:
                holder.rankLogoText.setVisibility(View.INVISIBLE);
                holder.rankLogoImage.setVisibility(View.VISIBLE);
                holder.rankLogoImage.setImageResource(R.mipmap.rank_silvery);

                if (dataBean.getImgSrc().equals("http://static1." + Constant.Web.WEB_SUFFIX + "uc_server/images/noavatar_middle.jpg")) {
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
                    GlideUtil.setImage(dataBean.getImgSrc(),context, R.mipmap.ic_user_default_x128,holder.userImage);
                }
                break;
            case 3:
                holder.rankLogoText.setVisibility(View.INVISIBLE);
                holder.rankLogoImage.setVisibility(View.VISIBLE);
                holder.rankLogoImage.setImageResource(R.mipmap.rank_copper);

                if (dataBean.getImgSrc().equals("http://static1." + Constant.Web.WEB_SUFFIX + "uc_server/images/noavatar_middle.jpg")) {
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

                    GlideUtil.setImage(dataBean.getImgSrc(),context, R.mipmap.ic_user_default_x128,holder.userImage);
                }
                break;
            default:
                holder.rankLogoImage.setVisibility(View.INVISIBLE);
                holder.rankLogoText.setVisibility(View.VISIBLE);
                holder.rankLogoText.setText(""+ dataBean.getRanking());
                holder.rankLogoText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                holder.rankLogoText.setSingleLine(true);
                holder.rankLogoText.setSelected(true);
//                rankLogoText.setFocusable(tdataBeane);
//                rankLogoText.setFocusableInTouchMode(tdataBeane);

                if (dataBean.getImgSrc().equals("http://static1." + Constant.Web.WEB_SUFFIX + "uc_server/images/noavatar_middle.jpg")) {
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

                    GlideUtil.setImage(dataBean.getImgSrc(),context, R.mipmap.ic_user_default_x128,holder.userImage);
                }
                break;

        }

        //共同部分
        if (!TextUtils.isEmpty(dataBean.getName()) && !"none".equalsIgnoreCase(dataBean.getName()) && !"null".equalsIgnoreCase(dataBean.getName())) {
            holder.userName.setText(dataBean.getName());
        }else {
            holder.userName.setText("" + dataBean.getUid());
        }
        holder.userInfo.setText("正确数: " + dataBean.getTotalRight());
        holder.userTime.setText("正确率："+rightRate(dataBean.getTotalRight(),dataBean.getTotalTest()));
        holder.userWord.setVisibility(View.VISIBLE);
        holder.userWord.setText("做题数: " + dataBean.getTotalTest());
    }

    private String rightRate(int right,int total){
        if (right==0||total==0){
            return "0";
        }

        float rate = right*1.0f/total;
        DecimalFormat df = new DecimalFormat("0.00");
        return (int)(Double.parseDouble(df.format(rate))*100)+"%";
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    private String getFirstChar(String name) {
        String subString;
        if (!TextUtils.isEmpty(name)){
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
        }

        return "A";
    }

    class TestHolder extends RecyclerView.ViewHolder{

        ImageView rankLogoImage, userImage;
        TextView rankLogoText, userImageText, userName;
        TextView userTime, userInfo, userWord;

        public TestHolder(ItemRankBinding itemView){
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
