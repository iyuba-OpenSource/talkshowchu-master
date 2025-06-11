package com.iyuba.talkshow.lil.help_fix.ui.study.word.wordTrain;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.ItemWordBreakBinding;
import com.iyuba.talkshow.lil.help_fix.data.bean.WordBean;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.data.listener.OnSimpleClickListener;

import java.util.List;

/**
 * @title:
 * @date: 2023/8/15 17:37
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordTrainAdapter extends RecyclerView.Adapter<WordTrainAdapter.TrainHolder> {

    private Context context;
    private List<WordBean> list;

    //当前标志的单词
    private String showWord = "";
    //当前选中的位置
    private int selectIndex = -1;
    //是否禁止点击
    private boolean isClicked = true;
    //需要展示的数据类型
    private String showTextType = TypeLibrary.TextShowType.CN;//单词或者释义

    public WordTrainAdapter(Context context, List<WordBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TrainHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWordBreakBinding binding = ItemWordBreakBinding.inflate(LayoutInflater.from(context),parent,false);
        return new TrainHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainHolder holder, int position) {
        if (holder==null){
            return;
        }

        WordBean wordBean = list.get(position);

        if (showTextType.equals(TypeLibrary.TextShowType.CN)){
            holder.desc.setText(wordBean.getDef());
        }else if (showTextType.equals(TypeLibrary.TextShowType.EN)){
            holder.desc.setText(wordBean.getWord());
        }

        if (wordBean.getWord().equals(showWord)){
            holder.desc.setBackgroundResource(R.drawable.answer_right);
        }else {
            if (selectIndex==position){
                holder.desc.setBackgroundResource(R.drawable.answer_wrong);
            }else {
                holder.desc.setBackgroundResource(R.drawable.wordtest_rect_default1);
            }
        }

        holder.itemView.setOnClickListener(v->{
            if (!isClicked){
                return;
            }

            if (listener!=null){
                listener.onClick(new Pair<>(position,wordBean));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class TrainHolder extends RecyclerView.ViewHolder{

        private TextView desc;

        public TrainHolder(ItemWordBreakBinding binding){
            super(binding.getRoot());

            desc = binding.desc;
        }
    }

    //刷新数据
    public void refreshData(List<WordBean> refreshList,String textType){
        this.list = refreshList;
        this.selectIndex = -1;
        this.showWord = "";
        this.isClicked = true;
        this.showTextType = textType;
        notifyDataSetChanged();
    }

    //刷新答案显示
    public void refreshAnswer(int index,String showWord){
        this.selectIndex = index;
        this.showWord = showWord;
        this.isClicked = false;
        notifyDataSetChanged();
    }

    //回调
    public OnSimpleClickListener<Pair<Integer,WordBean>> listener;

    public void setListener(OnSimpleClickListener<Pair<Integer, WordBean>> listener) {
        this.listener = listener;
    }
}
