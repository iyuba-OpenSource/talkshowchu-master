package com.iyuba.wordtest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.wordtest.R;
import com.iyuba.wordtest.bean.WordClearBean;
import com.iyuba.wordtest.databinding.ItemWordClearBinding;

import java.util.List;

/**
 * 单词消消乐的适配器
 */
public class WordClearAdapter extends RecyclerView.Adapter<WordClearAdapter.ClearHolder> {

    private Context context;
    private List<WordClearBean> list;

    //第一次选中的位置
    private int oncePosition = -1;
    //第二次选中的位置
    private int secondPosition = -1;
    //重置的高度
    private int layoutHeight = 0;

    public WordClearAdapter(Context context, List<WordClearBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ClearHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWordClearBinding binding = ItemWordClearBinding.inflate(LayoutInflater.from(context),parent,false);
        return new ClearHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ClearHolder holder, int position) {
        if (holder==null){
            return;
        }

        //数据
        WordClearBean clearBean = list.get(position);
        //展示
        if (clearBean.getShowTag() == WordClearBean.TAG_SHOW_PORN){
            holder.word.setText(clearBean.getData().def);
        }else if (clearBean.getShowTag() == WordClearBean.TAG_SHOW_WORD){
            holder.word.setText(clearBean.getData().word);
        }

        if (position == oncePosition||position==secondPosition){
            holder.wordLayout.setBackgroundResource(R.drawable.shape_border_blue_10dp);
        }else {
            if (clearBean.getShowTag() == WordClearBean.TAG_SHOW_WORD){
                holder.wordLayout.setBackgroundResource(R.drawable.shape_border_black_10dp);
            }else if (clearBean.getShowTag() == WordClearBean.TAG_SHOW_PORN){
                holder.wordLayout.setBackgroundResource(R.drawable.shape_border_gray_10dp);
            }
        }

        if (clearBean.isVisible()){
            holder.wordLayout.setVisibility(View.VISIBLE);
        }else {
            if (position == oncePosition){
                holder.wordLayout.startAnimation(hideAnim());
                oncePosition = -1;
            }

            if (position == secondPosition){
                holder.wordLayout.startAnimation(hideAnim());
                secondPosition = -1;
            }

            holder.wordLayout.setVisibility(View.INVISIBLE);
        }

        if (layoutHeight!=0){
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            params.height = layoutHeight;
            holder.itemView.setLayoutParams(params);
        }

        //回调
        holder.wordLayout.setOnClickListener(v->{
            if (onItemClickListener!=null){
                onItemClickListener.onClick(position,clearBean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ClearHolder extends RecyclerView.ViewHolder{

        private CardView wordLayout;
        private TextView word;

        public ClearHolder(ItemWordClearBinding binding){
            super(binding.getRoot());

            wordLayout = binding.wordLayout;
            word = binding.word;
        }
    }

    //动画
    private Animation hideAnim(){
        Animation animation = new AlphaAnimation(1.0F,0);
        animation.setDuration(300);
        return animation;
    }

    //获取第一次选中的位置
    public int getOncePosition(){
        return oncePosition;
    }

    //重置第一次选中的位置
    public void setOncePosition(int oncePosition) {
        this.oncePosition = oncePosition;
        notifyDataSetChanged();
    }

    //获取第二次选中的位置
    public int getSecondPosition(){
        return secondPosition;
    }

    //重置第二次选中的位置
    public void setSecondPosition(int secondPosition) {
        this.secondPosition = secondPosition;
        notifyDataSetChanged();
    }

    //刷新数据
    public void refreshData(List<WordClearBean> refreshList,boolean isReset){
        this.list = refreshList;
        if (isReset){
            oncePosition = -1;
            secondPosition = -1;
        }
        notifyDataSetChanged();
    }

    //刷新高度
    public void refreshHeight(int height){
        this.layoutHeight = height;
        notifyDataSetChanged();
    }

    //接口
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onClick(int position,WordClearBean bean);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
