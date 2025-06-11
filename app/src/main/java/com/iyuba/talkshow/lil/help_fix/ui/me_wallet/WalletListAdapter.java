package com.iyuba.talkshow.lil.help_fix.ui.me_wallet;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.databinding.ItemWalletListBinding;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Reward_history;
import com.iyuba.talkshow.lil.help_mvp.util.BigDecimalUtil;
import com.iyuba.talkshow.lil.help_mvp.util.DateUtil;

import java.util.List;

/**
 * @title:
 * @date: 2023/8/23 08:58
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WalletListAdapter extends RecyclerView.Adapter<WalletListAdapter.WalletListHolder> {

    private Context context;
    private List<Reward_history> list;

    public WalletListAdapter(Context context, List<Reward_history> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public WalletListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWalletListBinding binding = ItemWalletListBinding.inflate(LayoutInflater.from(context),parent,false);
        return new WalletListHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletListHolder holder, int position) {
        if (holder==null){
            return;
        }

        Reward_history history = list.get(position);
        holder.type.setText(history.getType());
        //变更单位为元
        int rewardInt = TextUtils.isEmpty(history.getScore())?0:Integer.parseInt(history.getScore());
        //显示金额(增加+-号)
        double reward = BigDecimalUtil.trans2Double(rewardInt*0.01f);
        String showReward = reward>0?String.valueOf("+"+reward):String.valueOf(reward);
        holder.reward.setText(showReward);
        //变更时间显示（如果是当天的，则显示今天xx；如果不是，则显示日期+时间）
        long time = DateUtil.toDateLong(history.getTime(),DateUtil.YMDHMSS);
        long curStartTime = getTodayStartTime();
        long curEndTime = curStartTime+24*60*60*1000;
        String showTime = DateUtil.toDateStr(time,DateUtil.YMD);
        if (time>curStartTime&&time<curEndTime){
            showTime = "今天"+DateUtil.toDateStr(time,DateUtil.HM);
        }
        holder.time.setText(showTime);

        holder.itemView.setOnClickListener(v->{
            String showMsg = "类型："+history.getType()+"\n金额："+reward+"元\n时间："+holder.time.getText().toString();
            new AlertDialog.Builder(context)
                    .setTitle("奖励信息")
                    .setMessage(showMsg)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class WalletListHolder extends RecyclerView.ViewHolder{

        private TextView type;
        private TextView reward;
        private TextView time;

        public WalletListHolder(ItemWalletListBinding binding){
            super(binding.getRoot());

            type = binding.type;
            reward = binding.reward;
            time = binding.time;
        }
    }

    //刷新数据
    public void refreshData(List<Reward_history> refreshList,boolean isAdd){
        if (isAdd){
            this.list.addAll(refreshList);
        }else {
            this.list = refreshList;
        }
        notifyDataSetChanged();
    }

    //获取今天的开始时间
    private long getTodayStartTime(){
        String todayDate = DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMD);
        long todayTime = DateUtil.toDateLong(todayDate,DateUtil.YMD);
        return todayTime;
    }
}
