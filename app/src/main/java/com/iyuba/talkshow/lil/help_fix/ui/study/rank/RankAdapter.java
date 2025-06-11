package com.iyuba.talkshow.lil.help_fix.ui.study.rank;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.FragmentEvalrankItemBinding;
import com.iyuba.talkshow.lil.help_fix.data.listener.OnSimpleClickListener;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Eval_rank;
import com.iyuba.talkshow.lil.help_fix.util.FixUtil;
import com.iyuba.talkshow.lil.help_fix.util.ImageUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @title:
 * @date: 2023/5/25 10:47
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class RankAdapter extends RecyclerView.Adapter<RankAdapter.RankHolder> {

    private Context context;
    private List<Eval_rank.DataBean> list;

    public RankAdapter(Context context, List<Eval_rank.DataBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RankHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentEvalrankItemBinding binding = FragmentEvalrankItemBinding.inflate(LayoutInflater.from(context),parent,false);
        return new RankHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RankHolder holder, int position) {
        if (holder==null){
            return;
        }

        Eval_rank.DataBean dataBean = list.get(position);
        if (position==0){
            holder.indexPic.setVisibility(View.VISIBLE);
            holder.indexText.setVisibility(View.GONE);
            holder.indexPic.setImageResource(R.drawable.rank_first);
        }else if (position==1){
            holder.indexPic.setVisibility(View.VISIBLE);
            holder.indexText.setVisibility(View.GONE);
            holder.indexPic.setImageResource(R.drawable.rank_second);
        }else if (position==2){
            holder.indexPic.setVisibility(View.VISIBLE);
            holder.indexText.setVisibility(View.GONE);
            holder.indexPic.setImageResource(R.drawable.rank_third);
        }else {
            holder.indexPic.setVisibility(View.INVISIBLE);
            holder.indexText.setVisibility(View.VISIBLE);
            holder.indexText.setText(String.valueOf(position+1));
        }

        if (TextUtils.isEmpty(dataBean.getImgSrc())){
            holder.userPic.setVisibility(View.GONE);
            holder.userText.setVisibility(View.VISIBLE);
            holder.userText.setText(FixUtil.getFirstChar(dataBean.getName()));
        }else {
            holder.userPic.setVisibility(View.VISIBLE);
            holder.userText.setVisibility(View.GONE);
            ImageUtil.loadCircleImg(dataBean.getImgSrc(), 0,holder.userPic);
        }

        holder.userName.setText(dataBean.getName());
        String info = "句子总数:"+dataBean.getCount()+"\n总分数:"+dataBean.getScores();
        holder.userInfo.setText(info);

        holder.itemView.setOnClickListener(v->{
            if (listener!=null){
                listener.onClick(dataBean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class RankHolder extends RecyclerView.ViewHolder{

        private ImageView indexPic;
        private TextView indexText;

        private CircleImageView userPic;
        private TextView userText;

        private TextView userName;
        private TextView userInfo;

        public RankHolder(FragmentEvalrankItemBinding binding){
            super(binding.getRoot());

            indexPic = binding.rankLogoImage;
            indexText = binding.rankLogoText;

            userPic = binding.userImage;
            userText = binding.userImageText;

            userName = binding.rankUserName;
            userInfo = binding.rankUserInfo;
        }
    }

    //刷新数据
    public void refreshData(List<Eval_rank.DataBean> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //增加数据
    public void addData(List<Eval_rank.DataBean> addList){
        this.list.addAll(addList);
        notifyDataSetChanged();
    }

    //回调
    private OnSimpleClickListener<Eval_rank.DataBean> listener;

    public void setListener(OnSimpleClickListener<Eval_rank.DataBean> listener) {
        this.listener = listener;
    }

    //获取第一个数据
    public Eval_rank.DataBean getFirstData(){
        if (list!=null&&list.size()>0){
            return list.get(0);
        }
        return null;
    }
}
