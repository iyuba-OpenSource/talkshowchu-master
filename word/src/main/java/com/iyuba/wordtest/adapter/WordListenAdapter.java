package com.iyuba.wordtest.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.wordtest.R;
import com.iyuba.wordtest.databinding.ItemWordListenBinding;
import com.iyuba.wordtest.entity.TalkShowListen;
import com.iyuba.wordtest.ui.listen.bean.WordListenShowBean;

import java.util.List;

/**
 * @desction:
 * @date: 2023/2/8 16:06
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class WordListenAdapter extends RecyclerView.Adapter<WordListenAdapter.ListenHolder> {

    private Context context;
    private List<WordListenShowBean> list;

    public WordListenAdapter(Context context, List<WordListenShowBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ListenHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWordListenBinding binding = ItemWordListenBinding.inflate(LayoutInflater.from(context),parent,false);
        return new ListenHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ListenHolder holder, int position) {
        if (holder==null){
            return;
        }

        //数据
        WordListenShowBean listen = list.get(position);
        //样式
        holder.word.setText(listen.getWord());
        if (listen.getStatus() == 1){
            holder.word.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }else {
            holder.word.setTextColor(context.getResources().getColor(R.color.red));
        }
        if (TextUtils.isEmpty(listen.getPorn())){
            holder.porn.setText("");
        }else {
            if (listen.getPorn().startsWith("[")){
                holder.porn.setText(listen.getPorn());
            }else {
                holder.porn.setText("["+listen.getPorn()+"]");
            }
        }
        holder.def.setText(listen.getDef());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ListenHolder extends RecyclerView.ViewHolder{

        private TextView word;
        private TextView porn;
        private TextView def;

        public ListenHolder(ItemWordListenBinding binding){
            super(binding.getRoot());

            word = binding.word;
            porn = binding.porn;
            def = binding.def;
        }
    }

    //刷新数据
    public void refreshList(List<WordListenShowBean> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }
}
