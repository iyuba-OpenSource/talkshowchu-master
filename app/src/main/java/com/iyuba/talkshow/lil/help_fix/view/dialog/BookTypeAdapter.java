package com.iyuba.talkshow.lil.help_fix.view.dialog;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.ItemDialogMsgListBinding;
import com.iyuba.talkshow.lil.help_fix.data.listener.OnSimpleClickListener;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/9 09:31
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class BookTypeAdapter extends RecyclerView.Adapter<BookTypeAdapter.ListMsgHolder> {

    private Context context;
    private List<Pair<String,String>> list;
    private String bookType = null;

    public BookTypeAdapter(Context context, List<Pair<String, String>> list) {
        this.context = context;
        this.list = list;
        this.bookType = null;
    }

    @NonNull
    @Override
    public ListMsgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDialogMsgListBinding binding = ItemDialogMsgListBinding.inflate(LayoutInflater.from(context),parent,false);
        return new ListMsgHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ListMsgHolder holder, int position) {
        if (holder==null){
            return;
        }

        Pair<String,String> bean = list.get(position);
        holder.textView.setText(bean.second);

        //判断当前是哪个
        if (bookType.equals(bean.first)){
            holder.textView.setBackgroundResource(R.drawable.shape_corner_theme_border_10dp);
        }else {
            holder.textView.setBackgroundResource(R.drawable.shape_corner_white_border_10dp);
        }

        holder.textView.setOnClickListener(v->{
            if (listener!=null){
                listener.onClick(bean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ListMsgHolder extends RecyclerView.ViewHolder{

        private TextView textView;
        private ImageView checkView;

        public ListMsgHolder(ItemDialogMsgListBinding binding){
            super(binding.getRoot());

            textView = binding.text;
            checkView = binding.check;
            checkView.setVisibility(View.GONE);
        }
    }

    //刷新数据
    public void refreshData(String curBookType,List<Pair<String,String>> refreshList){
        this.list = refreshList;
        this.bookType = curBookType;
        notifyDataSetChanged();
    }

    //刷新数据
    public void refreshType(String curBookType){
        this.bookType = curBookType;
        notifyDataSetChanged();
    }

    //回调
    private OnSimpleClickListener<Pair<String,String>> listener;

    public void setListener(OnSimpleClickListener<Pair<String,String>> listener) {
        this.listener = listener;
    }
}
