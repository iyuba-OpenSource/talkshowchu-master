package com.iyuba.talkshow.lil.help_fix.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.R;

import java.util.List;

/**
 * @desction:
 * @date: 2023/3/23 18:20
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class MainBottomAdapter extends RecyclerView.Adapter<MainBottomAdapter.BottomHolder> {

    private Context context;
    private List<MainBottomBean> list;

    //选中位置
    private String selectTag = "MAIN";

    public MainBottomAdapter(Context context, List<MainBottomBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BottomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View bottomView = LayoutInflater.from(context).inflate(R.layout.item_main_bottom,parent,false);
        return new BottomHolder(bottomView);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder==null){
            return;
        }

        MainBottomBean bean = list.get(position);
        holder.textView.setText(bean.getText());

        if (selectTag.equals(bean.getTag())){
            holder.imageView.setImageResource(bean.getNewResId());
            holder.textView.setTextSize(13);
            holder.textView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }else {
            holder.imageView.setImageResource(bean.getOldResId());
            holder.textView.setTextColor(Color.GRAY);
            holder.textView.setTextSize(12);
        }

        holder.itemView.setOnClickListener(v->{
            notifyDataSetChanged();

            if (onClickListener!=null){
                onClickListener.onClick(list.get(position).getTag());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class BottomHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView textView;

        public BottomHolder(View view){
            super(view);

            imageView = view.findViewById(R.id.image_headline);
            textView = view.findViewById(R.id.tv_headline);
        }
    }

    //接口
    public OnClickListener onClickListener;

    public interface OnClickListener{
        void onClick(String showTag);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    //设置当前选中的位置
    public void setIndex(String tag){
        this.selectTag = tag;
        notifyDataSetChanged();
    }
}
